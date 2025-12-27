# Render 배포 가이드

이 가이드는 Spring Boot 게시판 애플리케이션을 Render에 배포하는 방법을 설명합니다.

## 사전 준비

1. **GitHub 계정** - 이미 준비됨 ✅
2. **Render 계정** - https://render.com 에서 무료 가입
3. **코드 푸시** - 이미 완료됨 ✅

## 배포 단계

### 1단계: Render 계정 생성

1. https://render.com 접속
2. "Get Started for Free" 클릭
3. GitHub 계정으로 로그인

### 2단계: GitHub 저장소 연결

1. Render 대시보드에서 "New +" 버튼 클릭
2. "Blueprint" 선택
3. GitHub 저장소 연결 허용
4. `slow0704-eng/claudePipeline` 저장소 선택

### 3단계: Blueprint 배포

1. Render가 자동으로 `render.yaml` 파일을 감지합니다
2. 다음 서비스들이 자동으로 생성됩니다:
   - **PostgreSQL 데이터베이스** (board-postgres)
   - **웹 서비스** (spring-board-app)

3. "Apply" 버튼 클릭

### 4단계: 배포 진행

Render가 자동으로 다음 작업을 수행합니다:

1. ✅ PostgreSQL 데이터베이스 생성
2. ✅ 소스 코드 클론
3. ✅ Maven으로 빌드 (`./mvnw clean package -DskipTests`)
4. ✅ Docker 이미지 생성
5. ✅ 애플리케이션 시작
6. ✅ 데이터베이스 연결 설정

**예상 배포 시간:** 약 5-10분

### 5단계: 배포 완료 확인

1. 웹 서비스 상태가 "Live"로 표시될 때까지 대기
2. Render가 제공하는 URL 확인 (예: `https://spring-board-app.onrender.com`)
3. URL 클릭하여 애플리케이션 접속

## 배포 후 설정

### 초기 관리자 계정 생성

애플리케이션에 처음 접속한 후:

1. 회원가입 페이지로 이동
2. 관리자 계정 생성
3. 데이터베이스에서 수동으로 권한 변경 필요 (또는 애플리케이션에서 첫 사용자를 자동으로 ADMIN으로 설정)

### 환경 변수 (선택사항)

Render 대시보드에서 추가 환경 변수 설정 가능:

```
JAVA_TOOL_OPTIONS=-Xmx512m -Xms256m
SPRING_PROFILES_ACTIVE=prod
```

## 주요 기능

✅ 자동 HTTPS 인증서 (Let's Encrypt)
✅ 자동 배포 (GitHub push 시)
✅ PostgreSQL 데이터베이스
✅ 무료 플랜 사용 가능
✅ 로그 모니터링
✅ 헬스 체크 자동 실행

## 무료 플랜 제한사항

- **웹 서비스:**
  - 메모리: 512MB
  - 15분 미사용 시 자동 sleep
  - Sleep 후 첫 요청 시 Cold Start (약 30초)
  - 750시간/월 무료

- **PostgreSQL:**
  - 저장 공간: 1GB
  - 연결: 최대 97개
  - 자동 백업 없음 (수동 백업 필요)
  - 90일 후 자동 삭제 (무료 플랜)

## 업그레이드 옵션

더 나은 성능이 필요한 경우:

- **Starter Plan ($7/월):**
  - Sleep 없음
  - 더 많은 메모리
  - 우선 지원

- **Standard Plan ($25/월):**
  - 오토스케일링
  - 더 많은 리소스
  - 자동 백업

## 배포 URL

배포가 완료되면 다음과 같은 URL을 받게 됩니다:

```
https://spring-board-app.onrender.com
```

## 로그 확인

배포 상태 및 로그 확인:

1. Render 대시보드
2. "spring-board-app" 서비스 클릭
3. "Logs" 탭에서 실시간 로그 확인

## 재배포

코드 변경 후 재배포:

```bash
git add .
git commit -m "Update feature"
git push origin master
```

Render가 자동으로 감지하고 재배포합니다.

## 트러블슈팅

### 빌드 실패

- Maven 빌드 로그 확인
- Java 버전 확인 (Java 17 필요)

### 데이터베이스 연결 오류

- DATABASE_URL 환경 변수 확인
- PostgreSQL 서비스 상태 확인

### 애플리케이션 시작 실패

- 로그에서 에러 메시지 확인
- PORT 환경 변수 확인

## 모니터링

- **상태 확인:** Render 대시보드
- **로그 모니터링:** Logs 탭
- **메트릭:** Metrics 탭 (CPU, 메모리, 네트워크)

## 백업

PostgreSQL 무료 플랜은 자동 백업이 없으므로:

1. 정기적으로 데이터베이스 덤프 생성
2. 로컬 또는 다른 스토리지에 백업 저장

```bash
# Render Shell에서 실행
pg_dump $DATABASE_URL > backup.sql
```

## 다음 단계

1. ✅ 배포 완료
2. 🔧 도메인 연결 (선택사항)
3. 🔧 CDN 설정 (선택사항)
4. 🔧 모니터링 도구 추가 (선택사항)

## 지원

- Render 문서: https://render.com/docs
- 커뮤니티: https://community.render.com
- 이슈 리포트: GitHub Issues

---

**배포가 완료되면 애플리케이션이 인터넷에서 접근 가능합니다!** 🎉
