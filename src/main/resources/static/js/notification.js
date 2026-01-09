/**
 * notification.js - 알림 관련 JavaScript
 * 알림 목록 로드, 읽음 처리
 */

/**
 * 알림 목록 로드
 */
async function loadNotifications() {
    const notificationList = document.getElementById('notificationList');
    if (!notificationList) return;

    // 로딩 표시
    notificationList.innerHTML = '<div class="loading">로딩 중...</div>';

    try {
        const response = await fetch('/api/notifications/recent?limit=10');

        if (!response.ok) {
            throw new Error('Failed to load notifications');
        }

        const notifications = await response.json();

        if (!notifications || notifications.length === 0) {
            notificationList.innerHTML = '<div class="loading">알림이 없습니다.</div>';
            return;
        }

        // 알림 목록 HTML 생성
        const notificationHTML = notifications.map(notification => {
            const isRead = notification.isRead || notification.read;
            const notifClass = isRead ? 'notification-item read' : 'notification-item unread';

            return `
                <div class="${notifClass}" data-id="${notification.id}">
                    <div class="notification-content">
                        <div class="notification-icon">
                            ${getNotificationIcon(notification.type)}
                        </div>
                        <div class="notification-body">
                            <div class="notification-message">${escapeHtml(notification.message)}</div>
                            <div class="notification-time">${formatTime(notification.createdAt)}</div>
                        </div>
                    </div>
                    ${!isRead ? `
                    <button class="mark-read-btn" onclick="markAsRead(${notification.id})" title="읽음으로 표시">
                        <i class="fas fa-check"></i>
                    </button>
                    ` : ''}
                </div>
            `;
        }).join('');

        notificationList.innerHTML = notificationHTML;

        // CSS 스타일 추가 (인라인)
        addNotificationStyles();

    } catch (error) {
        console.error('Error loading notifications:', error);
        notificationList.innerHTML = '<div class="loading">알림을 불러올 수 없습니다.</div>';
    }
}

/**
 * 알림 타입에 따른 아이콘 반환
 * @param {string} type - 알림 타입
 * @returns {string} HTML 아이콘
 */
function getNotificationIcon(type) {
    const icons = {
        'COMMENT': '<i class="fas fa-comment" style="color: #007bff;"></i>',
        'REPLY': '<i class="fas fa-reply" style="color: #28a745;"></i>',
        'LIKE': '<i class="fas fa-heart" style="color: #dc3545;"></i>',
        'FOLLOW': '<i class="fas fa-user-plus" style="color: #17a2b8;"></i>',
        'MENTION': '<i class="fas fa-at" style="color: #ffc107;"></i>',
        'SYSTEM': '<i class="fas fa-bell" style="color: #6c757d;"></i>'
    };

    return icons[type] || icons['SYSTEM'];
}

/**
 * 시간 포맷팅
 * @param {string} dateString - 날짜 문자열
 * @returns {string} 포맷된 시간
 */
function formatTime(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;

    const minute = 60 * 1000;
    const hour = 60 * minute;
    const day = 24 * hour;

    if (diff < minute) {
        return '방금 전';
    } else if (diff < hour) {
        return Math.floor(diff / minute) + '분 전';
    } else if (diff < day) {
        return Math.floor(diff / hour) + '시간 전';
    } else if (diff < 7 * day) {
        return Math.floor(diff / day) + '일 전';
    } else {
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }
}

/**
 * HTML 이스케이프
 * @param {string} text - 이스케이프할 텍스트
 * @returns {string} 이스케이프된 텍스트
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * 단일 알림 읽음 처리
 * @param {number} notificationId - 알림 ID
 */
async function markAsRead(notificationId) {
    try {
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

        const headers = {
            'Content-Type': 'application/json'
        };

        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }

        const response = await fetch(`/api/notifications/${notificationId}/read`, {
            method: 'POST',
            headers: headers
        });

        if (response.ok) {
            // UI 업데이트
            const notificationItem = document.querySelector(`[data-id="${notificationId}"]`);
            if (notificationItem) {
                notificationItem.classList.remove('unread');
                notificationItem.classList.add('read');

                // 읽음 버튼 제거
                const markReadBtn = notificationItem.querySelector('.mark-read-btn');
                if (markReadBtn) {
                    markReadBtn.remove();
                }
            }

            // 배지 카운트 업데이트
            if (typeof loadHeaderCounts === 'function') {
                loadHeaderCounts();
            }
        }
    } catch (error) {
        console.error('Error marking notification as read:', error);
    }
}

/**
 * 모든 알림 읽음 처리
 */
async function markAllAsRead() {
    try {
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

        const headers = {
            'Content-Type': 'application/json'
        };

        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }

        const response = await fetch('/api/notifications/read-all', {
            method: 'POST',
            headers: headers
        });

        if (response.ok) {
            // 알림 목록 다시 로드
            loadNotifications();

            // 배지 카운트 업데이트
            if (typeof loadHeaderCounts === 'function') {
                loadHeaderCounts();
            }
        }
    } catch (error) {
        console.error('Error marking all notifications as read:', error);
    }
}

/**
 * 알림 스타일 추가 (인라인)
 */
function addNotificationStyles() {
    // 이미 스타일이 추가되어 있는지 확인
    if (document.getElementById('notification-styles')) return;

    const style = document.createElement('style');
    style.id = 'notification-styles';
    style.textContent = `
        .notification-item {
            padding: 12px 15px;
            border-bottom: 1px solid #f0f0f0;
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            transition: background-color 0.2s;
        }

        .notification-item:last-child {
            border-bottom: none;
        }

        .notification-item:hover {
            background-color: #f8f9fa;
        }

        .notification-item.unread {
            background-color: #e7f3ff;
        }

        .notification-content {
            display: flex;
            gap: 12px;
            flex: 1;
        }

        .notification-icon {
            font-size: 20px;
            flex-shrink: 0;
        }

        .notification-body {
            flex: 1;
        }

        .notification-message {
            color: #333;
            font-size: 14px;
            line-height: 1.4;
            margin-bottom: 4px;
        }

        .notification-time {
            color: #999;
            font-size: 12px;
        }

        .mark-read-btn {
            background: none;
            border: none;
            color: #667eea;
            cursor: pointer;
            padding: 4px 8px;
            border-radius: 4px;
            transition: all 0.2s;
        }

        .mark-read-btn:hover {
            background-color: #667eea;
            color: white;
        }
    `;

    document.head.appendChild(style);
}
