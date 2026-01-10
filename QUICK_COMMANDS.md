# 🚀 빠른 명령어 가이드

개발 중 자주 사용하는 명령어들을 한눈에 볼 수 있도록 정리한 문서입니다.

---

## 📋 목차

1. [서버 실행/중지](#1-서버-실행중지)
2. [데이터베이스 작업](#2-데이터베이스-작업)
3. [빌드 및 테스트](#3-빌드-및-테스트)
4. [Git 작업](#4-git-작업)
5. [배포 관련](#5-배포-관련)
6. [디버깅](#6-디버깅)

---

## 1. 서버 실행/중지

### 🟢 로컬 서버 시작
```bash
./mvnw.cmd spring-boot:run
```

### 🟢 로컬 서버 시작 (특정 프로파일)
```bash
# 개발 환경
./mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev

# 운영 환경
./mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod
```

### 🔴 서버 중지
```
Ctrl + C
```

### 🟢 백그라운드 실행 (권장하지 않음)
```bash
start /B ./mvnw.cmd spring-boot:run
```

---

## 2. 데이터베이스 작업

### 📊 목 데이터 전체 삽입
```bash
insert_mock_data.bat
```
- **내용**: 사용자 50명 + 게시글 300개 + 팔로우 + 좋아요 + 북마크 + 댓글 + 해시태그
- **비밀번호**: 모든 사용자 `1234`
- **관리자**: `admin` / `1234` (기존 계정 유지)

### 📝 기술 블로그 100개 삽입
```bash
insert_tech_boards.bat
```
- **내용**: 개발 관련 기술 블로그 100개
- **카테고리**: Spring Boot, Java, JavaScript, React, Database, DevOps, Algorithm

### 🧹 목 데이터 삭제
```bash
clear_mock_data.bat
```
- **주의**: admin 계정은 유지됩니다

### ✨ 간단 삽입 (빠른 테스트용)
```bash
SIMPLE_INSERT.bat
```
- **내용**: 최소한의 데이터만 삽입 (빠름)

### 🔍 사용자 데이터 확인
```bash
check_users.bat
```
- 전체 사용자 수, ID 범위, 사용자 목록 확인

### 🔍 게시글 수 확인
```bash
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -c "SELECT COUNT(*) FROM board;"
```
환경변수 설정:
```bash
set PGPASSWORD=1
```

### 🔍 데이터베이스 전체 상태 확인
로컬 서버 실행 후 브라우저에서:
```
http://localhost:8080/debug/db-info
```

---

## 3. 빌드 및 테스트

### 🔨 빌드 (테스트 제외)
```bash
./mvnw.cmd clean compile -DskipTests
```

### 🔨 전체 빌드 (테스트 포함)
```bash
./mvnw.cmd clean install
```

### 🔨 패키징 (JAR 생성)
```bash
./mvnw.cmd clean package -DskipTests
```

### 🧪 테스트 실행
```bash
# 전체 테스트
./mvnw.cmd test

# 특정 테스트 클래스만
./mvnw.cmd test -Dtest=UserServiceTest

# 특정 메서드만
./mvnw.cmd test -Dtest=UserServiceTest#testCreateUser
```

### 🧹 빌드 결과물 삭제
```bash
./mvnw.cmd clean
```

---

## 4. Git 작업

### 📥 최신 코드 받기
```bash
git pull origin main
```

### 📤 변경사항 푸시 (현재 브랜치)
```bash
git add .
git commit -m "메시지"
git push
```

### 🌿 브랜치 확인
```bash
# 현재 브랜치 확인
git branch --show-current

# 모든 브랜치 확인
git branch -a
```

### 🌿 브랜치 변경
```bash
# main 브랜치로 이동
git checkout main

# 개발 브랜치로 이동
git checkout claude/dev-build-fix-SvKJt
```

### 📊 변경사항 확인
```bash
# 상태 확인
git status

# 변경 내용 확인
git diff

# 로그 확인
git log --oneline -10
```

### 🔄 변경사항 임시 저장
```bash
# 저장
git stash

# 복원
git stash pop
```

---

## 5. 배포 관련

### 🚀 Render 자동 배포
현재 설정: `claude/dev-build-fix-SvKJt` 브랜치에 푸시하면 자동 배포

```bash
git push origin claude/dev-build-fix-SvKJt
```

### 🌐 배포된 사이트 확인
Render 대시보드에서 확인:
```
https://dashboard.render.com/
```

### 📋 배포 로그 확인
Render 대시보드 → 해당 서비스 → Logs

---

## 6. 디버깅

### 🔍 애플리케이션 로그 확인
서버 실행 중 콘솔에서 실시간 확인

### 🔍 데이터베이스 연결 확인
```bash
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb
```
비밀번호: `1`

### 🔍 포트 사용 확인 (8080)
```bash
netstat -ano | findstr :8080
```

### 🔍 프로세스 강제 종료
```bash
# 포트 8080 사용 중인 프로세스 찾기
netstat -ano | findstr :8080

# PID로 프로세스 종료
taskkill /PID [PID번호] /F
```

### 🔍 디버그 엔드포인트 (개발 환경만)
로컬 서버 실행 후:
```
# DB 정보 확인
http://localhost:8080/debug/db-info

# admin 계정 확인
http://localhost:8080/debug/check-admin

# 목 데이터 확인
http://localhost:8080/debug/check-mock-data

# 해시태그 재동기화
http://localhost:8080/debug/resync-all-hashtags
```

---

## 🎯 자주 사용하는 작업 시나리오

### 시나리오 1: 새로운 기능 개발 시작
```bash
# 1. 최신 코드 받기
git pull origin main

# 2. 로컬 서버 시작
./mvnw.cmd spring-boot:run

# 3. 브라우저에서 확인
# http://localhost:8080
```

### 시나리오 2: 데이터베이스 초기화 후 테스트
```bash
# 1. 기존 데이터 삭제 및 새 데이터 삽입
insert_mock_data.bat
# (옵션 1 선택 - 기존 데이터 삭제 후 삽입)

# 2. 서버 재시작
# Ctrl + C 후
./mvnw.cmd spring-boot:run

# 3. 데이터 확인
check_users.bat
```

### 시나리오 3: 코드 변경 후 테스트
```bash
# 1. 빌드 확인
./mvnw.cmd clean compile -DskipTests

# 2. 서버 재시작 (자동 재로드 안 될 경우)
# Ctrl + C 후
./mvnw.cmd spring-boot:run

# 3. 브라우저에서 테스트
```

### 시나리오 4: 운영 서버에 배포
```bash
# 1. 변경사항 커밋
git add .
git commit -m "기능 추가: ..."

# 2. 개발 브랜치에 푸시 (자동 배포)
git push origin claude/dev-build-fix-SvKJt

# 3. Render 대시보드에서 배포 상태 확인
```

### 시나리오 5: 빌드 오류 발생 시
```bash
# 1. 클린 빌드
./mvnw.cmd clean

# 2. 다시 빌드
./mvnw.cmd clean compile -DskipTests

# 3. 여전히 안 되면 의존성 재다운로드
./mvnw.cmd clean install -U
```

---

## 🔑 중요 계정 정보

### 로컬 데이터베이스
- **Host**: localhost
- **Port**: 5432
- **Database**: boarddb
- **User**: postgres
- **Password**: 1

### 관리자 계정
- **Username**: admin
- **Password**: admin1234
- **자동 생성**: 애플리케이션 시작 시

### 목 데이터 일반 사용자
- **Username**: user001 ~ user050
- **Password**: 1234 (모두 동일)

---

## 📝 참고 문서

- [배포 가이드](./RENDER_DEPLOYMENT.md)
- [목 데이터 사용법](./mock-data/README.md)
- [DB 마이그레이션 가이드](./DB_MIGRATION_GUIDE.md)
- [문제 해결 가이드](./해결방법.md)

---

## 💡 팁

### 개발 효율 향상
1. **서버 자동 재시작**: Spring Boot DevTools가 활성화되어 있어 코드 변경 시 자동 재시작됩니다
2. **브라우저 자동 새로고침**: LiveReload 브라우저 확장 프로그램 사용 권장
3. **로그 레벨 조정**: `application.yml`에서 `logging.level` 설정

### 자주 하는 실수 방지
1. **서버 중복 실행**: 8080 포트 이미 사용 중 오류 → 기존 프로세스 종료
2. **데이터베이스 연결 실패**: PostgreSQL 서비스 실행 확인
3. **CSRF 토큰 오류**: 템플릿에 CSRF 토큰 포함 확인
4. **Git 충돌**: 작업 전 항상 `git pull` 먼저 실행

---

## 🆘 문제 발생 시

### 1단계: 기본 확인
```bash
# 빌드 확인
./mvnw.cmd clean compile -DskipTests

# 포트 확인
netstat -ano | findstr :8080

# PostgreSQL 서비스 확인
# 서비스 관리자에서 확인
```

### 2단계: 로그 확인
- 콘솔 로그 확인
- Render 대시보드 로그 확인 (운영 서버)

### 3단계: 데이터베이스 확인
```bash
# DB 연결 테스트
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -c "SELECT COUNT(*) FROM users;"
```

### 4단계: 클린 재시작
```bash
# 1. 모든 빌드 결과물 삭제
./mvnw.cmd clean

# 2. 전체 재빌드
./mvnw.cmd clean install -DskipTests

# 3. 서버 재시작
./mvnw.cmd spring-boot:run
```

---

**마지막 업데이트**: 2026-01-11
**작성자**: Claude Code
