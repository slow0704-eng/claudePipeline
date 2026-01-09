package com.board.service;

import com.board.entity.Topic;
import com.board.entity.TopicMergeHistory;
import com.board.entity.BoardTopic;
import com.board.repository.TopicRepository;
import com.board.repository.BoardTopicRepository;
import com.board.repository.TopicMergeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final BoardTopicRepository boardTopicRepository;
    private final TopicMergeHistoryRepository mergeHistoryRepository;

    private static final int MAX_LEVEL = 2; // 0, 1, 2 = 3 levels total

    // ========== CRUD Operations ==========

    @Transactional
    public Topic createTopic(String name, String description, Long parentId,
                            String icon, String color, Integer displayOrder) {
        // Validate name uniqueness
        String normalizedName = name.toLowerCase().trim();
        if (topicRepository.existsByName(normalizedName)) {
            throw new RuntimeException("이미 존재하는 토픽명입니다: " + name);
        }

        Topic topic = new Topic();
        topic.setName(normalizedName);
        topic.setDescription(description);
        topic.setParentId(parentId);
        topic.setIcon(icon);
        topic.setColor(color);
        topic.setDisplayOrder(displayOrder != null ? displayOrder : 0);
        topic.setEnabled(true);

        // Calculate level and validate hierarchy
        if (parentId == null) {
            topic.setLevel(0);
        } else {
            Topic parent = getTopicById(parentId);
            int newLevel = parent.getLevel() + 1;

            if (newLevel > MAX_LEVEL) {
                throw new RuntimeException("최대 3단계까지만 생성할 수 있습니다.");
            }

            topic.setLevel(newLevel);
        }

        return topicRepository.save(topic);
    }

    public Topic getTopicById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("토픽을 찾을 수 없습니다: " + id));
    }

    @Transactional
    public Topic updateTopic(Long id, String name, String description, Long parentId,
                            String icon, String color, Integer displayOrder, Boolean enabled) {
        Topic topic = getTopicById(id);

        // Check name uniqueness if changed
        String normalizedName = name.toLowerCase().trim();
        if (!topic.getName().equals(normalizedName)) {
            if (topicRepository.existsByName(normalizedName)) {
                throw new RuntimeException("이미 존재하는 토픽명입니다: " + name);
            }
            topic.setName(normalizedName);
        }

        topic.setDescription(description);
        topic.setIcon(icon);
        topic.setColor(color);

        if (displayOrder != null) {
            topic.setDisplayOrder(displayOrder);
        }
        if (enabled != null) {
            topic.setEnabled(enabled);
        }

        // Handle parent change and validate circular reference
        if (parentId != null && !parentId.equals(topic.getParentId())) {
            validateNoCircularReference(id, parentId);

            Topic parent = getTopicById(parentId);
            int newLevel = parent.getLevel() + 1;

            if (newLevel > MAX_LEVEL) {
                throw new RuntimeException("최대 3단계까지만 생성할 수 있습니다.");
            }

            topic.setParentId(parentId);
            topic.setLevel(newLevel);

            // Update children levels recursively
            updateChildrenLevels(id, newLevel);
        } else if (parentId == null && topic.getParentId() != null) {
            topic.setParentId(null);
            topic.setLevel(0);
            updateChildrenLevels(id, 0);
        }

        return topicRepository.save(topic);
    }

    @Transactional
    public void deleteTopic(Long id) {
        Topic topic = getTopicById(id);

        // Check if has children
        List<Topic> children = topicRepository.findByParentIdOrderByDisplayOrderAsc(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("하위 토픽이 있어 삭제할 수 없습니다. 먼저 하위 토픽을 삭제해주세요.");
        }

        // Check if used in boards
        long usageCount = boardTopicRepository.countByTopicId(id);
        if (usageCount > 0) {
            throw new RuntimeException("이 토픽을 사용하는 게시글이 " + usageCount + "개 있습니다. 삭제할 수 없습니다.");
        }

        topicRepository.delete(topic);
    }

    // ========== Hierarchy Validation ==========

    private void validateNoCircularReference(Long topicId, Long newParentId) {
        if (topicId.equals(newParentId)) {
            throw new RuntimeException("자기 자신을 부모로 설정할 수 없습니다.");
        }

        // Check if newParent is a descendant of topic
        Long currentParentId = newParentId;
        while (currentParentId != null) {
            if (currentParentId.equals(topicId)) {
                throw new RuntimeException("순환 참조가 발생합니다.");
            }
            Topic parent = topicRepository.findById(currentParentId).orElse(null);
            if (parent == null) break;
            currentParentId = parent.getParentId();
        }
    }

    private void updateChildrenLevels(Long parentId, int parentLevel) {
        List<Topic> children = topicRepository.findByParentIdOrderByDisplayOrderAsc(parentId);
        for (Topic child : children) {
            int newLevel = parentLevel + 1;
            if (newLevel > MAX_LEVEL) {
                throw new RuntimeException("계층 구조 변경으로 인해 최대 깊이를 초과합니다.");
            }
            child.setLevel(newLevel);
            topicRepository.save(child);
            updateChildrenLevels(child.getId(), newLevel);
        }
    }

    // ========== Merge Operations ==========

    @Transactional
    public Map<String, Object> mergeTopics(Long sourceId, Long targetId, Long userId, String notes) {
        Topic source = getTopicById(sourceId);
        Topic target = getTopicById(targetId);

        if (sourceId.equals(targetId)) {
            throw new RuntimeException("같은 토픽은 병합할 수 없습니다.");
        }

        // Get all board relationships
        List<BoardTopic> sourceBoardTopics = boardTopicRepository.findByTopicId(sourceId);
        int movedCount = 0;

        for (BoardTopic bt : sourceBoardTopics) {
            // Check if target relationship already exists
            if (!boardTopicRepository.existsByBoardIdAndTopicId(bt.getBoardId(), targetId)) {
                BoardTopic newLink = new BoardTopic();
                newLink.setBoardId(bt.getBoardId());
                newLink.setTopicId(targetId);
                boardTopicRepository.save(newLink);
                movedCount++;
            }
            // Delete source relationship
            boardTopicRepository.delete(bt);
        }

        // Update statistics
        target.setUsageCount(target.getUsageCount() + source.getUsageCount());
        if (source.getLastUsedAt() != null &&
            (target.getLastUsedAt() == null || source.getLastUsedAt().isAfter(target.getLastUsedAt()))) {
            target.setLastUsedAt(source.getLastUsedAt());
        }
        topicRepository.save(target);

        // Mark source as merged
        source.setMergedIntoId(targetId);
        source.setMergedAt(LocalDateTime.now());
        source.setUsageCount(0L);
        source.setEnabled(false);
        topicRepository.save(source);

        // Create merge history
        TopicMergeHistory history = new TopicMergeHistory();
        history.setSourceTopicId(sourceId);
        history.setSourceTopicName(source.getName());
        history.setTargetTopicId(targetId);
        history.setTargetTopicName(target.getName());
        history.setMergedByUserId(userId);
        history.setBoardsAffected(movedCount);
        history.setNotes(notes);
        mergeHistoryRepository.save(history);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("sourceName", source.getName());
        result.put("targetName", target.getName());
        result.put("boardsAffected", movedCount);
        result.put("message", source.getName() + " → " + target.getName() + " 병합 완료 (" + movedCount + "개 게시글 이동)");

        return result;
    }

    // ========== Tree and Hierarchy ==========

    public List<Map<String, Object>> getTopicTree() {
        List<Topic> allTopics = topicRepository.findAllOrderByHierarchy();
        return buildTopicTree(allTopics, null);
    }

    private List<Map<String, Object>> buildTopicTree(List<Topic> allTopics, Long parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();

        for (Topic topic : allTopics) {
            boolean isMatch = (parentId == null && topic.getParentId() == null) ||
                             (parentId != null && parentId.equals(topic.getParentId()));

            if (isMatch) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", topic.getId());
                node.put("name", topic.getName());
                node.put("description", topic.getDescription());
                node.put("icon", topic.getIcon());
                node.put("color", topic.getColor());
                node.put("level", topic.getLevel());
                node.put("displayOrder", topic.getDisplayOrder());
                node.put("enabled", topic.getEnabled());
                node.put("usageCount", topic.getUsageCount());
                node.put("lastUsedAt", topic.getLastUsedAt());
                node.put("parentId", topic.getParentId());

                // Recursive children
                List<Map<String, Object>> children = buildTopicTree(allTopics, topic.getId());
                if (!children.isEmpty()) {
                    node.put("children", children);
                }

                tree.add(node);
            }
        }

        return tree;
    }

    // ========== Search and Autocomplete ==========

    public List<Map<String, Object>> searchTopics(String query) {
        List<Topic> topics = topicRepository.searchTopics(query);
        return topics.stream()
                .limit(10)
                .map(this::topicToMap)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getPopularTopics(int limit) {
        return topicRepository.findPopularTopics().stream()
                .limit(limit)
                .map(this::topicToMap)
                .collect(Collectors.toList());
    }

    // ========== Related Topics (Co-occurrence) ==========

    public List<Map<String, Object>> getRelatedTopics(Long topicId, int limit) {
        List<Object[]> relatedIds = boardTopicRepository.findRelatedTopicIds(topicId);

        return relatedIds.stream()
                .limit(limit)
                .map(result -> {
                    Long relatedId = (Long) result[0];
                    Long frequency = (Long) result[1];

                    Map<String, Object> map = new HashMap<>();
                    topicRepository.findById(relatedId).ifPresent(t -> {
                        map.put("id", t.getId());
                        map.put("name", t.getName());
                        map.put("icon", t.getIcon());
                        map.put("color", t.getColor());
                        map.put("usageCount", t.getUsageCount());
                        map.put("coOccurrence", frequency);
                    });
                    return map;
                })
                .filter(map -> !map.isEmpty())
                .collect(Collectors.toList());
    }

    // ========== Helper Methods ==========

    private Map<String, Object> topicToMap(Topic topic) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", topic.getId());
        map.put("name", topic.getName());
        map.put("description", topic.getDescription());
        map.put("icon", topic.getIcon());
        map.put("color", topic.getColor());
        map.put("level", topic.getLevel());
        map.put("usageCount", topic.getUsageCount());
        map.put("lastUsedAt", topic.getLastUsedAt());
        map.put("enabled", topic.getEnabled());
        map.put("parentId", topic.getParentId());
        return map;
    }

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public List<Topic> getActiveTopics() {
        return topicRepository.findActiveTopics();
    }

    public List<Topic> getMergedTopics() {
        return topicRepository.findMergedTopics();
    }

    @Transactional
    public Topic toggleTopicStatus(Long id) {
        Topic topic = getTopicById(id);
        topic.setEnabled(!topic.getEnabled());
        return topicRepository.save(topic);
    }

    public List<TopicMergeHistory> getAllMergeHistory() {
        return mergeHistoryRepository.findAllOrderByMergedAtDesc();
    }

    public Map<String, Object> getOverallStatistics() {
        Map<String, Object> stats = new HashMap<>();

        List<Topic> allTopics = topicRepository.findAll();
        long totalTopics = allTopics.size();
        long activeTopics = allTopics.stream().filter(t -> t.getEnabled() && t.getMergedIntoId() == null).count();
        long mergedTopics = allTopics.stream().filter(t -> t.getMergedIntoId() != null).count();
        long level0Topics = allTopics.stream().filter(t -> t.getLevel() == 0 && t.getEnabled()).count();

        Long totalUsageCount = allTopics.stream()
                .filter(t -> t.getEnabled())
                .mapToLong(Topic::getUsageCount)
                .sum();

        stats.put("totalTopics", totalTopics);
        stats.put("activeTopics", activeTopics);
        stats.put("mergedTopics", mergedTopics);
        stats.put("level0Topics", level0Topics);
        stats.put("totalUsageCount", totalUsageCount);
        stats.put("averageUsageCount", activeTopics > 0 ? totalUsageCount / (double) activeTopics : 0);

        // Find most used topic
        allTopics.stream()
                .filter(t -> t.getEnabled() && t.getUsageCount() > 0)
                .max(Comparator.comparing(Topic::getUsageCount))
                .ifPresent(t -> {
                    stats.put("mostUsedTopicName", t.getName());
                    stats.put("mostUsedTopicCount", t.getUsageCount());
                });

        return stats;
    }
}
