/**
 * boardModule.js - 게시글 관련 기능
 * 공유, 북마크, 신고, 이미지 갤러리 등
 */

import { post, del } from '../utils/ajax.js';
import { showToast, copyToClipboard } from '../utils/ui.js';

/**
 * 게시글 북마크 토글
 * @param {number} boardId - 게시글 ID
 * @returns {Promise<Object>} 응답 데이터
 */
export async function toggleBookmark(boardId) {
    try {
        const data = await post(`/bookmarks/${boardId}`);

        if (data.success) {
            const message = data.isBookmarked ? '북마크에 추가되었습니다' : '북마크가 해제되었습니다';
            showToast(message, 'success', 2000);

            // 북마크 버튼 UI 업데이트
            updateBookmarkButton(boardId, data.isBookmarked);
        }

        return data;
    } catch (error) {
        console.error('Bookmark toggle error:', error);
        showToast('북마크 처리 중 오류가 발생했습니다', 'error');
        throw error;
    }
}

/**
 * 북마크 버튼 UI 업데이트
 * @param {number} boardId - 게시글 ID
 * @param {boolean} isBookmarked - 북마크 상태
 */
function updateBookmarkButton(boardId, isBookmarked) {
    const bookmarkBtn = document.querySelector(`.bookmark-btn[data-board-id="${boardId}"]`);
    if (!bookmarkBtn) return;

    const icon = bookmarkBtn.querySelector('i');
    const text = bookmarkBtn.querySelector('.bookmark-text');

    if (isBookmarked) {
        bookmarkBtn.classList.add('bookmarked');
        if (icon) icon.className = 'fas fa-bookmark';
        if (text) text.textContent = '북마크됨';
    } else {
        bookmarkBtn.classList.remove('bookmarked');
        if (icon) icon.className = 'far fa-bookmark';
        if (text) text.textContent = '북마크';
    }
}

/**
 * 게시글 공유
 * @param {number} boardId - 게시글 ID
 * @param {string} title - 게시글 제목
 */
export async function shareBoard(boardId, title) {
    const url = `${window.location.origin}/board/${boardId}`;

    // Web Share API 지원 확인 (모바일)
    if (navigator.share) {
        try {
            await navigator.share({
                title: title,
                text: title,
                url: url
            });

            // 공유 통계 기록
            recordShare(boardId);

            showToast('게시글이 공유되었습니다', 'success', 2000);
        } catch (error) {
            if (error.name !== 'AbortError') {
                console.error('Share error:', error);
            }
        }
    } else {
        // Web Share API 미지원 - URL 복사
        await copyToClipboard(url);

        // 공유 통계 기록
        recordShare(boardId);
    }
}

/**
 * 공유 통계 기록
 * @param {number} boardId - 게시글 ID
 */
async function recordShare(boardId) {
    try {
        await post(`/shares/${boardId}`);
    } catch (error) {
        console.error('Share recording error:', error);
    }
}

/**
 * 게시글 신고
 * @param {number} boardId - 게시글 ID
 * @param {string} reason - 신고 사유
 * @param {string} description - 상세 설명
 * @returns {Promise<Object>} 응답 데이터
 */
export async function reportBoard(boardId, reason, description = '') {
    try {
        const data = await post(`/reports/board/${boardId}`, {
            reason,
            description
        });

        if (data.success) {
            showToast('신고가 접수되었습니다', 'success');
        }

        return data;
    } catch (error) {
        console.error('Report error:', error);
        showToast('신고 처리 중 오류가 발생했습니다', 'error');
        throw error;
    }
}

/**
 * 게시글 삭제
 * @param {number} boardId - 게시글 ID
 * @returns {Promise<Object>} 응답 데이터
 */
export async function deleteBoard(boardId) {
    if (!confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
        return { success: false, cancelled: true };
    }

    try {
        const data = await del(`/board/${boardId}`);

        if (data.success) {
            showToast('게시글이 삭제되었습니다', 'success');

            // 목록 페이지로 이동
            setTimeout(() => {
                location.href = '/board';
            }, 1000);
        }

        return data;
    } catch (error) {
        console.error('Board delete error:', error);
        showToast('게시글 삭제 중 오류가 발생했습니다', 'error');
        throw error;
    }
}

/**
 * 이미지 라이트박스 열기
 * @param {string} imageUrl - 이미지 URL
 * @param {string} caption - 이미지 캡션
 */
export function openLightbox(imageUrl, caption = '') {
    let lightbox = document.getElementById('image-lightbox');

    if (!lightbox) {
        lightbox = document.createElement('div');
        lightbox.id = 'image-lightbox';
        lightbox.className = 'lightbox';
        lightbox.innerHTML = `
            <div class="lightbox-content">
                <span class="lightbox-close">&times;</span>
                <img class="lightbox-image" src="" alt="Image">
                <div class="lightbox-caption"></div>
            </div>
        `;
        document.body.appendChild(lightbox);

        // 닫기 버튼 이벤트
        const closeBtn = lightbox.querySelector('.lightbox-close');
        closeBtn.addEventListener('click', closeLightbox);

        // 배경 클릭 시 닫기
        lightbox.addEventListener('click', (e) => {
            if (e.target === lightbox) {
                closeLightbox();
            }
        });

        // ESC 키로 닫기
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && lightbox.classList.contains('active')) {
                closeLightbox();
            }
        });
    }

    // 이미지 및 캡션 설정
    const img = lightbox.querySelector('.lightbox-image');
    const captionEl = lightbox.querySelector('.lightbox-caption');

    img.src = imageUrl;
    if (captionEl) captionEl.textContent = caption;

    // 라이트박스 표시
    lightbox.classList.add('active');
    document.body.style.overflow = 'hidden';
}

/**
 * 이미지 라이트박스 닫기
 */
export function closeLightbox() {
    const lightbox = document.getElementById('image-lightbox');
    if (lightbox) {
        lightbox.classList.remove('active');
        document.body.style.overflow = '';
    }
}

/**
 * 이미지 갤러리 초기화
 */
export function initializeImageGallery() {
    const images = document.querySelectorAll('.post-content img, .gallery-item img');

    images.forEach(img => {
        img.style.cursor = 'pointer';
        img.addEventListener('click', () => {
            const caption = img.alt || img.title || '';
            openLightbox(img.src, caption);
        });
    });
}

/**
 * 조회수 증가 (페이지 로드 시 자동 호출)
 * @param {number} boardId - 게시글 ID
 */
export async function incrementViewCount(boardId) {
    try {
        await post(`/board/${boardId}/view`);
    } catch (error) {
        console.error('View count increment error:', error);
        // 조회수는 실패해도 사용자에게 알리지 않음
    }
}

/**
 * 게시글 모듈 초기화
 */
export function initializeBoardModule() {
    // 이미지 갤러리 초기화
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initializeImageGallery);
    } else {
        initializeImageGallery();
    }

    // 조회수 증가 (게시글 상세 페이지인 경우)
    const container = document.querySelector('.container[data-board-id]');
    if (container) {
        const boardId = parseInt(container.getAttribute('data-board-id'));
        if (boardId) {
            incrementViewCount(boardId);
        }
    }

    console.log('Board module initialized');
}

// 전역 함수로 노출 (하위 호환성)
if (typeof window !== 'undefined') {
    window.toggleBookmark = toggleBookmark;
    window.shareBoard = shareBoard;
    window.reportBoard = reportBoard;
    window.deleteBoard = deleteBoard;
    window.openLightbox = openLightbox;
    window.closeLightbox = closeLightbox;
}

// 기본 내보내기
export default {
    toggleBookmark,
    shareBoard,
    reportBoard,
    deleteBoard,
    openLightbox,
    closeLightbox,
    initializeImageGallery,
    incrementViewCount,
    initializeBoardModule
};
