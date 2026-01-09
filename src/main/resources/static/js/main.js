/**
 * main.js - 메인 초기화 파일
 * 모든 모듈을 통합하고 초기화합니다
 */

// 유틸리티 모듈 임포트
import * as Ajax from './utils/ajax.js';
import * as DateFormat from './utils/dateFormat.js';
import * as Storage from './utils/storage.js';
import * as UI from './utils/ui.js';
import * as Validation from './utils/validation.js';
import * as UrlHelper from './utils/urlHelper.js';

// 도메인 모듈 임포트
import * as ReactionModule from './modules/reactionModule.js';
import * as CommentModule from './modules/commentModule.js';
import * as TopicModule from './modules/topicModule.js';
import * as BoardModule from './modules/boardModule.js';

/**
 * 전역 객체에 모듈 추가 (하위 호환성 및 템플릿에서 사용)
 */
function setupGlobalAPI() {
    // Utils 객체
    window.Utils = {
        ...Ajax,
        ...DateFormat,
        ...Storage,
        ...UI,
        ...Validation,
        ...UrlHelper
    };

    // 개별 모듈도 전역으로 노출
    window.Ajax = Ajax;
    window.DateFormat = DateFormat;
    window.Storage = Storage;
    window.UI = UI;
    window.Validation = Validation;
    window.UrlHelper = UrlHelper;

    // 도메인 모듈
    window.ReactionModule = ReactionModule;
    window.CommentModule = CommentModule;
    window.TopicModule = TopicModule;
    window.BoardModule = BoardModule;

    // 자주 사용하는 함수들을 window에 직접 노출
    window.showToast = UI.showToast;
    window.confirmDialog = UI.confirmDialog;
    window.getCsrfToken = Ajax.getCsrfToken;
    window.safeFetch = Ajax.safeFetch;
    window.formatDate = DateFormat.formatDate;
    window.getRelativeTime = DateFormat.getRelativeTime;

    console.log('Global API initialized');
}

/**
 * 모든 모듈 초기화
 */
function initializeAllModules() {
    // 반응 모듈 초기화
    if (typeof ReactionModule.initializeReactionModule === 'function') {
        ReactionModule.initializeReactionModule();
    }

    // 토픽 모듈 초기화
    if (typeof TopicModule.initializeTopicModule === 'function') {
        TopicModule.initializeTopicModule();
    }

    // 게시글 모듈 초기화
    if (typeof BoardModule.initializeBoardModule === 'function') {
        BoardModule.initializeBoardModule();
    }

    console.log('All modules initialized');
}

/**
 * 공통 이벤트 리스너 설정
 */
function setupEventListeners() {
    // 페이지 상단으로 버튼 (있는 경우)
    const scrollTopBtn = document.querySelector('.scroll-to-top');
    if (scrollTopBtn) {
        scrollTopBtn.addEventListener('click', () => {
            UI.scrollToTop(true);
        });

        // 스크롤 시 버튼 표시/숨김
        window.addEventListener('scroll', UI.throttle(() => {
            if (window.pageYOffset > 300) {
                scrollTopBtn.style.display = 'block';
            } else {
                scrollTopBtn.style.display = 'none';
            }
        }, 200));
    }

    console.log('Event listeners setup complete');
}

/**
 * 애플리케이션 초기화
 */
function initializeApp() {
    // 전역 API 설정
    setupGlobalAPI();

    // 모듈 초기화
    initializeAllModules();

    // 이벤트 리스너 설정
    setupEventListeners();

    console.log('Application initialized successfully');
}

// DOM 로드 완료 시 초기화
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeApp);
} else {
    // 이미 로드된 경우 즉시 실행
    initializeApp();
}

// ES6 모듈로 내보내기
export {
    Ajax,
    DateFormat,
    Storage,
    UI,
    Validation,
    UrlHelper,
    ReactionModule,
    CommentModule,
    TopicModule,
    BoardModule
};

// 디버깅을 위한 버전 정보
console.log('Board System v2.0 - Modular JavaScript Edition');
