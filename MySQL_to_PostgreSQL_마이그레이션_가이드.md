# MySQL에서 PostgreSQL로 마이그레이션 가이드

이 문서는 Spring Boot 게시판 애플리케이션을 MySQL에서 PostgreSQL로 마이그레이션하는 절차를 설명합니다.

## 📋 목차

1. [사전 준비](#사전-준비)
2. [마이그레이션 절차](#마이그레이션-절차)
3. [문제 해결](#문제-해결)
4. [수동 마이그레이션](#수동-마이그레이션)

---

## 🔧 사전 준비

### 1. PostgreSQL 설치

PostgreSQL이 설치되어 있지 않다면 다음 방법 중 하나로 설치하세요:

#### 방법 1: 공식 설치 프로그램 사용
1. [PostgreSQL 공식 사이트](https://www.postgresql.org/download/windows/)에서 설치 프로그램 다운로드
2. 설치 시 다음 정보 기록:
   - Username: `postgres` (기본값)
   - Password: 설치 시 설정한 비밀번호
   - Port: `5432` (기본값)

#### 방법 2: Docker 사용
```bash
docker run --name postgres-boarddb -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16
```

### 2. Python 3 설치 확인

데이터 변환을 위해 Python 3가 필요합니다:

```bash
python --version
```

Python이 설치되어 있지 않다면 [Python 공식 사이트](https://www.python.org/downloads/)에서 다운로드하세요.

### 3. PostgreSQL 서비스 확인

PostgreSQL이 실행 중인지 확인:

```bash
# Windows 서비스 확인
sc query postgresql-x64-16

# 또는 PostgreSQL 클라이언트로 접속 테스트
psql -U postgres -h localhost
```

---

## 🚀 마이그레이션 절차

### 단계별 실행

#### 1단계: MySQL 데이터 Export

```bash
mysql_export.bat
```

이 스크립트는:
- MySQL의 `boarddb` 데이터베이스를 export합니다
- `boarddb_export.sql` 파일을 생성합니다
- PostgreSQL 호환 모드로 export합니다

**주의사항:**
- MySQL 서버가 실행 중이어야 합니다
- `application.properties`의 MySQL 설정이 올바른지 확인하세요

#### 2단계: PostgreSQL 데이터베이스 생성

```bash
postgres_setup.bat
```

이 스크립트는:
- 기존 `boarddb` 데이터베이스를 삭제합니다 (있는 경우)
- 새로운 `boarddb` 데이터베이스를 생성합니다
- UTF-8 인코딩을 설정합니다

**설정 확인:**
- 스크립트 내의 사용자명/비밀번호가 맞는지 확인
- 기본값: `postgres` / `postgres`

#### 3단계: Spring Boot 애플리케이션 첫 실행

이 단계가 **매우 중요**합니다!

```bash
서버시작.bat
```

JPA가 PostgreSQL에 테이블 스키마를 자동으로 생성하도록 합니다.

**확인사항:**
- 애플리케이션이 오류 없이 시작되는지 확인
- 콘솔에서 CREATE TABLE 문이 실행되는지 확인
- `Ctrl+C`로 애플리케이션을 종료합니다

#### 4단계: 데이터 Import

```bash
postgres_import.bat
```

이 스크립트는:
1. Python 스크립트로 MySQL dump를 PostgreSQL 형식으로 변환
2. 변환된 SQL을 PostgreSQL로 import

**주의사항:**
- 3단계(테이블 생성)를 반드시 먼저 실행해야 합니다
- 기존 데이터가 있다면 덮어씌워집니다

#### 5단계: 애플리케이션 시작 및 확인

```bash
서버시작.bat
```

브라우저에서 확인:
- http://localhost:8080
- 기존 데이터가 잘 보이는지 확인
- 게시글, 사용자, 댓글 등이 정상적으로 표시되는지 확인

---

## 🔍 문제 해결

### PostgreSQL 연결 실패

**증상:** `Connection refused` 또는 `authentication failed`

**해결방법:**
1. PostgreSQL 서비스가 실행 중인지 확인
   ```bash
   sc query postgresql-x64-16
   ```

2. `application.properties` 설정 확인
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/boarddb
   spring.datasource.username=postgres
   spring.datasource.password=YOUR_PASSWORD
   ```

3. PostgreSQL 접속 테스트
   ```bash
   psql -U postgres -h localhost
   ```

### 데이터 타입 오류

**증상:** `column "xxx" is of type xxx but expression is of type xxx`

**해결방법:**
1. `boarddb_postgresql.sql` 파일을 텍스트 에디터로 열기
2. 문제가 되는 컬럼의 타입을 수동으로 수정
3. 다시 import

**일반적인 변환:**
- `tinyint(1)` → `boolean`
- `datetime` → `timestamp`
- `longtext` → `text`
- `int(11)` → `integer`

### AUTO_INCREMENT 관련 오류

**증상:** `nextval` 또는 시퀀스 관련 오류

**해결방법:**

PostgreSQL에서 시퀀스를 재설정:

```sql
-- 각 테이블의 시퀀스를 현재 최대값으로 재설정
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('boards_id_seq', (SELECT MAX(id) FROM boards));
SELECT setval('comments_id_seq', (SELECT MAX(id) FROM comments));
-- 기타 테이블도 동일하게...
```

### 한글 인코딩 문제

**증상:** 한글이 깨져서 보임

**해결방법:**

1. 데이터베이스 인코딩 확인:
   ```sql
   \l boarddb
   ```
   결과에서 `Encoding`이 `UTF8`인지 확인

2. 데이터베이스 재생성 (인코딩 지정):
   ```sql
   DROP DATABASE boarddb;
   CREATE DATABASE boarddb
     WITH ENCODING='UTF8'
     LC_COLLATE='ko_KR.UTF-8'
     LC_CTYPE='ko_KR.UTF-8'
     TEMPLATE=template0;
   ```

---

## 🔧 수동 마이그레이션

자동 스크립트가 작동하지 않는 경우 수동으로 마이그레이션할 수 있습니다.

### 1. MySQL 데이터 Export

```bash
mysqldump -u root --single-transaction --skip-triggers --no-create-db boarddb > boarddb_export.sql
```

### 2. PostgreSQL 데이터베이스 생성

```bash
psql -U postgres
```

```sql
DROP DATABASE IF EXISTS boarddb;
CREATE DATABASE boarddb WITH ENCODING='UTF8';
\c boarddb
```

### 3. Spring Boot로 테이블 생성

`application.properties` 설정:
```properties
spring.jpa.hibernate.ddl-auto=create
```

애플리케이션 실행 후 종료, 그리고 다시 `update`로 변경:
```properties
spring.jpa.hibernate.ddl-auto=update
```

### 4. 데이터를 수동으로 복사

각 테이블의 데이터를 CSV로 export하고 PostgreSQL의 COPY 명령으로 import:

```sql
-- PostgreSQL에서
COPY users FROM '/path/to/users.csv' DELIMITER ',' CSV HEADER;
COPY boards FROM '/path/to/boards.csv' DELIMITER ',' CSV HEADER;
-- ...
```

---

## ✅ 마이그레이션 체크리스트

- [ ] PostgreSQL 설치 및 실행 확인
- [ ] Python 3 설치 확인
- [ ] MySQL 데이터 export 완료
- [ ] PostgreSQL 데이터베이스 생성 완료
- [ ] `application.properties` PostgreSQL로 변경
- [ ] Spring Boot로 테이블 스키마 생성
- [ ] 데이터 import 완료
- [ ] 애플리케이션 정상 동작 확인
- [ ] 기존 데이터 검증
- [ ] 성능 테스트

---

## 📝 주요 변경사항

### application.properties

**변경 전 (MySQL):**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/boarddb
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

**변경 후 (PostgreSQL):**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/boarddb
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### pom.xml

PostgreSQL 드라이버가 이미 포함되어 있습니다:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## 🎯 마이그레이션 후 확인사항

1. **데이터 무결성**
   - 모든 게시글이 정상적으로 표시되는가?
   - 사용자 정보가 올바른가?
   - 댓글과 좋아요가 정상 작동하는가?
   - 첨부파일이 제대로 연결되어 있는가?

2. **기능 테스트**
   - 로그인/로그아웃
   - 게시글 작성/수정/삭제
   - 댓글 작성
   - 파일 업로드
   - 검색 기능

3. **성능 테스트**
   - 페이지 로딩 속도
   - 검색 속도
   - 대용량 데이터 처리

---

## 💡 팁

1. **백업은 필수**
   - 마이그레이션 전 MySQL 데이터를 반드시 백업하세요
   - `mysqldump`로 전체 데이터를 백업해두세요

2. **단계별 진행**
   - 한 번에 모든 것을 하지 말고 단계별로 진행하세요
   - 각 단계에서 오류가 없는지 확인하세요

3. **테스트 환경에서 먼저**
   - 가능하면 테스트 환경에서 먼저 마이그레이션을 시도하세요
   - 문제가 없는 것을 확인한 후 프로덕션에 적용하세요

---

## 📞 추가 도움이 필요한 경우

- PostgreSQL 공식 문서: https://www.postgresql.org/docs/
- Spring Data JPA 문서: https://spring.io/projects/spring-data-jpa
- Hibernate 문서: https://hibernate.org/orm/documentation/

---

**작성일:** 2024-12-24
**버전:** 1.0
