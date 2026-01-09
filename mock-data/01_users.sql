-- 목 사용자 계정 50개 생성
-- 비밀번호: 모두 "1234" (BCrypt 암호화)

-- 기존 데이터 삭제 (중복 방지)
DELETE FROM board_hashtag;
DELETE FROM attachment;
DELETE FROM comment;
DELETE FROM board_like;
DELETE FROM bookmark;
DELETE FROM user_follow;
DELETE FROM board;
DELETE FROM hashtag;
DELETE FROM users WHERE username != 'admin';

-- 일반 회원 50명 (admin은 이미 존재하므로 제외)
INSERT INTO users (username, password, nickname, email, name, role, enabled, created_at) VALUES
('user001', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', '코딩마스터', 'user001@email.com', '김철수', 'MEMBER', true, NOW() - INTERVAL '300 days'),
('user002', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', '개발왕초보', 'user002@email.com', '이영희', 'MEMBER', true, NOW() - INTERVAL '280 days'),
('user003', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'JavaLover', 'user003@email.com', '박민수', 'MEMBER', true, NOW() - INTERVAL '250 days'),
('user004', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'SpringMaster', 'user004@email.com', '최지은', 'MEMBER', true, NOW() - INTERVAL '220 days'),
('user005', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'DB전문가', 'user005@email.com', '정다은', 'MEMBER', true, NOW() - INTERVAL '200 days'),
('user006', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', '프론트엔드러', 'user006@email.com', '강호준', 'MEMBER', true, NOW() - INTERVAL '180 days'),
('user007', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', '백엔드개발자', 'user007@email.com', '윤서연', 'MEMBER', true, NOW() - INTERVAL '160 days'),
('user008', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', '풀스택지망생', 'user008@email.com', '임태희', 'MEMBER', true, NOW() - INTERVAL '140 days'),
('user009', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', '알고리즘킬러', 'user009@email.com', '오하늘', 'MEMBER', true, NOW() - INTERVAL '120 days'),
('user010', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', '데이터분석가', 'user010@email.com', '서준호', 'MEMBER', true, NOW() - INTERVAL '100 days'),
('user011', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'AI연구원', 'user011@email.com', '한지우', 'MEMBER', true, NOW() - INTERVAL '90 days'),
('user012', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', '클라우드엔지니어', 'user012@email.com', '권수아', 'MEMBER', true, NOW() - INTERVAL '80 days'),
('user013', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'DevOps전문가', 'user013@email.com', '송민재', 'MEMBER', true, NOW() - INTERVAL '70 days'),
('user014', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', '보안전문가', 'user014@email.com', '조예린', 'MEMBER', true, NOW() - INTERVAL '60 days'),
('user015', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', '모바일개발자', 'user015@email.com', '배현우', 'MEMBER', true, NOW() - INTERVAL '50 days'),
('user016', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'iOS전문가', 'user016@email.com', '신채원', 'MEMBER', true, NOW() - INTERVAL '45 days'),
('user017', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'Android마스터', 'user017@email.com', '홍시현', 'MEMBER', true, NOW() - INTERVAL '40 days'),
('user018', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'React개발자', 'user018@email.com', '류준서', 'MEMBER', true, NOW() - INTERVAL '35 days'),
('user019', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'Vue마스터', 'user019@email.com', '문서윤', 'MEMBER', true, NOW() - INTERVAL '30 days'),
('user020', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'Angular전문가', 'user020@email.com', '진도현', 'MEMBER', true, NOW() - INTERVAL '28 days'),
('user021', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'Node.js개발자', 'user021@email.com', '남주아', 'MEMBER', true, NOW() - INTERVAL '26 days'),
('user022', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'Python마스터', 'user022@email.com', '표지훈', 'MEMBER', true, NOW() - INTERVAL '24 days'),
('user023', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'Django전문가', 'user023@email.com', '탁은서', 'MEMBER', true, NOW() - INTERVAL '22 days'),
('user024', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'Flask개발자', 'user024@email.com', '변건우', 'MEMBER', true, NOW() - INTERVAL '20 days'),
('user025', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'Go언어러버', 'user025@email.com', '석예원', 'MEMBER', true, NOW() - INTERVAL '18 days'),
('user026', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'Rust전도사', 'user026@email.com', '길민준', 'MEMBER', true, NOW() - INTERVAL '16 days'),
('user027', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'C++마스터', 'user027@email.com', '노서아', 'MEMBER', true, NOW() - INTERVAL '14 days'),
('user028', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'Kotlin개발자', 'user028@email.com', '도연우', 'MEMBER', true, NOW() - INTERVAL '12 days'),
('user029', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'Swift전문가', 'user029@email.com', '하시우', 'MEMBER', true, NOW() - INTERVAL '10 days'),
('user030', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'TypeScript러버', 'user030@email.com', '목지안', 'MEMBER', true, NOW() - INTERVAL '9 days'),
('user031', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'GraphQL전문가', 'user031@email.com', '유다온', 'MEMBER', true, NOW() - INTERVAL '8 days'),
('user032', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'REST API마스터', 'user032@email.com', '차하준', 'MEMBER', true, NOW() - INTERVAL '7 days'),
('user033', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'MSA전문가', 'user033@email.com', '추소율', 'MEMBER', true, NOW() - INTERVAL '6 days'),
('user034', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'Docker마스터', 'user034@email.com', '피우진', 'MEMBER', true, NOW() - INTERVAL '5 days'),
('user035', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'K8s전문가', 'user035@email.com', '함수빈', 'MEMBER', true, NOW() - INTERVAL '4 days'),
('user036', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'AWS전문가', 'user036@email.com', '곽하율', 'MEMBER', true, NOW() - INTERVAL '3 days'),
('user037', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'Azure마스터', 'user037@email.com', '성시온', 'MEMBER', true, NOW() - INTERVAL '3 days'),
('user038', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'GCP전문가', 'user038@email.com', '양윤서', 'MEMBER', true, NOW() - INTERVAL '2 days'),
('user039', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'CI/CD마스터', 'user039@email.com', '민재원', 'MEMBER', true, NOW() - INTERVAL '2 days'),
('user040', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'Git전문가', 'user040@email.com', '복하윤', 'MEMBER', true, NOW() - INTERVAL '1 day'),
('user041', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', '코드리뷰어', 'user041@email.com', '설지호', 'MEMBER', true, NOW() - INTERVAL '1 day'),
('user042', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', '테스트마스터', 'user042@email.com', '옥서준', 'MEMBER', true, NOW() - INTERVAL '12 hours'),
('user043', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'QA전문가', 'user043@email.com', '엄다은', 'MEMBER', true, NOW() - INTERVAL '10 hours'),
('user044', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'PM지망생', 'user044@email.com', '예민성', 'MEMBER', true, NOW() - INTERVAL '8 hours'),
('user045', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'PO지망생', 'user045@email.com', '음채연', 'MEMBER', true, NOW() - INTERVAL '6 hours'),
('user046', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'UX디자이너', 'user046@email.com', '편도윤', 'MEMBER', true, NOW() - INTERVAL '4 hours'),
('user047', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', 'UI디자이너', 'user047@email.com', '빈하은', 'MEMBER', true, NOW() - INTERVAL '2 hours'),
('user048', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', '취준생', 'user048@email.com', '황예준', 'MEMBER', true, NOW() - INTERVAL '1 hour'),
('user049', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', '신입개발자', 'user049@email.com', '모지율', 'MEMBER', true, NOW() - INTERVAL '30 minutes'),
('user050', '$2a$10$N.zmdr9k7uOEXYqUc6JN4.Zq5lP8P5P5P5P5P5P5P5P5P5P5P5P5Pu', '주니어개발자', 'user050@email.com', '태서현', 'MEMBER', true, NOW() - INTERVAL '10 minutes');
