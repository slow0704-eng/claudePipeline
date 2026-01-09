/**
 * topicModule.js - 토픽 팔로우 관련 기능
 */

import { showToast } from '../utils/ui.js';

/**
 * 토픽 팔로우/언팔로우 토글
 * @param {number} topicId - 토픽 ID
 * @param {string} topicName - 토픽 이름
 * @returns {Promise<Object>} 응답 데이터
 */
export async function toggleTopicFollow(topicId, topicName) {
    if (!topicId) {
        console.error('토픽 ID가 없습니다.');
        return { success: false };
    }

    try {
        const response = await fetch(`/api/topics/${topicId}/follow`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            if (response.status === 401) {
                alert('로그인이 필요합니다.');
                location.href = '/auth/login';
                return { success: false };
            }

            const error = await response.json().catch(() => ({}));
            throw new Error(error.error || '팔로우 처리 중 오류가 발생했습니다.');
        }

        const data = await response.json();

        // 피드백 메시지
        const action = data.isFollowing ? '팔로우했습니다' : '언팔로우했습니다';
        showToast(`${topicName} 토픽을 ${action}`, 'success', 2000);

        return data;
    } catch (error) {
        console.error('토픽 팔로우 오류:', error);
        showToast(error.message || '토픽 팔로우 처리 중 오류가 발생했습니다.', 'error');
        throw error;
    }
}

/**
 * 토픽 팔로우 버튼 UI 업데이트
 * @param {HTMLElement} button - 팔로우 버튼 요소
 * @param {boolean} isFollowing - 팔로우 상태
 * @param {number} followerCount - 팔로워 수
 */
export function updateFollowButtonUI(button, isFollowing, followerCount) {
    if (!button) return;

    const followTextSpan = button.querySelector('.follow-text');
    const followerCountSpan = button.querySelector('.topic-follower-count');

    if (isFollowing) {
        button.classList.remove('not-following');
        button.classList.add('following');
        if (followTextSpan) followTextSpan.textContent = '팔로잉';
    } else {
        button.classList.remove('following');
        button.classList.add('not-following');
        if (followTextSpan) followTextSpan.textContent = '팔로우';
    }

    if (followerCountSpan && followerCount !== undefined) {
        followerCountSpan.textContent = followerCount > 0 ? `${followerCount}` : '';
    }
}

/**
 * 토픽 팔로우 상태 조회
 * @param {number} topicId - 토픽 ID
 * @returns {Promise<Object>} 팔로우 상태 데이터
 */
export async function getTopicFollowStatus(topicId) {
    try {
        const response = await fetch(`/api/topics/${topicId}/follow-status`);

        if (!response.ok) {
            return { isFollowing: false, followerCount: 0 };
        }

        return await response.json();
    } catch (error) {
        console.error(`토픽 ${topicId} 팔로우 상태 로드 실패:`, error);
        return { isFollowing: false, followerCount: 0 };
    }
}

/**
 * 페이지 로드 시 모든 토픽의 팔로우 상태 초기화
 */
export async function initializeTopicFollowStatus() {
    const followButtons = document.querySelectorAll('.topic-follow-btn');

    for (const button of followButtons) {
        const topicId = button.getAttribute('data-topic-id');
        if (!topicId) continue;

        try {
            const data = await getTopicFollowStatus(topicId);
            updateFollowButtonUI(button, data.isFollowing, data.followerCount);
        } catch (error) {
            console.error(`토픽 ${topicId} 초기화 실패:`, error);
            updateFollowButtonUI(button, false, 0);
        }
    }
}

/**
 * 토픽 팔로우 버튼 클릭 핸들러
 * @param {HTMLElement} button - 클릭된 버튼 요소
 */
export async function handleTopicFollowButtonClick(button) {
    const topicId = button.getAttribute('data-topic-id');
    const topicName = button.getAttribute('data-topic-name');

    if (!topicId) {
        console.error('토픽 ID가 없습니다.');
        return;
    }

    // 버튼 비활성화
    button.disabled = true;

    try {
        const data = await toggleTopicFollow(topicId, topicName);

        if (data.success !== false) {
            // UI 업데이트
            updateFollowButtonUI(button, data.isFollowing, data.followerCount);
        }
    } finally {
        // 버튼 활성화
        button.disabled = false;
    }
}

/**
 * 토픽 모듈 초기화
 */
export function initializeTopicModule() {
    // 페이지 로드 시 팔로우 상태 초기화
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initializeTopicFollowStatus);
    } else {
        initializeTopicFollowStatus();
    }

    // 팔로우 버튼에 이벤트 리스너 추가 (이벤트 위임 사용)
    document.addEventListener('click', (e) => {
        const button = e.target.closest('.topic-follow-btn');
        if (button) {
            handleTopicFollowButtonClick(button);
        }
    });

    console.log('Topic module initialized');
}

// 전역 함수로 노출 (하위 호환성)
if (typeof window !== 'undefined') {
    window.toggleTopicFollow = (button) => {
        const topicId = button.getAttribute('data-topic-id');
        const topicName = button.getAttribute('data-topic-name');
        handleTopicFollowButtonClick(button);
    };
}

// 기본 내보내기
export default {
    toggleTopicFollow,
    updateFollowButtonUI,
    getTopicFollowStatus,
    initializeTopicFollowStatus,
    handleTopicFollowButtonClick,
    initializeTopicModule
};
