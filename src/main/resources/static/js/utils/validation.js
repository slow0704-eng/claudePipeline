/**
 * validation.js - 유효성 검사 유틸리티
 */

/**
 * 이메일 유효성 검사
 * @param {string} email - 이메일 주소
 * @returns {boolean} 유효성 여부
 */
export function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

/**
 * 비밀번호 유효성 검사
 * @param {string} password - 비밀번호
 * @param {Object} options - 검사 옵션
 * @returns {Object} { valid: boolean, errors: string[] }
 */
export function validatePassword(password, options = {}) {
    const {
        minLength = 8,
        maxLength = 20,
        requireUppercase = true,
        requireLowercase = true,
        requireNumber = true,
        requireSpecial = true
    } = options;

    const errors = [];

    if (password.length < minLength) {
        errors.push(`비밀번호는 최소 ${minLength}자 이상이어야 합니다.`);
    }

    if (password.length > maxLength) {
        errors.push(`비밀번호는 최대 ${maxLength}자 이하여야 합니다.`);
    }

    if (requireUppercase && !/[A-Z]/.test(password)) {
        errors.push('대문자를 최소 1개 포함해야 합니다.');
    }

    if (requireLowercase && !/[a-z]/.test(password)) {
        errors.push('소문자를 최소 1개 포함해야 합니다.');
    }

    if (requireNumber && !/[0-9]/.test(password)) {
        errors.push('숫자를 최소 1개 포함해야 합니다.');
    }

    if (requireSpecial && !/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
        errors.push('특수문자를 최소 1개 포함해야 합니다.');
    }

    return {
        valid: errors.length === 0,
        errors
    };
}

/**
 * 사용자명 유효성 검사
 * @param {string} username - 사용자명
 * @param {Object} options - 검사 옵션
 * @returns {Object} { valid: boolean, error: string|null }
 */
export function validateUsername(username, options = {}) {
    const {
        minLength = 3,
        maxLength = 20,
        allowSpecial = false
    } = options;

    if (username.length < minLength) {
        return {
            valid: false,
            error: `사용자명은 최소 ${minLength}자 이상이어야 합니다.`
        };
    }

    if (username.length > maxLength) {
        return {
            valid: false,
            error: `사용자명은 최대 ${maxLength}자 이하여야 합니다.`
        };
    }

    if (!allowSpecial && !/^[a-zA-Z0-9가-힣]+$/.test(username)) {
        return {
            valid: false,
            error: '사용자명은 영문, 숫자, 한글만 사용할 수 있습니다.'
        };
    }

    return { valid: true, error: null };
}

/**
 * 전화번호 유효성 검사 (한국)
 * @param {string} phone - 전화번호
 * @returns {boolean} 유효성 여부
 */
export function isValidPhoneNumber(phone) {
    const phoneRegex = /^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/;
    return phoneRegex.test(phone);
}

/**
 * URL 유효성 검사
 * @param {string} url - URL
 * @returns {boolean} 유효성 여부
 */
export function isValidUrl(url) {
    try {
        new URL(url);
        return true;
    } catch (e) {
        return false;
    }
}

/**
 * 빈 값 체크
 * @param {*} value - 체크할 값
 * @returns {boolean} 빈 값 여부
 */
export function isEmpty(value) {
    if (value === null || value === undefined) return true;
    if (typeof value === 'string') return value.trim() === '';
    if (Array.isArray(value)) return value.length === 0;
    if (typeof value === 'object') return Object.keys(value).length === 0;
    return false;
}

/**
 * 숫자 범위 검사
 * @param {number} value - 값
 * @param {number} min - 최소값
 * @param {number} max - 최대값
 * @returns {boolean} 범위 내 여부
 */
export function isInRange(value, min, max) {
    const num = Number(value);
    return !isNaN(num) && num >= min && num <= max;
}

/**
 * 파일 확장자 검사
 * @param {File} file - 파일
 * @param {string[]} allowedExtensions - 허용된 확장자 배열 (예: ['jpg', 'png'])
 * @returns {boolean} 허용 여부
 */
export function isValidFileExtension(file, allowedExtensions) {
    if (!file || !file.name) return false;

    const extension = file.name.split('.').pop().toLowerCase();
    return allowedExtensions.map(ext => ext.toLowerCase()).includes(extension);
}

/**
 * 파일 크기 검사
 * @param {File} file - 파일
 * @param {number} maxSizeInMB - 최대 크기 (MB)
 * @returns {boolean} 크기 초과 여부
 */
export function isValidFileSize(file, maxSizeInMB) {
    if (!file) return false;

    const maxSizeInBytes = maxSizeInMB * 1024 * 1024;
    return file.size <= maxSizeInBytes;
}

/**
 * HTML 이스케이프
 * @param {string} text - 이스케이프할 텍스트
 * @returns {string} 이스케이프된 텍스트
 */
export function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * XSS 방지 문자열 정제
 * @param {string} text - 정제할 텍스트
 * @returns {string} 정제된 텍스트
 */
export function sanitizeString(text) {
    return text
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#x27;')
        .replace(/\//g, '&#x2F;');
}

// 기본 내보내기
export default {
    isValidEmail,
    validatePassword,
    validateUsername,
    isValidPhoneNumber,
    isValidUrl,
    isEmpty,
    isInRange,
    isValidFileExtension,
    isValidFileSize,
    escapeHtml,
    sanitizeString
};
