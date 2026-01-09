# DB 데이터 문제 진단 및 해결

## 🔍 1단계: 현재 상태 확인

애플리케이션을 실행하세요:
```batch
mvnw.cmd spring-boot:run
```

브라우저에서 다음 URL들을 차례로 방문:

### 1. DB 연결 정보 확인
```
http://localhost:8080/debug/db-info
```

**확인 사항**:
- `current_database`: 어떤 DB에 연결되어 있는지
- `table_counts`: 각 테이블의 데이터 개수
  - `users`: 몇 명?
  - `board`: 몇 개?
- `recent_users`: 최근 사용자 목록
- `recent_boards`: 최근 게시글 목록

### 2. admin 계정 확인
```
http://localhost:8080/debug/check-admin
```

**확인 사항**:
- `admin_exists`: true면 기존 데이터가 남아있음

### 3. 목 데이터 확인
```
http://localhost:8080/debug/check-mock-data
```

**확인 사항**:
- `user001_exists`: true면 목 데이터가 삽입됨
- `mock_user_count`: 50이면 목 데이터 정상
- `mock_data_inserted`: true면 성공

---

## 📊 2단계: 상황 분석

### 시나리오 A: 목 데이터가 전혀 없음
```json
{
  "table_counts": {
    "users": 1,
    "board": 0
  },
  "mock_data_inserted": false
}
```

**원인**: 목 데이터 삽입 실패 또는 다른 DB에 삽입됨

**해결**: 아래 3단계의 "해결책 A" 실행

---

### 시나리오 B: 기존 데이터만 있음
```json
{
  "admin_exists": true,
  "mock_data_inserted": false
}
```

**원인**: 기존 데이터가 남아있고 목 데이터는 삽입 안 됨

**해결**: 아래 3단계의 "해결책 B" 실행

---

### 시나리오 C: 목 데이터는 있는데 게시판에 안 보임
```json
{
  "mock_data_inserted": true,
  "table_counts": {
    "users": 51,
    "board": 0
  }
}
```

**원인**: 사용자는 삽입되었지만 게시글은 실패

**해결**: 아래 3단계의 "해결책 C" 실행

---

## 🔧 3단계: 해결책 실행

### 해결책 A: 완전 초기화 후 재삽입

```batch
reset_db_and_insert.bat
```

이 스크립트는:
1. DB 테이블 완전 삭제 및 재생성
2. 목 데이터 자동 삽입
3. 약 1분 소요

실행 후:
- admin/admin1234 → 사라짐
- user001/1234 ~ user050/1234 → 생성됨
- 게시글 300+개 → 생성됨

---

### 해결책 B: 기존 데이터 삭제 후 삽입

```batch
clear_mock_data.bat
```

그 다음:

```batch
insert_mock_data.bat
```

옵션 2 선택 (기존 데이터 유지)

---

### 해결책 C: 게시글만 다시 삽입

직접 SQL 실행:

```batch
set PGPASSWORD=1
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -f "mock-data/02_boards.sql"
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -f "mock-data/03_follows.sql"
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -f "mock-data/04_likes.sql"
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -f "mock-data/05_bookmarks.sql"
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -f "mock-data/06_comments.sql"
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -f "mock-data/07_hashtags.sql"
```

---

## ✅ 4단계: 검증

해결책 실행 후 다시 확인:

```
http://localhost:8080/debug/check-mock-data
```

**성공 조건**:
```json
{
  "mock_data_inserted": true,
  "mock_user_count": 50
}
```

그리고:
```
http://localhost:8080/board
```

- 게시글 300+개가 보여야 함
- 로그인: user001 / 1234

---

## 🎯 권장 순서

1. ✅ 애플리케이션 실행
2. ✅ http://localhost:8080/debug/db-info 확인
3. ✅ 상황 파악
4. ✅ 해결책 A (reset_db_and_insert.bat) 실행 ← **가장 확실**
5. ✅ 애플리케이션 재시작
6. ✅ http://localhost:8080/board 확인
7. ✅ user001 / 1234 로그인

---

## 💡 팁

- DebugController는 임시 디버깅용입니다
- 문제 해결 후 삭제해도 됩니다
- 또는 보안을 위해 접근 제한을 추가할 수 있습니다
