@echo off
chcp 65001 >nul

REM 배치 파일이 있는 디렉토리로 이동
cd /d "%~dp0"

echo ==========================================
echo 간단 DB 초기화 및 목 데이터 삽입
echo ==========================================
echo.

echo 📝 수동으로 진행하는 단계별 가이드
echo.
echo ⚠️  이 스크립트는 각 단계를 안내만 합니다.
echo    각 단계를 직접 수행해주세요.
echo.
pause

echo.
echo ==========================================
echo 1단계: application.properties 수정
echo ==========================================
echo.
echo 파일을 메모장으로 엽니다...
echo.
start notepad "src\main\resources\application.properties"
echo.
echo ✏️  다음 줄을 찾아서:
echo    spring.jpa.hibernate.ddl-auto=update
echo.
echo ✏️  이렇게 변경:
echo    spring.jpa.hibernate.ddl-auto=create
echo.
echo 저장하고 메모장을 닫으세요.
echo.
pause

echo.
echo ==========================================
echo 2단계: Spring Boot 시작 (테이블 재생성)
echo ==========================================
echo.
echo 새 명령 프롬프트를 열고 다음 명령 실행:
echo.
echo    mvnw.cmd spring-boot:run
echo.
echo 브라우저에서 http://localhost:8080 접속이 되면
echo Ctrl+C를 눌러서 애플리케이션을 종료하세요.
echo.
echo (모든 테이블이 삭제되고 재생성됩니다)
echo.
pause

echo.
echo ==========================================
echo 3단계: application.properties 복원
echo ==========================================
echo.
echo 파일을 다시 메모장으로 엽니다...
echo.
start notepad "src\main\resources\application.properties"
echo.
echo ✏️  다음 줄을 찾아서:
echo    spring.jpa.hibernate.ddl-auto=create
echo.
echo ✏️  이렇게 복원:
echo    spring.jpa.hibernate.ddl-auto=update
echo.
echo 저장하고 메모장을 닫으세요.
echo.
pause

echo.
echo ==========================================
echo 4단계: 목 데이터 삽입
echo ==========================================
echo.
echo 이제 목 데이터를 삽입합니다...
echo.

set LOCAL_HOST=localhost
set LOCAL_PORT=5432
set LOCAL_DB=boarddb
set LOCAL_USER=postgres
set LOCAL_PASSWORD=1
set PGPASSWORD=%LOCAL_PASSWORD%

echo --- 1. 사용자 (50명) ---
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/01_users.sql" 2>nul
if %ERRORLEVEL% EQU 0 (echo ✓ 사용자 완료) else (echo ✗ 사용자 실패)

echo --- 2. 게시글 (300+개) ---
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/02_boards.sql" 2>nul
if %ERRORLEVEL% EQU 0 (echo ✓ 게시글 완료) else (echo ✗ 게시글 실패)

echo --- 3. 팔로우 (200개) ---
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/03_follows.sql" 2>nul
if %ERRORLEVEL% EQU 0 (echo ✓ 팔로우 완료) else (echo ✗ 팔로우 실패)

echo --- 4. 좋아요 (300+개) ---
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/04_likes.sql" 2>nul
if %ERRORLEVEL% EQU 0 (echo ✓ 좋아요 완료) else (echo ✗ 좋아요 실패)

echo --- 5. 북마크 (100개) ---
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/05_bookmarks.sql" 2>nul
if %ERRORLEVEL% EQU 0 (echo ✓ 북마크 완료) else (echo ✗ 북마크 실패)

echo --- 6. 댓글 (60개) ---
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/06_comments.sql" 2>nul
if %ERRORLEVEL% EQU 0 (echo ✓ 댓글 완료) else (echo ✗ 댓글 실패)

echo --- 7. 해시태그 (78개) ---
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/07_hashtags.sql" 2>nul
if %ERRORLEVEL% EQU 0 (echo ✓ 해시태그 완료) else (echo ✗ 해시태그 실패)

echo.
echo ==========================================
echo ✓ 완료!
echo ==========================================
echo.
echo 목 데이터 삽입이 완료되었습니다.
echo.
echo 애플리케이션 시작:
echo   mvnw.cmd spring-boot:run
echo.
echo 테스트 계정:
echo   user001 / 1234
echo.

set PGPASSWORD=
pause
