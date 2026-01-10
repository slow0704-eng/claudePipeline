# 프로젝트 가이드라인 (Windows 한글 경로 대응)

## 서버 실행 지침
- **절대 원칙**: 백그라운드 실행(`&`)이 한글 경로 이슈로 잦은 실패를 일으킴.
- **실행 명령어**: `start cmd.exe /k "cd /d \"%CD%\" && mvnw.cmd spring-boot:run"`
- **설명**: 새 창을 띄워 가시적으로 서버를 실행하는 것이 가장 확실함.
- **한글 경로**: 반드시 따옴표(`\"`)로 감싸고 `cd /d` 명령어를 사용하여 드라이브 이동까지 포함할 것.

## 프로젝트 정보
- 빌드 도구: Maven (`mvnw.cmd`)
- 주요 스크립트: `서버시작.bat` 존재함
- 데이터베이스: PostgreSQL 16

## 데이터베이스 SQL 실행 지침
- **PostgreSQL 접속 정보**:
  - Host: localhost
  - Port: 5432
  - Database: boarddb
  - User: postgres
  - Password: 1

- **SQL 파일 실행 명령어** (Git Bash 환경):
```bash
PGPASSWORD=1 "/c/Program Files/PostgreSQL/16/bin/psql.exe" -h localhost -p 5432 -U postgres -d boarddb -f "/c/Users/slow0/OneDrive/바탕 화면/251121/251210 게시판/mock-data/파일명.sql"
```

- **주의사항**:
  - Git Bash에서는 `/c/` 형식의 경로 사용
  - `PGPASSWORD` 환경변수로 비밀번호 전달
  - 한글 경로도 따옴표로 감싸면 정상 작동

- **Mock 데이터 파일 위치**: `mock-data/` 폴더
  - `01_users.sql`: 사용자 데이터
  - `02_boards_fixed.sql`: 게시글 데이터
  - `03_follows.sql`: 팔로우 관계
  - `04_likes_v2.sql`: 좋아요 데이터
  - `05_bookmarks.sql`: 북마크 데이터
  - `06_comments_fixed.sql`: 댓글 데이터
  - `07_hashtags.sql`: 해시태그 데이터
  - `08_additional_boards.sql`: 추가 게시글 100개 (로컬용)
  - `09_prod_boards.sql`: 운영서버용 게시글 50개

## 운영서버(Render) DB 접속 지침
- **PostgreSQL 접속 정보**:
  - Host: dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com
  - Port: 5432
  - Database: boarddb_0u9z
  - User: boarduser
  - Password: RgdjzmPYsWj5GgxHs3feHNCDOQqbZ4aV

- **SQL 파일 실행 명령어** (Git Bash 환경):
```bash
PGPASSWORD=RgdjzmPYsWj5GgxHs3feHNCDOQqbZ4aV "/c/Program Files/PostgreSQL/16/bin/psql.exe" -h dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com -p 5432 -U boarduser -d boarddb_0u9z -f "/c/Users/slow0/OneDrive/바탕 화면/251121/251210 게시판/mock-data/파일명.sql"
```

- **운영서버 사용자 확인**:
```bash
PGPASSWORD=RgdjzmPYsWj5GgxHs3feHNCDOQqbZ4aV "/c/Program Files/PostgreSQL/16/bin/psql.exe" -h dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com -p 5432 -U boarduser -d boarddb_0u9z -c "SELECT id, username FROM users ORDER BY id;"
```

- **주의사항**:
  - 운영서버 사용자 ID는 1-11 범위만 존재 (로컬과 다름)
  - 운영용 SQL 작성 시 user_id를 1-11 범위로 제한할 것
  - 운영 사이트: https://spring-board-app.onrender.com
