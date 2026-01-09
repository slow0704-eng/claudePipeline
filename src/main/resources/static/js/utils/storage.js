/**
 * storage.js - 로컬 스토리지 유틸리티
 */

/**
 * 로컬 스토리지에 저장
 * @param {string} key - 키
 * @param {*} value - 값 (자동으로 JSON 변환)
 * @returns {boolean} 성공 여부
 */
export function setItem(key, value) {
    try {
        localStorage.setItem(key, JSON.stringify(value));
        return true;
    } catch (error) {
        console.error('LocalStorage set error:', error);
        return false;
    }
}

/**
 * 로컬 스토리지에서 가져오기
 * @param {string} key - 키
 * @param {*} defaultValue - 기본값
 * @returns {*} 저장된 값 또는 기본값
 */
export function getItem(key, defaultValue = null) {
    try {
        const item = localStorage.getItem(key);
        return item ? JSON.parse(item) : defaultValue;
    } catch (error) {
        console.error('LocalStorage get error:', error);
        return defaultValue;
    }
}

/**
 * 로컬 스토리지에서 제거
 * @param {string} key - 키
 * @returns {boolean} 성공 여부
 */
export function removeItem(key) {
    try {
        localStorage.removeItem(key);
        return true;
    } catch (error) {
        console.error('LocalStorage remove error:', error);
        return false;
    }
}

/**
 * 로컬 스토리지 전체 삭제
 * @returns {boolean} 성공 여부
 */
export function clear() {
    try {
        localStorage.clear();
        return true;
    } catch (error) {
        console.error('LocalStorage clear error:', error);
        return false;
    }
}

/**
 * 키가 존재하는지 확인
 * @param {string} key - 키
 * @returns {boolean} 존재 여부
 */
export function hasItem(key) {
    return localStorage.getItem(key) !== null;
}

/**
 * 만료 시간이 있는 아이템 저장
 * @param {string} key - 키
 * @param {*} value - 값
 * @param {number} expiresInMs - 만료 시간 (밀리초)
 * @returns {boolean} 성공 여부
 */
export function setItemWithExpiry(key, value, expiresInMs) {
    const item = {
        value: value,
        expiry: Date.now() + expiresInMs
    };
    return setItem(key, item);
}

/**
 * 만료 시간을 확인하여 아이템 가져오기
 * @param {string} key - 키
 * @param {*} defaultValue - 기본값
 * @returns {*} 저장된 값 또는 기본값
 */
export function getItemWithExpiry(key, defaultValue = null) {
    const item = getItem(key, null);

    if (!item) {
        return defaultValue;
    }

    // 만료 시간 확인
    if (item.expiry && Date.now() > item.expiry) {
        removeItem(key);
        return defaultValue;
    }

    return item.value !== undefined ? item.value : defaultValue;
}

// 객체 형태로 내보내기 (하위 호환성)
const storage = {
    set: setItem,
    get: getItem,
    remove: removeItem,
    clear,
    has: hasItem,
    setWithExpiry: setItemWithExpiry,
    getWithExpiry: getItemWithExpiry
};

export default storage;
