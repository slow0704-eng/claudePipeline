@echo off
chcp 65001 >nul

REM 배치 파일이 있는 디렉토리로 이동
cd /d "%~dp0"

echo ==========================================
echo DB 완전 초기화 및 목 데이터 삽입
echo ==========================================
echo.

echo 이 스크립트는 다음을 수행합니다:
echo 1. application.properties를 백업
echo 2. ddl-auto를 create로 변경
echo 3. Spring Boot 애플리케이션 시작 (테이블 재생성)
echo 4. 애플리케이션 자동 종료 (10초 후)
echo 5. ddl-auto를 update로 복원
echo 6. 목 데이터 삽입
echo.
echo ⚠️  주의: 기존 데이터가 모두 삭제됩니다!
echo.
pause

REM 1. application.properties 백업
echo.
echo [1/6] application.properties 백업 중...
copy "src\main\resources\application.properties" "src\main\resources\application.properties.backup" >nul
echo ✓ 백업 완료
echo.

REM 2. ddl-auto를 create로 변경
echo [2/6] ddl-auto를 create로 변경 중...
powershell -NoProfile -ExecutionPolicy Bypass -Command "& {Set-Location -LiteralPath '%CD%'; (Get-Content 'src\main\resources\application.properties' -Encoding UTF8) -replace 'spring.jpa.hibernate.ddl-auto=update', 'spring.jpa.hibernate.ddl-auto=create' | Set-Content 'src\main\resources\application.properties' -Encoding UTF8}"
echo ✓ 변경 완료
echo.

REM 3. Spring Boot 실행 (테이블 재생성)
echo [3/6] Spring Boot 시작 중 (테이블 재생성)...
echo.
echo 애플리케이션이 시작되면 10초 후 자동으로 종료됩니다.
echo 기다려주세요...
echo.

start /B cmd /c "mvnw.cmd spring-boot:run > nul 2>&1"

REM 15초 대기 (애플리케이션 시작 시간 포함)
timeout /t 15 /nobreak >nul

REM 4. Spring Boot 프로세스 종료
echo.
echo [4/6] 애플리케이션 종료 중...
taskkill /F /IM java.exe >nul 2>&1
timeout /t 3 /nobreak >nul
echo ✓ 종료 완료
echo.

REM 5. ddl-auto를 update로 복원
echo [5/6] ddl-auto를 update로 복원 중...
powershell -NoProfile -ExecutionPolicy Bypass -Command "& {Set-Location -LiteralPath '%CD%'; (Get-Content 'src\main\resources\application.properties' -Encoding UTF8) -replace 'spring.jpa.hibernate.ddl-auto=create', 'spring.jpa.hibernate.ddl-auto=update' | Set-Content 'src\main\resources\application.properties' -Encoding UTF8}"
echo ✓ 복원 완료
echo.

REM 6. 목 데이터 삽입
echo [6/6] 목 데이터 삽입 중...
echo.

set LOCAL_HOST=localhost
set LOCAL_PORT=5432
set LOCAL_DB=boarddb
set LOCAL_USER=postgres
set LOCAL_PASSWORD=1
set PGPASSWORD=%LOCAL_PASSWORD%

echo --- 1단계: 사용자 (50명) ---
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/01_users.sql" 2>nul
if %ERRORLEVEL% EQU 0 (echo ✓ 사용자 삽입 완료) else (echo ✗ 사용자 삽입 실패 & goto :error)

echo --- 2단계: 게시글 (300+개) ---
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/02_boards.sql" 2>nul
if %ERRORLEVEL% EQU 0 (echo ✓ 게시글 삽입 완료) else (echo ✗ 게시글 삽입 실패 & goto :error)

echo --- 3단계: 팔로우 (200개) ---
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/03_follows.sql" 2>nul
if %ERRORLEVEL% EQU 0 (echo ✓ 팔로우 삽입 완료) else (echo ✗ 팔로우 삽입 실패 & goto :error)

echo --- 4단계: 좋아요 (276개) ---
python insert_all_likes.py >nul 2>&1
if %ERRORLEVEL% EQU 0 (echo ✓ 좋아요 삽입 완료) else (echo ✗ 좋아요 삽입 실패 & goto :error)

echo --- 5단계: 북마크 (100개) ---
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/05_bookmarks.sql" 2>nul
if %ERRORLEVEL% EQU 0 (echo ✓ 북마크 삽입 완료) else (echo ✗ 북마크 삽입 실패 & goto :error)

echo --- 6단계: 댓글 (60개) ---
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/06_comments.sql" 2>nul
if %ERRORLEVEL% EQU 0 (echo ✓ 댓글 삽입 완료) else (echo ✗ 댓글 삽입 실패 & goto :error)

echo --- 7단계: 해시태그 (78개 + 90+개 연결) ---
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/07_hashtags.sql" 2>nul
if %ERRORLEVEL% EQU 0 (echo ✓ 해시태그 삽입 완료) else (echo ✗ 해시태그 삽입 실패 & goto :error)

echo.
echo ==========================================
echo ✓ 모든 작업 완료!
echo ==========================================
echo.
echo DB가 완전히 초기화되고 목 데이터가 삽입되었습니다.
echo.
echo 삽입된 데이터:
echo - 사용자: 50명 (admin은 없음, user001~user050만)
echo - 게시글: 300+개
echo - 팔로우: 200개
echo - 좋아요: 276개 (Board 1-20에 분산)
echo - 북마크: 100개
echo - 댓글: 60개
echo - 해시태그: 78개
echo.
echo 테스트 계정:
echo - user001 / 1234
echo - user002 / 1234
echo ... user050 / 1234
echo.
echo 애플리케이션 시작: mvnw.cmd spring-boot:run
echo.
goto :end

:error
echo.
echo ==========================================
echo ✗ 오류 발생
echo ==========================================
echo.
echo 데이터 삽입 중 오류가 발생했습니다.
echo.
echo 수동으로 확인하려면:
echo 1. check_db.bat 실행하여 DB 상태 확인
echo 2. PostgreSQL이 실행 중인지 확인
echo.

:end
set PGPASSWORD=
echo.
pause
