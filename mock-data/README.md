# 목 데이터 가이드

## 📦 생성된 파일 목록

### SQL 파일 (mock-data 폴더)
1. **01_users.sql** - 사용자 50명 + 관리자 1명
2. **02_boards.sql** - 게시글 300+개 (임시저장 5개 포함)
3. **03_follows.sql** - 팔로우 관계 200개
4. **04_likes.sql** - 게시글 좋아요 300+개
5. **05_bookmarks.sql** - 북마크 100개
6. **06_comments.sql** - 댓글 60개 (답글 포함)
7. **07_hashtags.sql** - 해시태그 78개 + 연결 90+개

### 실행 스크립트
- **insert_mock_data.bat** - 모든 목 데이터를 한 번에 삽입하는 스크립트
- **clear_mock_data.bat** - 기존 목 데이터를 삭제하는 스크립트
- **clear_mock_data.sql** - 데이터 삭제 SQL (직접 실행 가능)

## 🚀 사용 방법

### 방법 1: 배치 파일 실행 (권장)

프로젝트 루트 디렉토리에서:

```batch
insert_mock_data.bat
```

실행하면 다음 옵션이 표시됩니다:
- **옵션 1**: 기존 데이터 삭제 후 삽입 (권장) - 중복 오류 방지
- **옵션 2**: 기존 데이터 유지하고 삽입

이 스크립트는 자동으로:
1. 기존 데이터 삭제 (옵션 1 선택 시)
2. 로컬 PostgreSQL에 연결
3. 순서대로 모든 SQL 파일 실행
4. 각 단계별 성공/실패 메시지 표시

### 방법 1-1: 기존 데이터만 삭제

데이터를 삽입하지 않고 기존 목 데이터만 삭제하려면:

```batch
clear_mock_data.bat
```

이 스크립트는:
- 게시글, 댓글, 좋아요, 북마크, 팔로우, 해시태그 등 모든 목 데이터 삭제
- admin 계정은 유지
- user001~user050 사용자는 삭제

### 방법 2: 수동으로 SQL 파일 실행

각 SQL 파일을 순서대로 실행:

```bash
# 1. 사용자
psql -h localhost -p 5432 -U postgres -d boarddb -f mock-data/01_users.sql

# 2. 게시글
psql -h localhost -p 5432 -U postgres -d boarddb -f mock-data/02_boards.sql

# 3. 팔로우
psql -h localhost -p 5432 -U postgres -d boarddb -f mock-data/03_follows.sql

# 4. 좋아요
psql -h localhost -p 5432 -U postgres -d boarddb -f mock-data/04_likes.sql

# 5. 북마크
psql -h localhost -p 5432 -U postgres -d boarddb -f mock-data/05_bookmarks.sql

# 6. 댓글
psql -h localhost -p 5432 -U postgres -d boarddb -f mock-data/06_comments.sql

# 7. 해시태그
psql -h localhost -p 5432 -U postgres -d boarddb -f mock-data/07_hashtags.sql
```

### 방법 3: pgAdmin 사용

1. pgAdmin 4 실행
2. boarddb 데이터베이스 선택
3. Query Tool 열기
4. 각 SQL 파일 내용을 복사하여 순서대로 실행

## 📊 생성되는 데이터

### 사용자 (01_users.sql)
- **관리자**: `admin` / `1234`
- **일반 사용자**: `user001` ~ `user050` / `1234`
- 다양한 닉네임: 코딩마스터, JavaLover, SpringMaster, ReactNinja, DB전문가 등
- 생성일자: 최근 365일에 걸쳐 분산

### 게시글 (02_boards.sql)
- **총 300+개** 게시글
- **임시저장**: 5개
- **카테고리**: Spring/Java, React/Frontend, Database, DevOps, Algorithms, Career 등
- **조회수**: 145 ~ 723
- **좋아요**: 23 ~ 118
- **해시태그**: #Spring, #Java, #React, #Database 등 포함
- **작성기간**: 최근 30일에 걸쳐 분산

### 팔로우 (03_follows.sql)
- **총 200개** 팔로우 관계
- 인기 사용자 (user001, user003, user004 등)는 많은 팔로워 보유
- 상호 팔로우 관계 포함
- 최근 가입자들의 팔로우 활동 포함

### 좋아요 (04_likes.sql)
- **총 300+개** 게시글 좋아요
- 인기 게시글일수록 많은 좋아요
- 최근 게시글에 더 많은 좋아요 경향
- board_like 테이블에 저장

### 북마크 (05_bookmarks.sql)
- **총 100개** 북마크
- 사용자별 관심 분야에 따라 다양하게 분산
- 유용한 기술 글 위주로 북마크

### 댓글 (06_comments.sql)
- **총 60개** 댓글
- 답글(parent_id 있음) 포함
- 인기 게시글에 더 많은 댓글
- 실제적인 한국어 댓글 내용

### 해시태그 (07_hashtags.sql)
- **총 78개** 해시태그
- **90+개** 게시글-해시태그 연결
- 기술 스택별 분류: Spring, Java, React, Database, DevOps 등
- board_hashtag 테이블로 다대다 관계 구현

## ⚠️ 주의사항

### 데이터 중복 방지

처음 실행하는 경우에는 문제 없지만, 두 번째 실행 시 중복 데이터 오류가 발생할 수 있습니다.

**[방법 1] 기존 데이터만 삭제 (권장, 빠름)**

```batch
clear_mock_data.bat
```

또는 `insert_mock_data.bat` 실행 시 **옵션 1** 선택

**[방법 2] DB 완전 초기화 (모든 테이블 재생성)**

1. 애플리케이션 종료
2. `application.properties` 수정:
   ```properties
   spring.jpa.hibernate.ddl-auto=create
   ```
3. 애플리케이션 재시작 (테이블 재생성됨)
4. 애플리케이션 종료
5. `application.properties` 복원:
   ```properties
   spring.jpa.hibernate.ddl-auto=update
   ```
6. `insert_mock_data.bat` 실행

### PostgreSQL 실행 확인

목 데이터 삽입 전에 PostgreSQL이 실행 중인지 확인:

```bash
# Windows 서비스 확인
sc query postgresql-x64-16

# 또는 psql 접속 테스트
psql -h localhost -p 5432 -U postgres -d boarddb -c "SELECT version();"
```

## 🧪 테스트 방법

1. 목 데이터 삽입 완료 후 애플리케이션 실행:
   ```bash
   mvnw.cmd spring-boot:run
   ```

2. 브라우저에서 테스트:
   - http://localhost:8080
   - 로그인: `user001` / `1234`
   - 게시글 목록, 좋아요, 북마크, 댓글 등 확인

3. 다양한 사용자로 테스트:
   - `admin` / `1234` (관리자)
   - `user001` ~ `user050` / `1234` (일반 사용자)

## 📈 데이터 통계

```sql
-- 사용자 수 확인
SELECT COUNT(*) FROM users;  -- 51명 (admin 포함)

-- 게시글 수 확인
SELECT COUNT(*) FROM board;  -- 300+개

-- 임시저장 게시글 확인
SELECT COUNT(*) FROM board WHERE is_draft = true;  -- 5개

-- 팔로우 관계 확인
SELECT COUNT(*) FROM user_follow;  -- 200개

-- 좋아요 확인
SELECT COUNT(*) FROM board_like;  -- 300+개

-- 북마크 확인
SELECT COUNT(*) FROM bookmark;  -- 100개

-- 댓글 확인
SELECT COUNT(*) FROM comment;  -- 60개

-- 해시태그 확인
SELECT COUNT(*) FROM hashtag;  -- 78개
SELECT COUNT(*) FROM board_hashtag;  -- 90+개
```

## 🎯 활용 사례

### 1. 개발 환경 테스트
- 다양한 사용자와 게시글로 UI/UX 테스트
- 페이지네이션, 검색, 필터링 기능 테스트

### 2. 성능 테스트
- 300+개 게시글로 쿼리 성능 측정
- N+1 문제 검증
- 인덱스 효과 확인

### 3. 기능 테스트
- 팔로우/언팔로우 기능
- 좋아요/좋아요 취소
- 북마크 추가/삭제
- 댓글 작성/수정/삭제
- 해시태그 검색

### 4. 프레젠테이션
- 실제 데이터처럼 보이는 목 데이터로 데모 진행
- 한국어 콘텐츠로 현실적인 시연

## 🔧 문제 해결

### "ERROR: duplicate key value violates unique constraint"
- **원인**: 이미 데이터가 존재함 (중복 키 오류)
- **해결 방법 1** (권장): `clear_mock_data.bat` 실행 후 다시 시도
- **해결 방법 2**: `insert_mock_data.bat` 실행 시 **옵션 1** 선택
- **해결 방법 3**: DB 완전 초기화 (위 "데이터 중복 방지" 섹션 참고)

### "psql: command not found"
- PostgreSQL이 설치되지 않았거나 PATH에 없음
- 해결: `C:\Program Files\PostgreSQL\16\bin\` 경로 확인

### "password authentication failed"
- 비밀번호가 다름
- 해결: `insert_mock_data.bat`의 `LOCAL_PASSWORD` 수정

### "database 'boarddb' does not exist"
- 데이터베이스가 생성되지 않음
- 해결: 애플리케이션을 한 번 실행하여 DB 자동 생성

## 📝 라이선스

이 목 데이터는 테스트 및 개발 용도로만 사용하세요.
실제 운영 환경에는 사용하지 마세요.
