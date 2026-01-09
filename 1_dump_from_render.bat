@echo off
chcp 65001 >nul
echo ==========================================
echo Render 운영 DB에서 데이터 덤프
echo ==========================================
echo.

REM Render 대시보드에서 확인한 연결 정보를 입력하세요
REM 예: dpg-xxxxxxxxxxxx-a.oregon-postgres.render.com
set /p RENDER_HOST="Render DB Host: "
set /p RENDER_PORT="Render DB Port (기본값 5432): "
if "%RENDER_PORT%"=="" set RENDER_PORT=5432
set /p RENDER_DB="Render DB Name (기본값 boarddb): "
if "%RENDER_DB%"=="" set RENDER_DB=boarddb
set /p RENDER_USER="Render DB User (기본값 boarduser): "
if "%RENDER_USER%"=="" set RENDER_USER=boarduser
set /p RENDER_PASSWORD="Render DB Password: "

echo.
echo 연결 정보 확인:
echo Host: %RENDER_HOST%
echo Port: %RENDER_PORT%
echo Database: %RENDER_DB%
echo User: %RENDER_USER%
echo.
pause

echo.
echo 데이터 덤프 중...
echo.

REM PostgreSQL 비밀번호 환경변수 설정
set PGPASSWORD=%RENDER_PASSWORD%

REM pg_dump 실행 (스키마 + 데이터)
"C:\Program Files\PostgreSQL\16\bin\pg_dump.exe" ^
  -h %RENDER_HOST% ^
  -p %RENDER_PORT% ^
  -U %RENDER_USER% ^
  -d %RENDER_DB% ^
  --no-owner ^
  --no-privileges ^
  -F c ^
  -f render_backup.dump

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ 덤프 완료: render_backup.dump
    echo.
    echo 다음 단계: 2_restore_to_local.bat 실행
) else (
    echo.
    echo ✗ 덤프 실패
    echo 연결 정보를 확인하고 다시 시도하세요.
)

REM 환경변수 초기화
set PGPASSWORD=

echo.
pause
