@echo off
chcp 65001 >nul

REM 배치 파일이 있는 디렉토리로 이동
cd /d "%~dp0"

echo ========================================
echo PostgreSQL 로그 뷰어
echo ========================================
echo.

REM 로그 디렉토리로 이동
cd "C:\Program Files\PostgreSQL\16\data\log" 2>nul

if errorlevel 1 (
    echo 로그 디렉토리를 찾을 수 없습니다.
    echo.
    echo 다른 경로 시도 중...
    cd "C:\Program Files\PostgreSQL\16\data\pg_log" 2>nul

    if errorlevel 1 (
        echo 로그 디렉토리를 찾을 수 없습니다!
        echo.
        echo PostgreSQL 설치 경로를 확인하세요:
        echo - C:\Program Files\PostgreSQL\16\data\log
        echo - C:\Program Files\PostgreSQL\16\data\pg_log
        pause
        exit /b 1
    )
)

echo 현재 경로: %CD%
echo.
echo 최근 로그 파일 목록:
echo ----------------------------------------
dir /O-D /B *.log
echo ----------------------------------------
echo.

REM 가장 최근 로그 파일 찾기
for /f "delims=" %%a in ('dir /B /O-D *.log') do (
    set LATEST_LOG=%%a
    goto :found
)

:found
echo 가장 최근 로그 파일: %LATEST_LOG%
echo.
echo [1] 전체 로그 보기
echo [2] 최근 50줄만 보기
echo [3] 실시간 로그 보기 (Ctrl+C로 종료)
echo [4] 종료
echo.
set /p choice=선택하세요 (1-4):

if "%choice%"=="1" (
    type "%LATEST_LOG%"
    echo.
    pause
) else if "%choice%"=="2" (
    powershell "Get-Content '%LATEST_LOG%' -Tail 50"
    echo.
    pause
) else if "%choice%"=="3" (
    echo 실시간 로그 보기 시작... (Ctrl+C로 종료)
    echo.
    powershell "Get-Content '%LATEST_LOG%' -Wait -Tail 20"
) else if "%choice%"=="4" (
    exit /b 0
) else (
    echo 잘못된 선택입니다.
    pause
)

pause
