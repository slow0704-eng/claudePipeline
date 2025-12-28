# Mock Data for Board Application

개발용 목 데이터 SQL 스크립트입니다.

## 📊 생성되는 데이터

- **사용자 10명** (개발자 페르소나, 비밀번호: `test1234`)
  - 김개발, 이스프링, 박자바, 최리액트, DB정, 강데브옵스, 오백엔드, 윤풀스택, 한클라우드, 관리자

- **카테고리 7개**
  - Spring Boot, React, Database, DevOps, 코드리뷰, 프로젝트, 질문

- **게시글 20개** (개발 주제)
  - Spring Boot 팁, JPA N+1 해결, React Query, PostgreSQL vs MySQL 등
  - 다양한 조회수, 좋아요 수

- **댓글 48개**
  - 대댓글 포함
  - 실제 개발 토론 내용

- **상호작용**
  - 좋아요: 게시글/댓글 좋아요 다수
  - 팔로우: 사용자 간 상호 팔로우
  - 북마크: 다양한 게시글 북마크

## 🚀 Render PostgreSQL에 데이터 삽입 방법

### 방법 1: Render 대시보드 (추천)

1. **Render 대시보드** 접속
2. **board-postgres** 데이터베이스 클릭
3. 상단의 **"Shell"** 탭 클릭
4. 각 SQL 파일 내용을 복사해서 실행:

```bash
# 순서대로 실행
01_mock_users.sql
02_mock_categories.sql
03_mock_boards.sql
04_mock_comments.sql
05_mock_interactions.sql
```

### 방법 2: psql 사용 (로컬)

```bash
# External Database URL 사용
psql postgresql://boarduser:RgdjzmPYsWj5GgxHs3feHNCDOQqbZ4aV@dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com:5432/boarddb_0u9z

# 파일 실행
\i database/mock-data/01_mock_users.sql
\i database/mock-data/02_mock_categories.sql
\i database/mock-data/03_mock_boards.sql
\i database/mock-data/04_mock_comments.sql
\i database/mock-data/05_mock_interactions.sql
```

### 방법 3: 한 번에 실행

```bash
psql [DATABASE_URL] < database/mock-data/00_insert_all.sql
```

## 🔐 로그인 정보

모든 사용자 비밀번호: **test1234**

| Username | Nickname | Email |
|----------|----------|-------|
| kimcoder | 김개발 | kimcoder@example.com |
| leespring | 이스프링 | leespring@example.com |
| parkjs | 박자바 | parkjs@example.com |
| choireact | 최리액트 | choireact@example.com |
| jungdb | DB정 | jungdb@example.com |
| kangdevops | 강데브옵스 | kangdevops@example.com |
| ohbackend | 오백엔드 | ohbackend@example.com |
| yoonfull | 윤풀스택 | yoonfull@example.com |
| hancloud | 한클라우드 | hancloud@example.com |
| admin | 관리자 | admin@example.com |

## 📝 데이터 확인

```sql
-- 데이터 개수 확인
SELECT 'Users: ' || COUNT(*) FROM users;
SELECT 'Categories: ' || COUNT(*) FROM category;
SELECT 'Boards: ' || COUNT(*) FROM board;
SELECT 'Comments: ' || COUNT(*) FROM comment;
SELECT 'Likes: ' || COUNT(*) FROM likes;
SELECT 'Follows: ' || COUNT(*) FROM follow;
SELECT 'Bookmarks: ' || COUNT(*) FROM bookmark;
```

## 🗑️ 데이터 삭제

테스트 데이터를 삭제하려면:

```sql
SET session_replication_role = 'replica';
TRUNCATE TABLE bookmark, follow, likes, comment, attachment, board, category, users RESTART IDENTITY CASCADE;
SET session_replication_role = 'origin';
```

## ⚠️ 주의사항

- 프로덕션 환경에서는 사용하지 마세요
- 비밀번호가 모두 동일하므로 개발/테스트용으로만 사용
- 기존 데이터와 충돌할 수 있으니 빈 데이터베이스에 사용 권장
