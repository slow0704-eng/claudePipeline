/**
 * ui.js - UI 관련 유틸리티
 * 토스트, 다이얼로그, 스크롤 등
 */

/**
 * 알림 표시 (토스트)
 * @param {string} message - 메시지
 * @param {string} type - 타입 (success, error, info, warning)
 * @param {number} duration - 표시 시간 (ms)
 */
export function showToast(message, type = 'info', duration = 3000) {
    // 토스트 컨테이너 생성 (없으면)
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.style.cssText = 'position: fixed; top: 20px; right: 20px; z-index: 9999;';
        document.body.appendChild(container);
    }

    // 토스트 생성
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.style.cssText = `
        padding: 15px 20px;
        margin-bottom: 10px;
        border-radius: 8px;
        color: white;
        min-width: 250px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        animation: slideIn 0.3s ease;
        cursor: pointer;
    `;

    // 타입별 배경색
    const colors = {
        success: '#28a745',
        error: '#dc3545',
        warning: '#ffc107',
        info: '#17a2b8'
    };
    toast.style.backgroundColor = colors[type] || colors.info;

    toast.textContent = message;

    // 클릭 시 제거
    toast.onclick = () => toast.remove();

    // 컨테이너에 추가
    container.appendChild(toast);

    // 자동 제거
    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, duration);
}

/**
 * 확인 다이얼로그 (Promise 기반)
 * @param {string} message - 메시지
 * @param {string} title - 제목 (선택적)
 * @returns {Promise<boolean>} 확인 여부
 */
export function confirmDialog(message, title = '확인') {
    return new Promise((resolve) => {
        const confirmed = confirm(message);
        resolve(confirmed);
    });
}

/**
 * 페이지 상단으로 스크롤
 * @param {boolean} smooth - 부드러운 스크롤 여부
 */
export function scrollToTop(smooth = true) {
    window.scrollTo({
        top: 0,
        behavior: smooth ? 'smooth' : 'auto'
    });
}

/**
 * 특정 요소로 스크롤
 * @param {HTMLElement|string} element - 요소 또는 선택자
 * @param {boolean} smooth - 부드러운 스크롤 여부
 * @param {number} offset - 오프셋 (px)
 */
export function scrollToElement(element, smooth = true, offset = 0) {
    const el = typeof element === 'string' ? document.querySelector(element) : element;

    if (!el) {
        console.warn('Element not found:', element);
        return;
    }

    const top = el.getBoundingClientRect().top + window.pageYOffset - offset;

    window.scrollTo({
        top,
        behavior: smooth ? 'smooth' : 'auto'
    });
}

/**
 * 모바일 여부 체크
 * @returns {boolean} 모바일 여부
 */
export function isMobile() {
    return window.innerWidth <= 768;
}

/**
 * 복사하기
 * @param {string} text - 복사할 텍스트
 * @returns {Promise<boolean>} 복사 성공 여부
 */
export async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
        showToast('복사되었습니다', 'success', 2000);
        return true;
    } catch (error) {
        console.error('Copy failed:', error);
        showToast('복사에 실패했습니다', 'error', 2000);
        return false;
    }
}

/**
 * 요소 표시/숨김 토글
 * @param {HTMLElement|string} element - 요소 또는 선택자
 * @param {boolean} show - 표시 여부 (선택적)
 */
export function toggleElement(element, show = null) {
    const el = typeof element === 'string' ? document.querySelector(element) : element;

    if (!el) {
        console.warn('Element not found:', element);
        return;
    }

    if (show === null) {
        el.style.display = el.style.display === 'none' ? '' : 'none';
    } else {
        el.style.display = show ? '' : 'none';
    }
}

/**
 * 로딩 스피너 표시
 * @param {boolean} show - 표시 여부
 * @param {string} message - 로딩 메시지 (선택적)
 */
export function showLoading(show = true, message = '로딩 중...') {
    let spinner = document.getElementById('loading-spinner');

    if (show) {
        if (!spinner) {
            spinner = document.createElement('div');
            spinner.id = 'loading-spinner';
            spinner.style.cssText = `
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.5);
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 10000;
            `;

            spinner.innerHTML = `
                <div style="
                    background: white;
                    padding: 20px 40px;
                    border-radius: 8px;
                    text-align: center;
                ">
                    <div class="spinner-border" role="status"></div>
                    <div style="margin-top: 10px;">${message}</div>
                </div>
            `;

            document.body.appendChild(spinner);
        }
    } else {
        if (spinner) {
            spinner.remove();
        }
    }
}

/**
 * Debounce 함수
 * @param {Function} func - 실행할 함수
 * @param {number} wait - 대기 시간 (ms)
 * @returns {Function} debounced 함수
 */
export function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * Throttle 함수
 * @param {Function} func - 실행할 함수
 * @param {number} limit - 제한 시간 (ms)
 * @returns {Function} throttled 함수
 */
export function throttle(func, limit) {
    let inThrottle;
    return function executedFunction(...args) {
        if (!inThrottle) {
            func(...args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

/**
 * 애니메이션 스타일 주입 (토스트용)
 */
function injectToastAnimations() {
    if (!document.getElementById('toast-animations')) {
        const style = document.createElement('style');
        style.id = 'toast-animations';
        style.textContent = `
            @keyframes slideIn {
                from {
                    transform: translateX(400px);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
            @keyframes slideOut {
                from {
                    transform: translateX(0);
                    opacity: 1;
                }
                to {
                    transform: translateX(400px);
                    opacity: 0;
                }
            }
        `;
        document.head.appendChild(style);
    }
}

// 페이지 로드 시 애니메이션 주입
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', injectToastAnimations);
} else {
    injectToastAnimations();
}

// 기본 내보내기
export default {
    showToast,
    confirmDialog,
    scrollToTop,
    scrollToElement,
    isMobile,
    copyToClipboard,
    toggleElement,
    showLoading,
    debounce,
    throttle
};
