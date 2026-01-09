/**
 * ajax.js - AJAX/Fetch 유틸리티
 * CSRF 토큰 처리 및 안전한 fetch 래퍼
 */

/**
 * CSRF 토큰 가져오기
 * @returns {Object} CSRF 토큰 및 헤더 정보
 */
export function getCsrfToken() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

    return { token, header };
}

/**
 * Fetch 요청에 CSRF 토큰 추가
 * @param {Object} options - fetch options
 * @returns {Object} CSRF 토큰이 추가된 options
 */
export function addCsrfToFetch(options = {}) {
    const { token, header } = getCsrfToken();

    if (!options.headers) {
        options.headers = {};
    }

    if (token && header) {
        options.headers[header] = token;
    }

    // Content-Type이 명시되지 않았고 body가 JSON인 경우 자동 설정
    if (!options.headers['Content-Type'] && options.body && typeof options.body === 'string') {
        try {
            JSON.parse(options.body);
            options.headers['Content-Type'] = 'application/json';
        } catch (e) {
            // JSON이 아니면 무시
        }
    }

    return options;
}

/**
 * 안전한 Fetch 래퍼 (CSRF 토큰 자동 추가)
 * @param {string} url - 요청 URL
 * @param {Object} options - fetch options
 * @returns {Promise<Response>} fetch Promise
 */
export async function safeFetch(url, options = {}) {
    return fetch(url, addCsrfToFetch(options));
}

/**
 * JSON을 반환하는 안전한 Fetch 래퍼
 * @param {string} url - 요청 URL
 * @param {Object} options - fetch options
 * @returns {Promise<any>} JSON 응답
 * @throws {Error} HTTP 오류 또는 JSON 파싱 오류
 */
export async function safeFetchJson(url, options = {}) {
    const response = await safeFetch(url, options);

    if (!response.ok) {
        let errorMessage = `HTTP error! status: ${response.status}`;
        try {
            const errorData = await response.json();
            errorMessage = errorData.message || errorData.error || errorMessage;
        } catch (e) {
            // JSON 파싱 실패 시 기본 메시지 사용
        }
        throw new Error(errorMessage);
    }

    return response.json();
}

/**
 * POST 요청 헬퍼
 * @param {string} url - 요청 URL
 * @param {Object} data - 전송할 데이터
 * @param {Object} options - 추가 옵션
 * @returns {Promise<any>} JSON 응답
 */
export async function post(url, data, options = {}) {
    return safeFetchJson(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
        ...options
    });
}

/**
 * PUT 요청 헬퍼
 * @param {string} url - 요청 URL
 * @param {Object} data - 전송할 데이터
 * @param {Object} options - 추가 옵션
 * @returns {Promise<any>} JSON 응답
 */
export async function put(url, data, options = {}) {
    return safeFetchJson(url, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
        ...options
    });
}

/**
 * DELETE 요청 헬퍼
 * @param {string} url - 요청 URL
 * @param {Object} options - 추가 옵션
 * @returns {Promise<any>} JSON 응답
 */
export async function del(url, options = {}) {
    return safeFetchJson(url, {
        method: 'DELETE',
        ...options
    });
}

/**
 * GET 요청 헬퍼
 * @param {string} url - 요청 URL
 * @param {Object} params - 쿼리 파라미터
 * @param {Object} options - 추가 옵션
 * @returns {Promise<any>} JSON 응답
 */
export async function get(url, params = {}, options = {}) {
    const queryString = new URLSearchParams(params).toString();
    const fullUrl = queryString ? `${url}?${queryString}` : url;

    return safeFetchJson(fullUrl, {
        method: 'GET',
        ...options
    });
}

// 기본 내보내기 (하위 호환성)
export default {
    getCsrfToken,
    addCsrfToFetch,
    safeFetch,
    safeFetchJson,
    post,
    put,
    del,
    get
};
