/**
 * commentModule.js - 댓글 관련 기능
 */

import { post, del, getCsrfToken } from '../utils/ajax.js';
import { showToast } from '../utils/ui.js';
import { escapeHtml } from '../utils/validation.js';

/**
 * 댓글 작성
 * @param {number} boardId - 게시글 ID
 * @param {string} content - 댓글 내용
 * @param {number|null} parentId - 부모 댓글 ID (대댓글인 경우)
 * @returns {Promise<Object>} 응답 데이터
 */
export async function createComment(boardId, content, parentId = null) {
    try {
        const data = await post(`/comments/post/${boardId}`, {
            content: content.trim(),
            parentId
        });

        if (data.success) {
            showToast('댓글이 작성되었습니다', 'success', 2000);
        }

        return data;
    } catch (error) {
        console.error('Comment create error:', error);
        showToast('댓글 작성 중 오류가 발생했습니다', 'error');
        throw error;
    }
}

/**
 * 댓글 수정
 * @param {number} commentId - 댓글 ID
 * @param {string} content - 수정할 내용
 * @returns {Promise<Object>} 응답 데이터
 */
export async function updateComment(commentId, content) {
    const { token, header } = getCsrfToken();

    try {
        const response = await fetch(`/comments/${commentId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            },
            body: JSON.stringify({ content: content.trim() })
        });

        if (!response.ok) {
            throw new Error('Failed to update comment');
        }

        const data = await response.json();

        if (data.success) {
            showToast('댓글이 수정되었습니다', 'success', 2000);
        }

        return data;
    } catch (error) {
        console.error('Comment update error:', error);
        showToast('댓글 수정 중 오류가 발생했습니다', 'error');
        throw error;
    }
}

/**
 * 댓글 삭제
 * @param {number} commentId - 댓글 ID
 * @returns {Promise<Object>} 응답 데이터
 */
export async function deleteComment(commentId) {
    if (!confirm('정말로 이 댓글을 삭제하시겠습니까?')) {
        return { success: false, cancelled: true };
    }

    try {
        const data = await del(`/comments/${commentId}`);

        if (data.success) {
            showToast('댓글이 삭제되었습니다', 'success', 2000);
        }

        return data;
    } catch (error) {
        console.error('Comment delete error:', error);
        showToast('댓글 삭제 중 오류가 발생했습니다', 'error');
        throw error;
    }
}

/**
 * 댓글 목록 로드
 * @param {number} boardId - 게시글 ID
 * @param {Object} options - 옵션 (page, size, sort 등)
 * @returns {Promise<Array>} 댓글 목록
 */
export async function loadComments(boardId, options = {}) {
    const { page = 0, size = 20, sort = 'createdAt,desc' } = options;

    try {
        const response = await fetch(
            `/api/comments/post/${boardId}?page=${page}&size=${size}&sort=${sort}`
        );

        if (!response.ok) {
            throw new Error('Failed to load comments');
        }

        const data = await response.json();
        return data.content || data;
    } catch (error) {
        console.error('Comments load error:', error);
        return [];
    }
}

/**
 * 댓글 편집 폼 표시
 * @param {number} commentId - 댓글 ID
 */
export function showCommentEditForm(commentId) {
    const commentEl = document.querySelector(`.comment-item[data-comment-id="${commentId}"]`);
    if (!commentEl) return;

    const contentEl = commentEl.querySelector('.comment-content');
    const actionsEl = commentEl.querySelector('.comment-actions');

    if (!contentEl) return;

    const currentContent = contentEl.textContent.trim();

    // 편집 폼 생성
    const editForm = document.createElement('div');
    editForm.className = 'comment-edit-form';
    editForm.innerHTML = `
        <textarea class="comment-edit-textarea" rows="3">${escapeHtml(currentContent)}</textarea>
        <div class="comment-edit-actions">
            <button class="btn btn-secondary btn-sm" onclick="cancelCommentEdit(${commentId})">취소</button>
            <button class="btn btn-primary btn-sm" onclick="saveCommentEdit(${commentId})">저장</button>
        </div>
    `;

    // 기존 내용 숨기고 편집 폼 표시
    contentEl.style.display = 'none';
    if (actionsEl) actionsEl.style.display = 'none';

    commentEl.insertBefore(editForm, contentEl.nextSibling);

    // 텍스트 영역에 포커스
    const textarea = editForm.querySelector('.comment-edit-textarea');
    if (textarea) {
        textarea.focus();
        textarea.setSelectionRange(textarea.value.length, textarea.value.length);
    }
}

/**
 * 댓글 편집 취소
 * @param {number} commentId - 댓글 ID
 */
export function cancelCommentEdit(commentId) {
    const commentEl = document.querySelector(`.comment-item[data-comment-id="${commentId}"]`);
    if (!commentEl) return;

    const editForm = commentEl.querySelector('.comment-edit-form');
    const contentEl = commentEl.querySelector('.comment-content');
    const actionsEl = commentEl.querySelector('.comment-actions');

    if (editForm) editForm.remove();
    if (contentEl) contentEl.style.display = '';
    if (actionsEl) actionsEl.style.display = '';
}

/**
 * 댓글 편집 저장
 * @param {number} commentId - 댓글 ID
 */
export async function saveCommentEdit(commentId) {
    const commentEl = document.querySelector(`.comment-item[data-comment-id="${commentId}"]`);
    if (!commentEl) return;

    const editForm = commentEl.querySelector('.comment-edit-form');
    const textarea = editForm?.querySelector('.comment-edit-textarea');

    if (!textarea) return;

    const newContent = textarea.value.trim();

    if (!newContent) {
        alert('댓글 내용을 입력해주세요.');
        return;
    }

    try {
        const data = await updateComment(commentId, newContent);

        if (data.success) {
            // UI 업데이트
            const contentEl = commentEl.querySelector('.comment-content');
            if (contentEl) {
                contentEl.textContent = newContent;
            }

            cancelCommentEdit(commentId);
        }
    } catch (error) {
        // 에러는 updateComment에서 이미 처리됨
    }
}

/**
 * 대댓글 폼 표시
 * @param {number} commentId - 부모 댓글 ID
 */
export function showReplyForm(commentId) {
    const commentEl = document.querySelector(`.comment-item[data-comment-id="${commentId}"]`);
    if (!commentEl) return;

    // 기존 대댓글 폼이 있으면 제거
    const existingForm = commentEl.querySelector('.reply-form');
    if (existingForm) {
        existingForm.remove();
        return;
    }

    const boardId = getBoardId();
    if (!boardId) return;

    // 대댓글 폼 생성
    const replyForm = document.createElement('div');
    replyForm.className = 'reply-form';
    replyForm.innerHTML = `
        <textarea class="reply-textarea" rows="2" placeholder="답글을 입력하세요..."></textarea>
        <div class="reply-actions">
            <button class="btn btn-secondary btn-sm" onclick="hideReplyForm(${commentId})">취소</button>
            <button class="btn btn-primary btn-sm" onclick="submitReply(${boardId}, ${commentId})">답글 작성</button>
        </div>
    `;

    commentEl.appendChild(replyForm);

    const textarea = replyForm.querySelector('.reply-textarea');
    if (textarea) textarea.focus();
}

/**
 * 대댓글 폼 숨김
 * @param {number} commentId - 부모 댓글 ID
 */
export function hideReplyForm(commentId) {
    const commentEl = document.querySelector(`.comment-item[data-comment-id="${commentId}"]`);
    if (!commentEl) return;

    const replyForm = commentEl.querySelector('.reply-form');
    if (replyForm) replyForm.remove();
}

/**
 * 대댓글 작성
 * @param {number} boardId - 게시글 ID
 * @param {number} parentId - 부모 댓글 ID
 */
export async function submitReply(boardId, parentId) {
    const commentEl = document.querySelector(`.comment-item[data-comment-id="${parentId}"]`);
    if (!commentEl) return;

    const textarea = commentEl.querySelector('.reply-textarea');
    if (!textarea) return;

    const content = textarea.value.trim();

    if (!content) {
        alert('답글 내용을 입력해주세요.');
        return;
    }

    try {
        const data = await createComment(boardId, content, parentId);

        if (data.success) {
            // 폼 제거
            hideReplyForm(parentId);

            // 페이지 새로고침 또는 동적으로 댓글 추가
            location.reload();
        }
    } catch (error) {
        // 에러는 createComment에서 이미 처리됨
    }
}

/**
 * 현재 페이지의 게시글 ID 가져오기
 * @returns {number|null} 게시글 ID
 */
function getBoardId() {
    const container = document.querySelector('.container[data-board-id]');
    return container ? parseInt(container.getAttribute('data-board-id')) : null;
}

// 전역 함수로 노출 (하위 호환성)
if (typeof window !== 'undefined') {
    window.showCommentEditForm = showCommentEditForm;
    window.cancelCommentEdit = cancelCommentEdit;
    window.saveCommentEdit = saveCommentEdit;
    window.showReplyForm = showReplyForm;
    window.hideReplyForm = hideReplyForm;
    window.submitReply = submitReply;
}

// 기본 내보내기
export default {
    createComment,
    updateComment,
    deleteComment,
    loadComments,
    showCommentEditForm,
    cancelCommentEdit,
    saveCommentEdit,
    showReplyForm,
    hideReplyForm,
    submitReply
};
