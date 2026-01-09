/**
 * header.js - 헤더 관련 JavaScript
 * 드롭다운, 모바일 메뉴, 알림/메시지 카운트 로드
 */

/**
 * 드롭다운 토글
 * @param {string} dropdownId - 드롭다운 메뉴 ID
 * @param {Event} event - 이벤트 객체
 */
function toggleDropdown(dropdownId, event) {
    event.preventDefault();
    event.stopPropagation();

    const dropdown = document.getElementById(dropdownId);
    if (!dropdown) return;

    // 다른 모든 드롭다운 닫기
    document.querySelectorAll('.dropdown-menu').forEach(menu => {
        if (menu.id !== dropdownId && menu.classList.contains('show')) {
            menu.classList.remove('show');
        }
    });

    // 현재 드롭다운 토글
    dropdown.classList.toggle('show');

    // 알림 드롭다운이 열릴 때 알림 목록 로드
    if (dropdownId === 'notification-dropdown' && dropdown.classList.contains('show')) {
        if (typeof loadNotifications === 'function') {
            loadNotifications();
        }
    }
}

/**
 * 모바일 메뉴 토글
 */
function toggleMobileMenu() {
    const mobileMenu = document.getElementById('mobile-menu');
    if (mobileMenu) {
        mobileMenu.classList.toggle('show');
    }
}

/**
 * 알림 및 메시지 카운트 로드
 */
async function loadHeaderCounts() {
    try {
        // 알림 카운트 로드
        const notificationResponse = await fetch('/api/notifications/count');
        if (notificationResponse.ok) {
            const notificationData = await notificationResponse.json();
            updateBadge('notificationBadge', notificationData.count || notificationData.unreadCount || 0);
        }
    } catch (error) {
        console.error('Failed to load notification count:', error);
    }

    try {
        // 메시지 카운트 로드 (API가 있는 경우)
        const messageResponse = await fetch('/messages/unread-count');
        if (messageResponse.ok) {
            const messageData = await messageResponse.json();
            updateBadge('messageBadge', messageData.count || messageData.unreadCount || 0);
        }
    } catch (error) {
        // 메시지 API가 없으면 무시
        console.debug('Message count API not available');
    }
}

/**
 * 배지 업데이트
 * @param {string} badgeId - 배지 엘리먼트 ID
 * @param {number} count - 카운트 숫자
 */
function updateBadge(badgeId, count) {
    const badge = document.getElementById(badgeId);
    if (!badge) return;

    if (count > 0) {
        badge.textContent = count > 99 ? '99+' : count;
        badge.style.display = 'block';
    } else {
        badge.style.display = 'none';
    }
}

/**
 * 외부 클릭 시 드롭다운 닫기
 */
function setupDropdownOutsideClick() {
    document.addEventListener('click', function(event) {
        // 드롭다운 버튼이나 드롭다운 내부를 클릭한 경우가 아니면 모든 드롭다운 닫기
        if (!event.target.closest('.dropdown') && !event.target.closest('.header-notification')) {
            document.querySelectorAll('.dropdown-menu').forEach(menu => {
                menu.classList.remove('show');
            });
        }
    });
}

/**
 * 검색 입력 자동완성 (선택적)
 */
function setupSearchAutocomplete() {
    const searchInput = document.getElementById('headerSearchInput');
    if (!searchInput) return;

    let debounceTimer;

    searchInput.addEventListener('input', function() {
        clearTimeout(debounceTimer);

        const keyword = this.value.trim();
        if (keyword.length < 2) return;

        debounceTimer = setTimeout(async () => {
            try {
                const response = await fetch(`/api/search/suggestions?keyword=${encodeURIComponent(keyword)}`);
                if (response.ok) {
                    const suggestions = await response.json();
                    // 자동완성 UI 표시 (구현 필요)
                    console.log('Search suggestions:', suggestions);
                }
            } catch (error) {
                console.error('Search autocomplete error:', error);
            }
        }, 300);
    });
}

/**
 * 초기화
 */
document.addEventListener('DOMContentLoaded', function() {
    // 드롭다운 외부 클릭 설정
    setupDropdownOutsideClick();

    // 검색 자동완성 설정 (선택적)
    // setupSearchAutocomplete();

    // 헤더 카운트 로드
    loadHeaderCounts();

    // 주기적으로 카운트 갱신 (30초마다)
    setInterval(loadHeaderCounts, 30000);

    // 모바일 메뉴 외부 클릭 시 닫기
    document.addEventListener('click', function(event) {
        const mobileMenu = document.getElementById('mobile-menu');
        const mobileMenuToggle = document.querySelector('.mobile-menu-toggle');

        if (mobileMenu &&
            mobileMenu.classList.contains('show') &&
            !mobileMenu.contains(event.target) &&
            !mobileMenuToggle.contains(event.target)) {
            mobileMenu.classList.remove('show');
        }
    });
});
