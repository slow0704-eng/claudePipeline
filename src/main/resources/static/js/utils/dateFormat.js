/**
 * dateFormat.js - 날짜 포맷팅 유틸리티
 */

/**
 * 날짜 포맷팅
 * @param {Date|string} date - 포맷할 날짜
 * @param {string} format - 포맷 형식 (default: 'YYYY-MM-DD HH:mm')
 * @returns {string} 포맷된 날짜 문자열
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm') {
    const d = new Date(date);

    if (isNaN(d.getTime())) {
        return '';
    }

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
export function getRelativeTime(date) {
    const d = new Date(date);
    const now = new Date();
    const diff = now - d;

    if (isNaN(diff) || diff < 0) {
        return formatDate(d, 'YYYY-MM-DD');
    }

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
 * 한국어 형식 날짜 포맷팅
 * @param {Date|string} date - 날짜
 * @param {boolean} includeTime - 시간 포함 여부
 * @returns {string} 포맷된 날짜 (예: "2024년 1월 4일" 또는 "2024년 1월 4일 오후 3:45")
 */
export function formatKoreanDate(date, includeTime = false) {
    const d = new Date(date);

    if (isNaN(d.getTime())) {
        return '';
    }

    const year = d.getFullYear();
    const month = d.getMonth() + 1;
    const day = d.getDate();

    let result = `${year}년 ${month}월 ${day}일`;

    if (includeTime) {
        const hours = d.getHours();
        const minutes = String(d.getMinutes()).padStart(2, '0');
        const period = hours < 12 ? '오전' : '오후';
        const displayHours = hours % 12 || 12;

        result += ` ${period} ${displayHours}:${minutes}`;
    }

    return result;
}

/**
 * ISO 날짜를 로컬 날짜로 변환
 * @param {string} isoString - ISO 날짜 문자열
 * @returns {Date} 로컬 Date 객체
 */
export function parseISODate(isoString) {
    return new Date(isoString);
}

/**
 * 두 날짜 사이의 일수 계산
 * @param {Date|string} date1 - 시작 날짜
 * @param {Date|string} date2 - 종료 날짜
 * @returns {number} 일수 차이
 */
export function daysBetween(date1, date2) {
    const d1 = new Date(date1);
    const d2 = new Date(date2);
    const diffTime = Math.abs(d2 - d1);
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
}

// 기본 내보내기
export default {
    formatDate,
    getRelativeTime,
    formatKoreanDate,
    parseISODate,
    daysBetween
};
