@echo off
chcp 65001 >nul

cd /d "%~dp0"

set PGPASSWORD=1

echo ====================================
echo 기술 게시글 100개 삽입
echo ====================================
echo.

"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -f "insert_tech_boards.sql"

if %ERRORLEVEL% NEQ 0 (
    echo ✗ 게시글 데이터 삽입 실패
    pause
    exit /b 1
)

echo.
echo ✓ 기술 게시글 100개 삽입 완료!
echo.

REM 환경변수 초기화
set PGPASSWORD=
