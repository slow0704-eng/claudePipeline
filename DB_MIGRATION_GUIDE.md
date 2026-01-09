# 운영 DB → 로컬 DB 데이터 이관 가이드

## 📋 사전 준비

### 1. Render 대시보드에서 DB 연결 정보 확인

1. [Render 대시보드](https://dashboard.render.com) 접속
2. `board-postgres` 데이터베이스 선택
3. "Connect" 섹션에서 다음 정보 확인:
   - **External Database URL** 또는 개별 연결 정보
   - Host (예: `dpg-xxxx-a.oregon-postgres.render.com`)
   - Port (기본값: `5432`)
   - Database (예: `boarddb`)
   - Username (예: `boarduser`)
   - Password

### 2. 로컬 PostgreSQL 확인

- PostgreSQL 16 설치 확인: `C:\Program Files\PostgreSQL\16\`
- 로컬 PostgreSQL 서비스 실행 중인지 확인

## 🚀 이관 절차

### Step 1: 운영 DB 덤프

```bash
1_dump_from_render.bat
```

실행 후 프롬프트에 Render DB 연결 정보 입력:
- Host: Render에서 복사한 호스트 주소
- Port: 5432 (기본값)
- Database: boarddb
- User: boarduser
- Password: Render에서 확인한 비밀번호

✅ 성공 시 `render_backup.dump` 파일 생성됨

### Step 2: 로컬 DB 복원

```bash
2_restore_to_local.bat
```

⚠️ **주의**: 기존 로컬 DB 데이터가 모두 삭제되고 운영 데이터로 대체됩니다!

✅ 성공 시 로컬 `boarddb`에 운영 데이터 복원 완료

### Step 3: 애플리케이션 확인

```bash
mvnw.cmd spring-boot:run
```

브라우저에서 `http://localhost:8080` 접속하여 데이터 확인

## 📁 파일 설명

- **1_dump_from_render.bat**: 운영 DB에서 데이터 덤프
- **2_restore_to_local.bat**: 로컬 DB로 데이터 복원
- **render_backup.dump**: 덤프된 데이터 파일 (생성됨)

## 🔧 현재 DB 설정

### 로컬 환경 (application.properties)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/boarddb
spring.datasource.username=postgres
spring.datasource.password=1
```

### 운영 환경 (application-prod.properties)
- Render 환경변수에서 자동 설정
- `SPRING_PROFILES_ACTIVE=prod`

## ⚠️ 문제 해결

### "pg_dump: command not found" 오류
- PostgreSQL 16이 `C:\Program Files\PostgreSQL\16\`에 설치되어 있는지 확인
- 다른 경로에 설치된 경우 스크립트의 경로 수정

### "connection refused" 오류
- Render DB 연결 정보 재확인
- 방화벽이 PostgreSQL 포트(5432)를 차단하지 않는지 확인
- Render DB가 일시 중지 상태가 아닌지 확인 (Free plan은 비활동 시 중지됨)

### "authentication failed" 오류
- Render DB 비밀번호 재확인
- Render 대시보드에서 최신 비밀번호 복사

### "database already exists" 오류
- `2_restore_to_local.bat`가 자동으로 기존 DB를 삭제하고 재생성합니다
- 수동으로 삭제하려면:
  ```sql
  psql -U postgres -h localhost
  DROP DATABASE boarddb;
  CREATE DATABASE boarddb;
  ```

## 💡 참고사항

### 덤프 파일 형식
- Custom format (`-F c`): 효율적이고 선택적 복원 가능
- 압축되어 용량 절약
- 병렬 복원 지원

### 옵션 설명
- `--no-owner`: 소유자 정보 제외 (로컬 DB 사용자로 생성)
- `--no-privileges`: 권한 정보 제외
- `--clean`: 복원 전 기존 객체 삭제
- `--if-exists`: 존재하지 않는 객체 삭제 시도 시 오류 무시

### 데이터베이스 크기 확인
```sql
-- 로컬 DB 접속
psql -U postgres -h localhost -d boarddb

-- 테이블별 크기 확인
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- 전체 DB 크기
SELECT pg_size_pretty(pg_database_size('boarddb'));
```

## 🔄 정기 동기화

운영 DB 데이터를 주기적으로 로컬에 동기화하려면:

1. `1_dump_from_render.bat` 실행
2. `2_restore_to_local.bat` 실행
3. 백업 파일 보관 (선택사항)

## 📝 백업 관리

### 백업 파일 이름 변경 (선택사항)
```bash
# 날짜별 백업 보관
ren render_backup.dump render_backup_20250629.dump
```

### 여러 백업 관리
`1_dump_from_render.bat`를 수정하여 날짜별 파일명 생성:
```batch
set BACKUP_FILE=render_backup_%date:~0,4%%date:~5,2%%date:~8,2%.dump
```

## ✅ 완료 체크리스트

- [ ] Render DB 연결 정보 확인
- [ ] 로컬 PostgreSQL 실행 중
- [ ] `1_dump_from_render.bat` 실행 완료
- [ ] `render_backup.dump` 파일 생성됨
- [ ] `2_restore_to_local.bat` 실행 완료
- [ ] 애플리케이션 정상 실행 확인
- [ ] 데이터 확인 완료
