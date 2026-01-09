@echo off
chcp 65001 >nul

REM 배치 파일이 있는 디렉토리로 이동
cd /d "%~dp0"

echo ==========================================
echo 인덱스 충돌 해결
echo ==========================================
echo.

echo 기존 중복 인덱스를 삭제합니다.
echo 애플리케이션을 재시작하면 Hibernate가 새 인덱스를 생성합니다.
echo.
pause

set LOCAL_HOST=localhost
set LOCAL_PORT=5432
set LOCAL_DB=boarddb
set LOCAL_USER=postgres
set LOCAL_PASSWORD=1
set PGPASSWORD=%LOCAL_PASSWORD%

echo.
echo 인덱스 삭제 중...
echo.

"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "fix_index_conflict.sql"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ==========================================
    echo ✓ 인덱스 충돌 해결 완료!
    echo ==========================================
    echo.
    echo 이제 애플리케이션을 재시작하세요:
    echo   mvnw.cmd spring-boot:run
    echo.
    echo Hibernate가 새 인덱스를 자동으로 생성합니다:
    echo   - idx_uhf_user_id
    echo   - idx_uhf_hashtag_id
    echo.
) else (
    echo.
    echo ==========================================
    echo ✗ 오류 발생
    echo ==========================================
    echo.
    echo PostgreSQL이 실행 중인지 확인하세요.
    echo.
)

set PGPASSWORD=
echo.
pause
