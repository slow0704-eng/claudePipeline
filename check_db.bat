@echo off
chcp 65001 >nul

REM 배치 파일이 있는 디렉토리로 이동
cd /d "%~dp0"

echo ==========================================
echo PostgreSQL 연결 진단
echo ==========================================
echo.

REM DB 설정
set LOCAL_HOST=localhost
set LOCAL_PORT=5432
set LOCAL_DB=boarddb
set LOCAL_USER=postgres
set LOCAL_PASSWORD=1
set PGPASSWORD=%LOCAL_PASSWORD%

echo [1단계] PostgreSQL 서비스 확인
echo.
sc query postgresql-x64-16
echo.

echo ==========================================
echo [2단계] PostgreSQL 버전 확인
echo ==========================================
echo.
"C:\Program Files\PostgreSQL\16\bin\psql.exe" --version
echo.

echo ==========================================
echo [3단계] 데이터베이스 연결 테스트
echo ==========================================
echo.
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -c "SELECT version();"
echo.

if %ERRORLEVEL% EQU 0 (
    echo ✓ 데이터베이스 연결 성공!
    echo.

    echo ==========================================
    echo [4단계] 현재 데이터 확인
    echo ==========================================
    echo.

    echo --- 사용자 수 ---
    "C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -c "SELECT COUNT(*) as user_count FROM users;"
    echo.

    echo --- 게시글 수 ---
    "C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -c "SELECT COUNT(*) as board_count FROM board;"
    echo.

    echo --- 기존 user001~user050 사용자 확인 ---
    "C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -c "SELECT username FROM users WHERE username LIKE 'user%%' LIMIT 5;"
    echo.

    echo ==========================================
    echo 진단 완료!
    echo ==========================================
    echo.
    echo 연결이 정상입니다.
    echo.
    echo 만약 기존 데이터가 있다면:
    echo   clear_mock_data.bat 실행 후 다시 시도하세요.
    echo.
) else (
    echo ==========================================
    echo ✗ 데이터베이스 연결 실패!
    echo ==========================================
    echo.
    echo 가능한 원인:
    echo 1. PostgreSQL 서비스가 실행 중이지 않음
    echo 2. 비밀번호가 틀림
    echo 3. boarddb 데이터베이스가 없음
    echo.
    echo 해결 방법:
    echo.
    echo [PostgreSQL 서비스 시작]
    echo   net start postgresql-x64-16
    echo.
    echo [비밀번호 확인]
    echo   application.properties 파일의 비밀번호 확인
    echo   현재 설정: %LOCAL_PASSWORD%
    echo.
    echo [데이터베이스 생성]
    echo   애플리케이션을 한 번 실행하면 자동 생성됩니다.
    echo   mvnw.cmd spring-boot:run
    echo.
)

set PGPASSWORD=
echo.
pause
