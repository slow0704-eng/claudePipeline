# Render 배포 가이드

## 준비 완료 사항
- ✅ MySQL 드라이버 설정
- ✅ PostgreSQL 드라이버 추가 (선택사항)
- ✅ Production 프로파일 설정 (application-prod.properties)
- ✅ render.yaml 설정 파일

## Render 배포 방법

### 방법 1: Render 웹 UI (추천)

#### 1단계: Render 계정 생성 및 로그인
1. https://render.com 접속
2. "Get Started" 또는 "Sign Up" 클릭
3. GitHub 계정으로 로그인

#### 2단계: 새 Web Service 생성
1. 대시보드에서 **"New +"** → **"Web Service"** 클릭
2. **"Connect a repository"** → GitHub 저장소 연결
3. `slow0704-eng/claudePipeline` 저장소 선택
4. 다음 설정 입력:
   - **Name**: `spring-board-app` (원하는 이름)
   - **Region**: `Singapore` (가장 가까운 지역)
   - **Branch**: `master`
   - **Root Directory**: (비워둠)
   - **Environment**: `Java`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/*.jar`
   - **Instance Type**: `Free`

#### 3단계: 환경 변수 설정

**중요**: MySQL URL을 설정해야 합니다. 두 가지 옵션이 있습니다:

##### 옵션 A: Railway MySQL 사용 (추천)
1. Railway에서 MySQL만 생성:
   - https://railway.app 접속
   - "New Project" → "Provision MySQL"
   - MySQL 생성 후 **"Connect"** 탭에서 **"MySQL Connection URL"** 복사

2. Render에서 환경 변수 추가:
   - **Key**: `MYSQL_URL`
   - **Value**: Railway에서 복사한 MySQL URL
   - 예시: `mysql://user:password@containers-us-west-xxx.railway.app:1234/railway`

##### 옵션 B: 다른 MySQL 서비스 사용
- **PlanetScale**: https://planetscale.com (무료 플랜 5GB)
- **Clever Cloud**: https://www.clever-cloud.com (무료 MySQL 256MB)
- **Aiven**: https://aiven.io (무료 플랜 제한적)

환경 변수:
```
SPRING_PROFILES_ACTIVE = prod
MYSQL_URL = mysql://username:password@host:port/database
```

#### 4단계: 배포
1. "Create Web Service" 클릭
2. 빌드가 자동으로 시작됨 (첫 배포 10-15분 소요)
3. "Logs" 탭에서 빌드 진행 상황 확인

#### 5단계: 배포 확인
1. 빌드 완료 후 Render가 제공하는 URL 확인
   - 예: `https://spring-board-app.onrender.com`
2. URL로 접속하여 애플리케이션 동작 확인

---

### 방법 2: render.yaml로 자동 배포

1. GitHub에 `render.yaml` 파일이 있으면 Render가 자동 인식
2. Render 대시보드에서 "New" → "Blueprint" 선택
3. 저장소 연결하면 render.yaml 기반으로 자동 설정
4. 환경 변수만 수동으로 추가 (MYSQL_URL)

---

## Railway MySQL + Render 애플리케이션 조합 (추천)

이 조합이 가장 간단합니다:

### Railway에서 MySQL만 생성
1. https://railway.app 로그인
2. "New Project" → "Provision MySQL"
3. MySQL 서비스 클릭 → "Connect" 탭
4. **"MySQL Connection URL"** 복사
   ```
   mysql://root:password@containers-us-west-123.railway.app:1234/railway
   ```

### Render에 애플리케이션 배포
1. Render에서 Web Service 생성 (위 단계 참고)
2. 환경 변수에 Railway MySQL URL 입력
3. 배포 완료!

**장점**:
- Railway MySQL은 무료로 1GB 제공
- Render Web Service도 무료
- 로컬(MySQL)과 프로덕션(MySQL) 환경 동일
- 설정 간단

---

## 배포 후 확인사항

1. **배포 로그 확인**
   - Render 대시보드 → "Logs" 탭
   - Spring Boot 시작 로그 확인
   - 에러 없는지 체크

2. **데이터베이스 연결 확인**
   - 로그에서 Hibernate 초기화 확인
   - 테이블 자동 생성 확인

3. **애플리케이션 접속**
   - Render 제공 URL로 접속
   - 로그인/회원가입 테스트

---

## 주의사항

- **첫 배포 시간**: Maven 의존성 다운로드로 10-15분 소요
- **무료 플랜 제약**:
  - 15분간 요청 없으면 자동으로 sleep 모드
  - Sleep 후 첫 요청 시 50초 정도 웨이크업 시간 소요
  - 750시간/월 무료 사용 시간 제공
- **파일 업로드**: 파일은 재배포 시 삭제됨 (외부 스토리지 권장)
- **환경 일치**: 로컬(MySQL)과 프로덕션(MySQL) 동일

---

## 트러블슈팅

### 빌드 실패 시
```bash
# 로컬에서 빌드 테스트
./mvnw clean package -DskipTests
```

### 데이터베이스 연결 실패 시
- Render Environment 탭에서 MYSQL_URL 확인
- MySQL URL 형식 확인: `mysql://user:password@host:port/database`
- Railway MySQL이면 Public Networking 활성화 확인

### 애플리케이션 시작 실패 시
- Logs 탭에서 에러 메시지 확인
- PORT 환경 변수는 Render가 자동 설정 (수동 설정 불필요)
- Java 17 사용 확인

### Sleep 모드 방지 (선택사항)
- 무료 플랜에서는 불가피
- 유료 플랜 ($7/월)으로 업그레이드하면 항상 활성화

---

## 다음 단계

1. Railway에서 MySQL 생성 (또는 다른 MySQL 서비스)
2. MySQL URL 복사
3. GitHub에 변경사항 푸시 (render.yaml 포함)
4. Render에서 Web Service 생성
5. 환경 변수 설정 (MYSQL_URL)
6. 배포 확인

---

## 커스텀 도메인 (선택사항)

Render는 무료 플랜에서도 커스텀 도메인 사용 가능:
1. Render 대시보드 → "Settings" → "Custom Domain"
2. 도메인 입력 및 DNS 설정
3. 자동 SSL 인증서 발급
