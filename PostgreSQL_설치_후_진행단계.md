# PostgreSQL 설치 후 마이그레이션 진행 단계

## ✅ PostgreSQL 설치 완료 후 실행할 명령어

### 1단계: PostgreSQL 서비스 시작 확인

```bash
# Windows 서비스에서 PostgreSQL이 실행 중인지 확인
sc query postgresql-x64-16

# 또는 PostgreSQL에 직접 접속 시도
psql -U postgres
# 비밀번호 입력 후 접속되면 OK
# \q 로 종료
```

### 2단계: 데이터베이스 생성

```bash
# postgres_setup.bat 실행
postgres_setup.bat
```

이 스크립트는:
- 기존 `boarddb` 데이터베이스 삭제 (있다면)
- 새로운 `boarddb` 데이터베이스 생성
- UTF-8 인코딩 설정

### 3단계: Spring Boot로 테이블 스키마 생성

```bash
# 서버시작.bat 실행
서버시작.bat
```

**중요:**
- JPA가 자동으로 PostgreSQL에 테이블을 생성합니다
- 콘솔에서 `CREATE TABLE` SQL이 실행되는 것을 확인하세요
- 에러 없이 서버가 시작되면 `Ctrl+C`로 종료하세요

**확인사항:**
```
Hibernate: create table users (...) -- 이런 식의 로그가 보여야 함
Hibernate: create table boards (...)
...
```

### 4단계: MySQL 데이터 Export (선택사항)

기존 MySQL 데이터를 마이그레이션하려면:

```bash
# MySQL 데이터 export
mysql_export.bat
```

이 단계는 **MySQL에 기존 데이터가 있을 때만** 필요합니다.

### 5단계: PostgreSQL로 데이터 Import (선택사항)

```bash
# 데이터 import
postgres_import.bat
```

**주의:**
- Python 3가 설치되어 있어야 합니다
- 3단계(테이블 생성)를 먼저 완료해야 합니다

### 6단계: 서버 최종 시작

```bash
# 서버 시작
서버시작.bat
```

브라우저에서 확인:
- http://localhost:8080

---

## 🔍 문제 해결

### PostgreSQL 연결 실패

**증상:**
```
org.postgresql.util.PSQLException: Connection refused
```

**해결:**
1. PostgreSQL 서비스가 실행 중인지 확인
   ```bash
   sc query postgresql-x64-16
   ```

2. 서비스가 중지되었다면 시작:
   ```bash
   sc start postgresql-x64-16
   ```

### 비밀번호 오류

**증상:**
```
password authentication failed for user "postgres"
```

**해결:**
1. `application.properties` 파일 확인:
   ```properties
   spring.datasource.password=YOUR_ACTUAL_PASSWORD
   ```

2. `postgres_setup.bat` 파일에서 비밀번호 수정:
   ```batch
   set PGPASSWORD=YOUR_ACTUAL_PASSWORD
   ```

### 데이터베이스가 이미 존재

**증상:**
```
database "boarddb" already exists
```

**해결:**
수동으로 삭제 후 재생성:
```sql
psql -U postgres
DROP DATABASE boarddb;
CREATE DATABASE boarddb WITH ENCODING='UTF8';
\q
```

---

## 📋 현재 설정 확인

### application.properties
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/boarddb
spring.datasource.username=postgres
spring.datasource.password=postgres  ← 실제 비밀번호로 변경
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### 비밀번호 변경이 필요한 경우

1. `application.properties` 수정
2. `postgres_setup.bat` 수정
3. `postgres_import.bat` 수정

---

## 🎯 체크리스트

- [ ] PostgreSQL 16 설치 완료
- [ ] PostgreSQL 서비스 실행 중
- [ ] `psql -U postgres` 접속 가능
- [ ] `application.properties` 비밀번호 확인
- [ ] `postgres_setup.bat` 실행 완료
- [ ] Spring Boot로 테이블 생성 완료
- [ ] (선택) MySQL 데이터 export 완료
- [ ] (선택) PostgreSQL로 데이터 import 완료
- [ ] 서버 정상 시작 확인
- [ ] http://localhost:8080 접속 확인

---

## 💡 팁

### pgAdmin 4 사용

PostgreSQL과 함께 설치된 pgAdmin 4를 사용하면 GUI로 데이터베이스를 관리할 수 있습니다:

1. pgAdmin 4 실행
2. Servers → PostgreSQL 16 → 우클릭 → Connect
3. 비밀번호 입력
4. Databases → boarddb에서 테이블 확인

### SQL 직접 실행

```bash
# PostgreSQL 접속
psql -U postgres -d boarddb

# 테이블 목록 확인
\dt

# 사용자 목록 확인
SELECT * FROM users;

# 종료
\q
```

---

**작성일:** 2024-12-24
