# Spring Boot 게시판 애플리케이션

엔터프라이즈급 커뮤니티 플랫폼 with RBAC, 소셜 기능, 파일 관리

## 빠른 배포

아래 버튼을 클릭하여 Render에 즉시 배포하세요:

[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/slow0704-eng/claudePipeline)

## 주요 기능

### 게시판 기능
- ✅ 게시글 CRUD (작성, 조회, 수정, 삭제)
- ✅ 계층형 댓글 시스템 (무제한 대댓글)
- ✅ 다중 파일 업로드 (최대 50MB, 5개/게시글)
- ✅ 임시저장 (자동 삭제: 30일)
- ✅ 카테고리 관리
- ✅ 게시글 상단 고정

### 소셜 기능
- ✅ 좋아요 (게시글, 댓글)
- ✅ 북마크
- ✅ 팔로우/언팔로우
- ✅ 1:1 쪽지
- ✅ 실시간 알림

### 검색 기능
- ✅ 통합 검색 (제목, 내용, 작성자)
- ✅ 고급 필터 (날짜, 조회수, 좋아요 수)
- ✅ 자동완성
- ✅ 인기 검색어

### 관리자 기능
- ✅ 대시보드 (통계, 모니터링)
- ✅ 사용자 관리
- ✅ 게시글 대량 관리
- ✅ 신고 처리
- ✅ 금지어 관리
- ✅ 파일 스토리지 관리
- ✅ RBAC (역할 기반 접근 제어)

## 기술 스택

- **Backend:** Spring Boot 3.2.0
- **Database:** PostgreSQL
- **Security:** Spring Security
- **Template Engine:** Thymeleaf
- **Build Tool:** Maven
- **Java Version:** 17

## 로컬 실행

### 사전 요구사항
- Java 17+
- PostgreSQL 16+
- Maven 3.9+

### 1. 데이터베이스 설정

```bash
# PostgreSQL 데이터베이스 생성
createdb boarddb
```

### 2. 애플리케이션 설정

`src/main/resources/application.properties` 파일 수정:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/boarddb
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 3. 애플리케이션 실행

```bash
# Maven으로 실행
./mvnw spring-boot:run

# 또는 빌드 후 실행
./mvnw clean package
java -jar target/board-1.0.0.jar
```

애플리케이션이 http://localhost:8080 에서 실행됩니다.

## 프로덕션 배포

### Render (추천)

1. 위의 "Deploy to Render" 버튼 클릭
2. GitHub 계정으로 로그인
3. "Apply" 클릭
4. 배포 완료 대기 (약 5-10분)

상세 가이드: [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)

### Docker

```bash
# Docker 이미지 빌드
docker build -t spring-board-app .

# 컨테이너 실행
docker run -p 8080:8080 \
  -e DATABASE_URL=your_database_url \
  -e SPRING_PROFILES_ACTIVE=prod \
  spring-board-app
```

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/board/
│   │   ├── controller/     # REST Controllers
│   │   ├── service/        # Business Logic
│   │   ├── entity/         # JPA Entities
│   │   ├── repository/     # Data Access
│   │   ├── config/         # Configuration
│   │   ├── scheduler/      # Scheduled Tasks
│   │   └── security/       # Security Config
│   └── resources/
│       ├── templates/      # Thymeleaf Templates
│       ├── static/         # CSS, JS, Images
│       └── application.properties
└── test/                   # Unit & Integration Tests
```

## 엔티티 모델

- **User** - 사용자
- **Board** - 게시글
- **Comment** - 댓글
- **Attachment** - 첨부파일
- **Like** - 좋아요
- **Bookmark** - 북마크
- **Follow** - 팔로우
- **Message** - 쪽지
- **Notification** - 알림
- **Report** - 신고
- **Category** - 카테고리
- **Role** - 역할 (RBAC)
- **Menu** - 메뉴 (RBAC)

## 기능점수 & 비용

- **조정 기능점수:** 469 FP
- **개발 공수:** 4,221시간 (27 인월)
- **예상 개발 비용:** 약 3억 4,200만원

상세 분석: 프로젝트 루트의 기능점수 분석 문서 참조

## API 엔드포인트

### 인증
- `POST /auth/register` - 회원가입
- `POST /auth/login` - 로그인
- `POST /auth/logout` - 로그아웃

### 게시판
- `GET /board` - 게시글 목록
- `GET /board/{id}` - 게시글 상세
- `POST /board` - 게시글 작성
- `PUT /board/{id}` - 게시글 수정
- `DELETE /board/{id}` - 게시글 삭제

### 댓글
- `POST /comments` - 댓글 작성
- `DELETE /comments/{id}` - 댓글 삭제

### 검색
- `GET /search?keyword={keyword}` - 통합 검색

## 환경 변수

프로덕션 환경에서 설정 필요:

```bash
DATABASE_URL=postgresql://user:password@host:port/database
PORT=8080
SPRING_PROFILES_ACTIVE=prod
JAVA_TOOL_OPTIONS=-Xmx512m -Xms256m
```

## 보안

- Spring Security 기반 인증/인가
- 비밀번호 암호화 (BCrypt)
- CSRF 보호
- XSS 방지
- SQL Injection 방지
- 금지어 필터링

## 라이선스

이 프로젝트는 교육 목적으로 제작되었습니다.

## 지원

이슈나 질문이 있으시면 [GitHub Issues](https://github.com/slow0704-eng/claudePipeline/issues)에 등록해주세요.

---

**Made with ❤️ using Spring Boot & Claude Code**
