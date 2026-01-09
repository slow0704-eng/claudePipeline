-- 댓글 생성 (comment)
-- 인기 게시글에 댓글과 답글 추가

-- Board ID 1 (Spring Boot 3.0 새로운 기능 정리)의 댓글
INSERT INTO comments (board_id, user_id, nickname, content, parent_comment_id, is_deleted, like_count, created_at, updated_at) VALUES
(1, 3, '개발왕초보', '정리 감사합니다! Spring Boot 3.0으로 업그레이드할 때 참고하겠습니다.', NULL, false, 0, NOW() - INTERVAL '29 days', NOW() - INTERVAL '29 days')),
(1, 4, 'JavaLover', 'GraalVM 네이티브 이미지 지원이 정말 기대되네요!', NULL, false, 0, NOW() - INTERVAL '28 days', NOW() - INTERVAL '28 days')),
(1, 5, 'DB전문가', 'Java 17 필수인가요? 17로 업그레이드 해야겠네요.', NULL, false, 0, NOW() - INTERVAL '27 days', NOW() - INTERVAL '27 days')),
(1, 2, '코딩마스터', '네, Java 17이 최소 요구사항입니다!', 3, false, 0, NOW() - INTERVAL '27 days', NOW() - INTERVAL '27 days')),
(1, 6, '프론트엔드러', '성능 개선이 얼마나 되나요?', NULL, false, 0, NOW() - INTERVAL '26 days', NOW() - INTERVAL '26 days')),
(1, 2, '코딩마스터', '프로젝트마다 다르지만 평균 20-30% 정도 개선되었다고 합니다.', 5, false, 0, NOW() - INTERVAL '26 days', NOW() - INTERVAL '26 days')),
(1, 7, '백엔드개발자', '너무 유용한 정보입니다. 북마크했어요!', NULL, false, 0, NOW() - INTERVAL '25 days', NOW() - INTERVAL '25 days')),

-- Board ID 2 (JPA N+1 문제 해결 방법)의 댓글
INSERT INTO comments (board_id, user_id, nickname, content, parent_comment_id, is_deleted, like_count, created_at, updated_at) VALUES
(2, 4, 'JavaLover', '항상 헷갈리던 부분인데 잘 정리해주셨네요!', NULL, false, 0, NOW() - INTERVAL '27 days', NOW() - INTERVAL '27 days')),
(2, 5, 'DB전문가', 'fetch join 사용할 때 페이징 이슈도 있죠?', NULL, false, 0, NOW() - INTERVAL '26 days', NOW() - INTERVAL '26 days')),
(2, 3, '개발왕초보', '맞아요, @BatchSize 어노테이션으로 해결할 수 있습니다.', 9, false, 0, NOW() - INTERVAL '26 days', NOW() - INTERVAL '26 days')),
(2, 6, '프론트엔드러', 'EntityGraph도 좋은 대안인 것 같아요.', NULL, false, 0, NOW() - INTERVAL '25 days', NOW() - INTERVAL '25 days')),
(2, 7, '백엔드개발자', '실무에서 많이 겪는 문제인데 도움이 많이 되었습니다!', NULL, false, 0, NOW() - INTERVAL '24 days', NOW() - INTERVAL '24 days')),
(2, 8, '풀스택지망생', '쿼리 개수 확인하는 방법도 알려주시면 좋을 것 같아요.', NULL, false, 0, NOW() - INTERVAL '23 days', NOW() - INTERVAL '23 days')),
(2, 3, '개발왕초보', 'p6spy 라이브러리 사용하시면 쉽게 확인 가능합니다!', 13, false, 0, NOW() - INTERVAL '23 days', NOW() - INTERVAL '23 days')),

-- Board ID 3 (RESTful API 설계 가이드)의 댓글
INSERT INTO comments (board_id, user_id, nickname, content, parent_comment_id, is_deleted, like_count, created_at, updated_at) VALUES
(3, 5, 'DB전문가', 'REST API 네이밍 규칙 참고하겠습니다.', NULL, false, 0, NOW() - INTERVAL '25 days', NOW() - INTERVAL '25 days')),
(3, 6, '프론트엔드러', 'HTTP 메소드 선택 기준이 명확하네요!', NULL, false, 0, NOW() - INTERVAL '24 days', NOW() - INTERVAL '24 days')),
(3, 7, '백엔드개발자', '에러 처리는 어떻게 하는게 좋을까요?', NULL, false, 0, NOW() - INTERVAL '23 days', NOW() - INTERVAL '23 days')),
(3, 4, 'JavaLover', 'RFC 7807 Problem Details 스펙 추천드립니다!', 17, false, 0, NOW() - INTERVAL '23 days', NOW() - INTERVAL '23 days')),
(3, 8, '풀스택지망생', '버전 관리는 URL에 포함하는게 맞나요?', NULL, false, 0, NOW() - INTERVAL '22 days', NOW() - INTERVAL '22 days')),
(3, 4, 'JavaLover', 'Header 방식과 URL 방식 모두 사용되는데, 프로젝트 상황에 맞게 선택하시면 됩니다.', 19, false, 0, NOW() - INTERVAL '22 days', NOW() - INTERVAL '22 days')),

-- Board ID 4 (Java Stream API 활용법)의 댓글
INSERT INTO comments (board_id, user_id, nickname, content, parent_comment_id, is_deleted, like_count, created_at, updated_at) VALUES
(4, 6, '프론트엔드러', 'Stream 사용하면 코드가 정말 깔끔해지네요!', NULL, false, 0, NOW() - INTERVAL '23 days', NOW() - INTERVAL '23 days')),
(4, 7, '백엔드개발자', '성능 차이는 어떤가요? for문보다 느리다고 들었는데...', NULL, false, 0, NOW() - INTERVAL '22 days', NOW() - INTERVAL '22 days')),
(4, 5, 'DB전문가', '간단한 연산은 큰 차이 없고, parallel stream 사용하면 오히려 빠를 수 있습니다.', 22, false, 0, NOW() - INTERVAL '22 days', NOW() - INTERVAL '22 days')),
(4, 8, '풀스택지망생', 'collect() 메소드 사용법이 헷갈렸는데 이해가 됐어요!', NULL, false, 0, NOW() - INTERVAL '21 days', NOW() - INTERVAL '21 days')),

-- Board ID 5 (데이터베이스 인덱스 최적화)의 댓글
INSERT INTO comments (board_id, user_id, nickname, content, parent_comment_id, is_deleted, like_count, created_at, updated_at) VALUES
(5, 7, '백엔드개발자', '인덱스를 무조건 많이 생성하는게 좋은줄 알았는데 아니었네요.', NULL, false, 0, NOW() - INTERVAL '21 days', NOW() - INTERVAL '21 days')),
(5, 8, '풀스택지망생', '복합 인덱스 순서가 중요하다는 것 처음 알았습니다!', NULL, false, 0, NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days')),
(5, 9, '알고리즘킬러', 'EXPLAIN 분석 결과 보는 법도 알려주시면 좋겠어요.', NULL, false, 0, NOW() - INTERVAL '19 days', NOW() - INTERVAL '19 days')),
(5, 6, '프론트엔드러', '다음 포스팅에서 다뤄보겠습니다!', 27, false, 0, NOW() - INTERVAL '19 days', NOW() - INTERVAL '19 days')),

-- Board ID 6 (React Hooks 완벽 가이드)의 댓글
INSERT INTO comments (board_id, user_id, nickname, content, parent_comment_id, is_deleted, like_count, created_at, updated_at) VALUES
(6, 8, '풀스택지망생', 'useState랑 useEffect 차이를 이제야 제대로 이해했어요.', NULL, false, 0, NOW() - INTERVAL '19 days', NOW() - INTERVAL '19 days')),
(6, 9, '알고리즘킬러', 'useCallback과 useMemo는 언제 사용하나요?', NULL, false, 0, NOW() - INTERVAL '18 days', NOW() - INTERVAL '18 days')),
(6, 6, '프론트엔드러', '렌더링 최적화가 필요한 경우에 사용합니다. 무분별한 사용은 오히려 성능 저하를 일으킬 수 있어요.', 30, false, 0, NOW() - INTERVAL '18 days', NOW() - INTERVAL '18 days')),
(6, 10, '데이터분석가', 'Custom Hook 만드는 법도 궁금합니다!', NULL, false, 0, NOW() - INTERVAL '17 days', NOW() - INTERVAL '17 days')),

-- Board ID 7 (Spring Security JWT 인증 구현)의 댓글
INSERT INTO comments (board_id, user_id, nickname, content, parent_comment_id, is_deleted, like_count, created_at, updated_at) VALUES
(7, 9, '알고리즘킬러', 'JWT 토큰 저장은 어디에 하는게 좋나요?', NULL, false, 0, NOW() - INTERVAL '17 days', NOW() - INTERVAL '17 days')),
(7, 10, '데이터분석가', 'Refresh Token 구현도 추가해주시면 좋겠어요!', NULL, false, 0, NOW() - INTERVAL '16 days', NOW() - INTERVAL '16 days')),
(7, 5, 'DB전문가', 'Refresh Token 구현은 다음 편에서 다루겠습니다!', 34, false, 0, NOW() - INTERVAL '16 days', NOW() - INTERVAL '16 days')),
(7, 11, 'AI연구원', '보안 관련해서 정말 도움이 많이 되었습니다.', NULL, false, 0, NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days')),

-- Board ID 8 (Git 협업 워크플로우)의 댓글
INSERT INTO comments (board_id, user_id, nickname, content, parent_comment_id, is_deleted, like_count, created_at, updated_at) VALUES
(8, 10, '데이터분석가', 'Git Flow vs GitHub Flow 차이를 이제야 알겠네요!', NULL, false, 0, NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days')),
(8, 11, 'AI연구원', 'Rebase vs Merge는 어떤 상황에 뭘 쓰나요?', NULL, false, 0, NOW() - INTERVAL '14 days', NOW() - INTERVAL '14 days')),
(8, 8, '풀스택지망생', 'Merge는 히스토리 보존, Rebase는 깔끔한 히스토리를 원할 때 사용합니다.', 38, false, 0, NOW() - INTERVAL '14 days', NOW() - INTERVAL '14 days')),
(8, 12, '클라우드엔지니어', '충돌 해결하는 팁도 알려주세요!', NULL, false, 0, NOW() - INTERVAL '13 days', NOW() - INTERVAL '13 days')),

-- Board ID 9 (Docker 컨테이너 기초)의 댓글
INSERT INTO comments (board_id, user_id, nickname, content, parent_comment_id, is_deleted, like_count, created_at, updated_at) VALUES
(9, 11, 'AI연구원', 'Docker 처음 시작하는데 많은 도움이 되었습니다!', NULL, false, 0, NOW() - INTERVAL '13 days', NOW() - INTERVAL '13 days')),
(9, 12, '클라우드엔지니어', 'docker-compose 사용법도 궁금해요.', NULL, false, 0, NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days')),
(9, 9, '알고리즘킬러', 'Docker Compose 편도 준비 중입니다!', 42, false, 0, NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days')),

-- Board ID 10 (TypeScript 기초부터 실전까지)의 댓글
INSERT INTO comments (board_id, user_id, nickname, content, parent_comment_id, is_deleted, like_count, created_at, updated_at) VALUES
(10, 12, '클라우드엔지니어', '타입 추론이 이렇게 강력한 줄 몰랐어요!', NULL, false, 0, NOW() - INTERVAL '11 days', NOW() - INTERVAL '11 days')),
(10, 13, 'DevOps전문가', 'Generic 사용법이 어렵네요 ㅠㅠ', NULL, false, 0, NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days')),
(10, 10, '데이터분석가', 'Generic은 처음엔 어렵지만 익숙해지면 정말 유용합니다!', 45, false, 0, NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days')),

-- 최근 게시글들의 댓글
INSERT INTO comments (board_id, user_id, nickname, content, parent_comment_id, is_deleted, like_count, created_at, updated_at) VALUES
(11, 13, 'DevOps전문가', '좋은 정보 감사합니다!', NULL, false, 0, NOW() - INTERVAL '9 days', NOW() - INTERVAL '9 days')),
(11, 14, '보안전문가', '실무에 바로 적용해봐야겠어요.', NULL, false, 0, NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days')),

(12, 14, '보안전문가', '이런 내용 찾고 있었는데 감사합니다!', NULL, false, 0, NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days')),
(12, 15, '모바일개발자', '더 자세한 설명 부탁드려요.', NULL, false, 0, NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days')),

(13, 15, '모바일개발자', '정말 유용한 글이네요!', NULL, false, 0, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days')),
(13, 16, 'iOS전문가', '북마크 했습니다!', NULL, false, 0, NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days')),

(14, 16, 'iOS전문가', '설명이 이해하기 쉬워요.', NULL, false, 0, NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days')),
(14, 17, 'Android마스터', '도움 많이 받았습니다.', NULL, false, 0, NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days')),

(15, 17, 'Android마스터', '좋은 글 감사합니다!', NULL, false, 0, NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days')),

(16, 18, 'React개발자', '유익한 정보네요!', NULL, false, 0, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days')),

(17, 19, 'Vue마스터', '잘 읽었습니다!', NULL, false, 0, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day')),

(18, 20, 'Angular전문가', '최고의 글이에요!', NULL, false, 0, NOW() - INTERVAL '12 hours', NOW() - INTERVAL '12 hours')),

(19, 21, 'Node.js개발자', '감사합니다!', NULL, false, 0, NOW() - INTERVAL '6 hours', NOW() - INTERVAL '6 hours')),

(20, 22, 'Python마스터', '유용해요!', NULL, false, 0, NOW() - INTERVAL '3 hours', NOW() - INTERVAL '3 hours'));

-- 총 약 60개의 댓글 (답글 포함)
