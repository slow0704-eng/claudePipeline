# Railway 배포 가이드

## 준비 완료 사항
- ✅ PostgreSQL 드라이버 추가
- ✅ Production 프로파일 설정 (application-prod.properties)
- ✅ Procfile 생성
- ✅ nixpacks.toml 설정

## Railway 배포 방법

### 방법 1: GitHub 연동 (추천)

1. **Railway 계정 생성**
   - https://railway.app 접속
   - GitHub 계정으로 로그인

2. **새 프로젝트 생성**
   - "New Project" 클릭
   - "Deploy from GitHub repo" 선택
   - `slow0704-eng/claudePipeline` 저장소 선택

3. **PostgreSQL 데이터베이스 추가**
   - 프로젝트 대시보드에서 "+ New" 클릭
   - "Database" → "Add PostgreSQL" 선택
   - 자동으로 DATABASE_URL 환경 변수가 설정됩니다

4. **환경 변수 설정**
   Railway 대시보드의 Variables 탭에서 다음 설정:
   ```
   SPRING_PROFILES_ACTIVE=prod
   ```
   (DATABASE_URL과 PORT는 Railway가 자동으로 설정)

5. **배포**
   - GitHub에 push하면 자동으로 배포됩니다
   - 배포 로그를 확인하여 성공 여부 체크

### 방법 2: Railway CLI 사용

1. **Railway CLI 설치**
   ```bash
   npm install -g @railway/cli
   ```

2. **로그인**
   ```bash
   railway login
   ```

3. **프로젝트 초기화**
   ```bash
   railway init
   ```

4. **PostgreSQL 추가**
   ```bash
   railway add
   # PostgreSQL 선택
   ```

5. **환경 변수 설정**
   ```bash
   railway variables set SPRING_PROFILES_ACTIVE=prod
   ```

6. **배포**
   ```bash
   railway up
   ```

## 배포 후 확인사항

1. **배포 상태 확인**
   - Railway 대시보드에서 "Deployments" 탭 확인
   - 로그에서 에러 없는지 체크

2. **데이터베이스 연결 확인**
   - 애플리케이션 로그에서 Hibernate 초기화 확인
   - 테이블이 자동 생성되었는지 확인

3. **도메인 확인**
   - Railway가 제공하는 URL로 접속
   - Settings → Domains에서 커스텀 도메인 설정 가능

## 주의사항

- **빌드 시간**: 첫 배포 시 Maven 의존성 다운로드로 5-10분 소요
- **메모리**: 무료 플랜은 512MB RAM 제한 있음
- **파일 업로드**: 업로드된 파일은 재배포 시 삭제됨 (S3 등 외부 스토리지 사용 권장)
- **데이터베이스**: 무료 플랜은 PostgreSQL 5GB 제한

## 트러블슈팅

### 빌드 실패 시
```bash
# 로컬에서 빌드 테스트
./mvnw clean package -DskipTests
```

### 데이터베이스 연결 실패 시
- Railway 대시보드에서 DATABASE_URL 환경 변수 확인
- PostgreSQL 플러그인이 제대로 연결되었는지 확인

### 메모리 부족 시
- nixpacks.toml에서 JAVA_OPTS 조정:
  ```toml
  [start]
  cmd = "java -Xmx400m -Xms256m -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/*.jar"
  ```

## 다음 단계

1. GitHub에 변경사항 커밋 및 푸시
2. Railway 프로젝트 생성
3. GitHub 저장소 연동
4. PostgreSQL 추가
5. 배포 확인
