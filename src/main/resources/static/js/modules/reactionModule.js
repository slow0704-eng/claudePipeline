/**
 * reactionModule.js - 게시글 및 댓글 반응 시스템
 * Facebook 스타일 반응 (LIKE, HELPFUL, FUNNY, WOW, SAD, ANGRY, THINKING, CELEBRATE)
 */

import { getCsrfToken } from '../utils/ajax.js';

// 반응 타입 이모지 및 라벨 매핑
const REACTION_EMOJIS = {
    'LIKE': '❤️',
    'HELPFUL': '👍',
    'FUNNY': '😂',
    'WOW': '😮',
    'SAD': '😢',
    'ANGRY': '😡',
    'THINKING': '🤔',
    'CELEBRATE': '🎉'
};

const REACTION_LABELS = {
    'LIKE': '좋아요',
    'HELPFUL': '도움됨',
    'FUNNY': '재미있음',
    'WOW': '놀라움',
    'SAD': '슬픔',
    'ANGRY': '분노',
    'THINKING': '생각중',
    'CELEBRATE': '축하'
};

let hidePickerTimeout;
let touchTimer;
let quickTap = true;

/**
 * 게시글 반응 토글
 * @param {number} boardId - 게시글 ID
 * @param {string} reactionType - 반응 타입
 * @returns {Promise<Object>} 응답 데이터
 */
export async function togglePostReaction(boardId, reactionType) {
    const { token, header } = getCsrfToken();

    if (!token || !header) {
        throw new Error('CSRF 토큰을 찾을 수 없습니다.');
    }

    const allBtns = document.querySelectorAll('.reaction-section .reaction-btn');
    allBtns.forEach(b => b.disabled = true);

    try {
        const response = await fetch(`/likes/post/${boardId}/reaction?reactionType=${reactionType}`, {
            method: 'POST',
            headers: {
                [header]: token
            }
        });

        if (!response.ok) {
            const data = await response.json().catch(() => ({}));
            throw new Error(data.message || `HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        if (data.success) {
            updateReactionUI(data);
        }

        return data;
    } finally {
        allBtns.forEach(b => b.disabled = false);
    }
}

/**
 * 반응 UI 업데이트
 * @param {Object} data - 서버 응답 데이터
 */
function updateReactionUI(data) {
    const allBtns = document.querySelectorAll('.reaction-section .reaction-btn');

    // 모든 반응 버튼의 active 클래스 제거
    allBtns.forEach(b => b.classList.remove('active'));

    // 현재 선택된 반응에 active 추가
    if (data.isReacted && data.currentReaction) {
        const activeBtn = document.querySelector(
            `.reaction-section .reaction-btn[data-reaction="${data.currentReaction}"]`
        );
        if (activeBtn) activeBtn.classList.add('active');

        // 프라이머리 버튼 업데이트
        updatePrimaryButton(data.currentReaction, true);
    } else {
        // 반응 취소 시 기본 상태로
        updatePrimaryButton('LIKE', false);
    }

    // 각 반응별 카운트 업데이트
    if (data.reactionCounts) {
        Object.entries(data.reactionCounts).forEach(([reaction, count]) => {
            const countElement = document.getElementById(`post-reaction-${reaction}`);
            if (countElement) countElement.textContent = count;
        });
    }

    // 총 반응 수 업데이트
    if (data.totalCount !== undefined) {
        const totalElement = document.getElementById('total-reactions');
        if (totalElement) totalElement.textContent = data.totalCount;
    }
}

/**
 * 메인 반응 버튼 업데이트
 * @param {string} reactionType - 반응 타입
 * @param {boolean} isActive - 활성 상태
 */
function updatePrimaryButton(reactionType, isActive) {
    const primaryBtn = document.getElementById('primaryReactionBtn');
    if (!primaryBtn) return;

    const emoji = REACTION_EMOJIS[reactionType] || '❤️';
    const label = REACTION_LABELS[reactionType] || '좋아요';
    const countEl = document.getElementById(`post-reaction-${reactionType}`);
    const count = countEl ? countEl.textContent : '0';

    primaryBtn.innerHTML = `
        <span class="emoji">${emoji}</span>
        <span class="label">${label}</span>
        <span class="count" id="primary-reaction-count">${count}</span>
    `;

    primaryBtn.className = 'primary-reaction-btn';
    if (isActive) {
        primaryBtn.classList.add('active');
        primaryBtn.dataset.reaction = reactionType;
    } else {
        primaryBtn.dataset.reaction = 'LIKE';
    }
}

/**
 * 반응 피커 표시
 */
export function showReactionPicker() {
    const reactionPicker = document.getElementById('reactionPicker');
    const primaryBtn = document.getElementById('primaryReactionBtn');

    if (reactionPicker) {
        reactionPicker.classList.add('show');
        if (primaryBtn) {
            primaryBtn.setAttribute('aria-expanded', 'true');
        }
    }
}

/**
 * 반응 피커 숨김
 */
export function hideReactionPicker() {
    const reactionPicker = document.getElementById('reactionPicker');
    const primaryBtn = document.getElementById('primaryReactionBtn');

    if (reactionPicker) {
        reactionPicker.classList.remove('show');
        if (primaryBtn) {
            primaryBtn.setAttribute('aria-expanded', 'false');
        }
    }
}

/**
 * 반응 선택
 * @param {number} boardId - 게시글 ID
 * @param {string} reactionType - 반응 타입
 */
export async function selectReaction(boardId, reactionType) {
    hideReactionPicker();

    try {
        await togglePostReaction(boardId, reactionType);
    } catch (error) {
        console.error('Reaction error:', error);
        alert('오류가 발생했습니다: ' + error.message);
    }
}

/**
 * 반응 피커 초기화 (데스크톱 - 호버)
 */
export function initializeReactionPickerDesktop() {
    const primaryBtn = document.getElementById('primaryReactionBtn');
    const reactionPicker = document.getElementById('reactionPicker');

    if (!primaryBtn || !reactionPicker || ('ontouchstart' in window)) {
        return;
    }

    primaryBtn.addEventListener('mouseenter', () => {
        clearTimeout(hidePickerTimeout);
        showReactionPicker();
    });

    primaryBtn.addEventListener('mouseleave', () => {
        hidePickerTimeout = setTimeout(hideReactionPicker, 300);
    });

    reactionPicker.addEventListener('mouseenter', () => {
        clearTimeout(hidePickerTimeout);
    });

    reactionPicker.addEventListener('mouseleave', () => {
        hidePickerTimeout = setTimeout(hideReactionPicker, 300);
    });
}

/**
 * 반응 피커 초기화 (모바일 - 길게 누르기)
 */
export function initializeReactionPickerMobile() {
    const primaryBtn = document.getElementById('primaryReactionBtn');

    if (!primaryBtn || !('ontouchstart' in window)) {
        return;
    }

    primaryBtn.addEventListener('touchstart', (e) => {
        quickTap = true;
        touchTimer = setTimeout(() => {
            quickTap = false;
            showReactionPicker();

            // 햅틱 피드백 (지원하는 기기만)
            if (navigator.vibrate) {
                navigator.vibrate(50);
            }
        }, 500);
    });

    primaryBtn.addEventListener('touchmove', () => {
        clearTimeout(touchTimer);
        quickTap = false;
    });

    primaryBtn.addEventListener('touchend', (e) => {
        clearTimeout(touchTimer);

        if (quickTap) {
            // 빠른 탭: 기본 반응 토글
            const boardId = getBoardId();
            const currentReaction = primaryBtn.dataset.reaction || 'LIKE';
            if (boardId) {
                selectReaction(boardId, currentReaction);
            }
        }
    });
}

/**
 * 현재 페이지의 게시글 ID 가져오기
 * @returns {number|null} 게시글 ID
 */
function getBoardId() {
    const container = document.querySelector('.container[data-board-id]');
    return container ? parseInt(container.getAttribute('data-board-id')) : null;
}

/**
 * 댓글 반응 토글
 * @param {number} commentId - 댓글 ID
 * @param {string} reactionType - 반응 타입
 * @returns {Promise<Object>} 응답 데이터
 */
export async function toggleCommentReaction(commentId, reactionType) {
    const { token, header } = getCsrfToken();

    if (!token || !header) {
        throw new Error('CSRF 토큰을 찾을 수 없습니다.');
    }

    const response = await fetch(`/likes/comment/${commentId}/reaction?reactionType=${reactionType}`, {
        method: 'POST',
        headers: {
            [header]: token
        }
    });

    if (!response.ok) {
        const data = await response.json().catch(() => ({}));
        throw new Error(data.message || `HTTP error! status: ${response.status}`);
    }

    const data = await response.json();

    if (data.success) {
        updateCommentReactionUI(commentId, data);
    }

    return data;
}

/**
 * 댓글 반응 UI 업데이트
 * @param {number} commentId - 댓글 ID
 * @param {Object} data - 서버 응답 데이터
 */
function updateCommentReactionUI(commentId, data) {
    const commentEl = document.querySelector(`.comment-item[data-comment-id="${commentId}"]`);
    if (!commentEl) return;

    const allBtns = commentEl.querySelectorAll('.comment-reaction-btn');

    // 모든 반응 버튼의 active 클래스 제거
    allBtns.forEach(b => b.classList.remove('active'));

    // 현재 선택된 반응에 active 추가
    if (data.isReacted && data.currentReaction) {
        const activeBtn = commentEl.querySelector(
            `.comment-reaction-btn[data-reaction="${data.currentReaction}"]`
        );
        if (activeBtn) activeBtn.classList.add('active');
    }

    // 각 반응별 카운트 업데이트
    if (data.reactionCounts) {
        Object.entries(data.reactionCounts).forEach(([reaction, count]) => {
            const countElement = commentEl.querySelector(`#comment-reaction-${commentId}-${reaction}`);
            if (countElement) countElement.textContent = count;
        });
    }

    // 총 반응 수 업데이트
    if (data.totalCount !== undefined) {
        const totalElement = commentEl.querySelector(`.comment-total-reactions[data-comment-id="${commentId}"]`);
        if (totalElement) totalElement.textContent = data.totalCount;
    }
}

/**
 * 반응 모듈 초기화
 */
export function initializeReactionModule() {
    initializeReactionPickerDesktop();
    initializeReactionPickerMobile();

    console.log('Reaction module initialized');
}

// 기본 내보내기
export default {
    togglePostReaction,
    toggleCommentReaction,
    selectReaction,
    showReactionPicker,
    hideReactionPicker,
    initializeReactionModule,
    REACTION_EMOJIS,
    REACTION_LABELS
};
