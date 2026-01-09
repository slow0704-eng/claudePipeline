/**
 * urlHelper.js - URL 관련 유틸리티
 */

/**
 * URL 쿼리 파라미터 가져오기
 * @param {string} name - 파라미터 이름
 * @returns {string|null} 파라미터 값
 */
export function getQueryParam(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}

/**
 * 모든 쿼리 파라미터 가져오기
 * @returns {Object} 파라미터 객체
 */
export function getAllQueryParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const params = {};

    for (const [key, value] of urlParams) {
        params[key] = value;
    }

    return params;
}

/**
 * 쿼리 파라미터 추가/업데이트
 * @param {string} name - 파라미터 이름
 * @param {string} value - 파라미터 값
 * @param {boolean} reload - 페이지 리로드 여부
 */
export function setQueryParam(name, value, reload = false) {
    const url = new URL(window.location);
    url.searchParams.set(name, value);

    if (reload) {
        window.location.href = url.toString();
    } else {
        window.history.pushState({}, '', url);
    }
}

/**
 * 쿼리 파라미터 제거
 * @param {string} name - 파라미터 이름
 * @param {boolean} reload - 페이지 리로드 여부
 */
export function removeQueryParam(name, reload = false) {
    const url = new URL(window.location);
    url.searchParams.delete(name);

    if (reload) {
        window.location.href = url.toString();
    } else {
        window.history.pushState({}, '', url);
    }
}

/**
 * 객체를 쿼리 스트링으로 변환
 * @param {Object} params - 파라미터 객체
 * @returns {string} 쿼리 스트링
 */
export function objectToQueryString(params) {
    return new URLSearchParams(params).toString();
}

/**
 * 현재 페이지 URL 복사
 * @returns {Promise<boolean>} 복사 성공 여부
 */
export async function copyCurrentUrl() {
    try {
        await navigator.clipboard.writeText(window.location.href);
        return true;
    } catch (error) {
        console.error('URL copy failed:', error);
        return false;
    }
}

/**
 * 페이지 리디렉션
 * @param {string} url - 이동할 URL
 * @param {number} delay - 지연 시간 (ms, 선택적)
 */
export function redirect(url, delay = 0) {
    if (delay > 0) {
        setTimeout(() => {
            window.location.href = url;
        }, delay);
    } else {
        window.location.href = url;
    }
}

/**
 * 새 탭에서 열기
 * @param {string} url - 열 URL
 */
export function openInNewTab(url) {
    window.open(url, '_blank', 'noopener,noreferrer');
}

// 기본 내보내기
export default {
    getQueryParam,
    getAllQueryParams,
    setQueryParam,
    removeQueryParam,
    objectToQueryString,
    copyCurrentUrl,
    redirect,
    openInNewTab
};
