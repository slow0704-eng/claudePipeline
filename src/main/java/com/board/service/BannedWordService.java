package com.board.service;

import com.board.entity.BannedWord;
import com.board.enums.BannedWordAction;
import com.board.repository.BannedWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Service
@RequiredArgsConstructor
public class BannedWordService {

    private final BannedWordRepository bannedWordRepository;

    /**
     * 모든 금지어 조회
     */
    public List<BannedWord> getAllBannedWords() {
        return bannedWordRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 활성화된 금지어만 조회
     */
    public List<BannedWord> getActiveBannedWords() {
        return bannedWordRepository.findByEnabledTrueOrderByCreatedAtDesc();
    }

    /**
     * ID로 금지어 조회
     */
    public BannedWord getBannedWordById(Long id) {
        return bannedWordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("금지어를 찾을 수 없습니다."));
    }

    /**
     * 금지어 생성
     */
    @Transactional
    public BannedWord createBannedWord(String word, String description, Boolean isRegex, BannedWordAction action) {
        // 정규식인 경우 유효성 검사
        if (isRegex != null && isRegex) {
            try {
                Pattern.compile(word);
            } catch (PatternSyntaxException e) {
                throw new RuntimeException("잘못된 정규식 패턴입니다: " + e.getMessage());
            }
        }

        // 중복 체크
        if (bannedWordRepository.existsByWordAndEnabled(word, true)) {
            throw new RuntimeException("이미 존재하는 금지어입니다.");
        }

        BannedWord bannedWord = new BannedWord();
        bannedWord.setWord(word);
        bannedWord.setDescription(description);
        bannedWord.setIsRegex(isRegex != null ? isRegex : false);
        bannedWord.setAction(action != null ? action : BannedWordAction.BLOCK);
        bannedWord.setEnabled(true);

        return bannedWordRepository.save(bannedWord);
    }

    /**
     * 금지어 수정
     */
    @Transactional
    public BannedWord updateBannedWord(Long id, String word, String description, Boolean isRegex, BannedWordAction action, Boolean enabled) {
        BannedWord bannedWord = getBannedWordById(id);

        // 정규식인 경우 유효성 검사
        if (isRegex != null && isRegex) {
            try {
                Pattern.compile(word);
            } catch (PatternSyntaxException e) {
                throw new RuntimeException("잘못된 정규식 패턴입니다: " + e.getMessage());
            }
        }

        bannedWord.setWord(word);
        bannedWord.setDescription(description);
        if (isRegex != null) {
            bannedWord.setIsRegex(isRegex);
        }
        if (action != null) {
            bannedWord.setAction(action);
        }
        if (enabled != null) {
            bannedWord.setEnabled(enabled);
        }

        return bannedWordRepository.save(bannedWord);
    }

    /**
     * 금지어 삭제
     */
    @Transactional
    public void deleteBannedWord(Long id) {
        BannedWord bannedWord = getBannedWordById(id);
        bannedWordRepository.delete(bannedWord);
    }

    /**
     * 금지어 활성화/비활성화
     */
    @Transactional
    public BannedWord toggleBannedWordStatus(Long id) {
        BannedWord bannedWord = getBannedWordById(id);
        bannedWord.setEnabled(!bannedWord.getEnabled());
        return bannedWordRepository.save(bannedWord);
    }

    /**
     * 텍스트에 금지어가 포함되어 있는지 검사
     * @param text 검사할 텍스트
     * @return 금지어가 발견된 경우 해당 BannedWord 객체, 없으면 null
     */
    public BannedWord checkForBannedWords(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        List<BannedWord> activeBannedWords = getActiveBannedWords();

        for (BannedWord bannedWord : activeBannedWords) {
            if (containsBannedWord(text, bannedWord)) {
                return bannedWord;
            }
        }

        return null;
    }

    /**
     * 텍스트에 특정 금지어가 포함되어 있는지 확인
     */
    private boolean containsBannedWord(String text, BannedWord bannedWord) {
        if (bannedWord.getIsRegex()) {
            // 정규식 패턴 매칭
            try {
                Pattern pattern = Pattern.compile(bannedWord.getWord(), Pattern.CASE_INSENSITIVE);
                return pattern.matcher(text).find();
            } catch (PatternSyntaxException e) {
                // 정규식 오류가 있는 경우 무시
                return false;
            }
        } else {
            // 일반 텍스트 검색 (대소문자 구분 없음)
            return text.toLowerCase().contains(bannedWord.getWord().toLowerCase());
        }
    }

    /**
     * 금지어가 포함된 경우 취해야 할 조치 반환
     * @param text 검사할 텍스트
     * @return BLOCK, PENDING, 또는 null (금지어 없음)
     */
    public BannedWordAction getBannedWordAction(String text) {
        BannedWord bannedWord = checkForBannedWords(text);
        return bannedWord != null ? bannedWord.getAction() : null;
    }
}
