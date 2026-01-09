/**
 * common.js - 공통 유틸리티 함수
 * 전역에서 사용되는 헬퍼 함수들
 */

/**
 * CSRF 토큰 가져오기
 * @returns {Object} CSRF 토큰 및 헤더 정보
 */
function getCsrfToken() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

    return { token, header };
}

/**
 * Fetch 요청에 CSRF 토큰 추가
 * @param {Object} options - fetch options
 * @returns {Object} CSRF 토큰이 추가된 options
 */
function addCsrfToFetch(options = {}) {
    const { token, header } = getCsrfToken();

    if (!options.headers) {
        options.headers = {};
    }

    if (token && header) {
        options.headers[header] = token;
    }

    return options;
}

/**
 * 안전한 Fetch 래퍼 (CSRF 토큰 자동 추가)
 * @param {string} url - 요청 URL
 * @param {Object} options - fetch options
 * @returns {Promise} fetch Promise
 */
async function safeFetch(url, options = {}) {
    return fetch(url, addCsrfToFetch(options));
}

/**
 * 날짜 포맷팅
 * @param {Date|string} date - 포맷할 날짜
 * @param {string} format - 포맷 형식 (default: 'YYYY-MM-DD HH:mm')
 * @returns {string} 포맷된 날짜 문자열
 */
function formatDate(date, format = 'YYYY-MM-DD HH:mm') {
    const d = new Date(date);

    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    const seconds = String(d.getSeconds()).padStart(2, '0');

    return format
        .replace('YYYY', year)
        .replace('MM', month)
        .replace('DD', day)
        .replace('HH', hours)
        .replace('mm', minutes)
        .replace('ss', seconds);
}

/**
 * 상대 시간 표시
 * @param {Date|string} date - 날짜
 * @returns {string} 상대 시간 (예: "3분 전")
 */
function getRelativeTime(date) {
    const d = new Date(date);
    const now = new Date();
    const diff = now - d;

    const minute = 60 * 1000;
    const hour = 60 * minute;
    const day = 24 * hour;
    const week = 7 * day;
    const month = 30 * day;

    if (diff < minute) return '방금 전';
    if (diff < hour) return Math.floor(diff / minute) + '분 전';
    if (diff < day) return Math.floor(diff / hour) + '시간 전';
    if (diff < week) return Math.floor(diff / day) + '일 전';
    if (diff < month) return Math.floor(diff / week) + '주 전';

    return formatDate(d, 'YYYY-MM-DD');
}

/**
 * 숫자 포맷팅 (천 단위 콤마)
 * @param {number} num - 포맷할 숫자
 * @returns {string} 포맷된 숫자
 */
function formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

/**
 * 숫자 축약 (예: 1000 -> 1K)
 * @param {number} num - 축약할 숫자
 * @returns {string} 축약된 숫자
 */
function abbreviateNumber(num) {
    if (num < 1000) return num.toString();
    if (num < 1000000) return (num / 1000).toFixed(1).replace(/\.0$/, '') + 'K';
    return (num / 1000000).toFixed(1).replace(/\.0$/, '') + 'M';
}

/**
 * Debounce 함수
 * @param {Function} func - 실행할 함수
 * @param {number} wait - 대기 시간 (ms)
 * @returns {Function} debounced 함수
 */
function debounce(func, wait) {
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
function throttle(func, limit) {
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
 * 로컬 스토리지 헬퍼
 */
const storage = {
    /**
     * 로컬 스토리지에 저장
     * @param {string} key - 키
     * @param {*} value - 값 (자동으로 JSON 변환)
     */
    set(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
        } catch (error) {
            console.error('LocalStorage set error:', error);
        }
    },

    /**
     * 로컬 스토리지에서 가져오기
     * @param {string} key - 키
     * @param {*} defaultValue - 기본값
     * @returns {*} 저장된 값 또는 기본값
     */
    get(key, defaultValue = null) {
        try {
            const item = localStorage.getItem(key);
            return item ? JSON.parse(item) : defaultValue;
        } catch (error) {
            console.error('LocalStorage get error:', error);
            return defaultValue;
        }
    },

    /**
     * 로컬 스토리지에서 제거
     * @param {string} key - 키
     */
    remove(key) {
        try {
            localStorage.removeItem(key);
        } catch (error) {
            console.error('LocalStorage remove error:', error);
        }
    },

    /**
     * 로컬 스토리지 전체 삭제
     */
    clear() {
        try {
            localStorage.clear();
        } catch (error) {
            console.error('LocalStorage clear error:', error);
        }
    }
};

/**
 * 알림 표시 (토스트)
 * @param {string} message - 메시지
 * @param {string} type - 타입 (success, error, info, warning)
 * @param {number} duration - 표시 시간 (ms)
 */
function showToast(message, type = 'info', duration = 3000) {
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
 * 애니메이션 CSS 추가
 */
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

/**
 * 확인 다이얼로그
 * @param {string} message - 메시지
 * @param {Function} onConfirm - 확인 시 콜백
 * @param {Function} onCancel - 취소 시 콜백
 */
function confirmDialog(message, onConfirm, onCancel) {
    if (confirm(message)) {
        if (typeof onConfirm === 'function') onConfirm();
    } else {
        if (typeof onCancel === 'function') onCancel();
    }
}

/**
 * 페이지 상단으로 스크롤
 * @param {boolean} smooth - 부드러운 스크롤 여부
 */
function scrollToTop(smooth = true) {
    window.scrollTo({
        top: 0,
        behavior: smooth ? 'smooth' : 'auto'
    });
}

/**
 * URL 쿼리 파라미터 가져오기
 * @param {string} name - 파라미터 이름
 * @returns {string|null} 파라미터 값
 */
function getQueryParam(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}

/**
 * 모바일 여부 체크
 * @returns {boolean} 모바일 여부
 */
function isMobile() {
    return window.innerWidth <= 768;
}

/**
 * 복사하기
 * @param {string} text - 복사할 텍스트
 * @returns {Promise<boolean>} 복사 성공 여부
 */
async function copyToClipboard(text) {
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

// 전역 객체에 유틸리티 함수 추가
window.utils = {
    getCsrfToken,
    addCsrfToFetch,
    safeFetch,
    formatDate,
    getRelativeTime,
    formatNumber,
    abbreviateNumber,
    debounce,
    throttle,
    storage,
    showToast,
    confirmDialog,
    scrollToTop,
    getQueryParam,
    isMobile,
    copyToClipboard
};
