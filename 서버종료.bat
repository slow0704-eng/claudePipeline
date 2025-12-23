@echo off
chcp 65001 > nul
echo ========================================
echo   Spring Boot 서버 종료
echo ========================================
echo.

cd /d "%~dp0"

echo 포트 8080을 사용하는 프로세스를 찾는 중...
echo.

REM 포트 8080을 사용하는 프로세스 찾기
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
    set PID=%%a
    goto :found
)
goto :notfound

:found
echo ⚠️  포트 8080을 사용 중인 프로세스 발견 (PID: %PID%)
echo 서버를 종료합니다...
taskkill /PID %PID% /F
if %errorlevel% equ 0 (
    echo.
    echo ✓ 서버가 정상적으로 종료되었습니다.
) else (
    echo.
    echo ✗ 서버 종료 중 오류가 발생했습니다.
)
goto :end

:notfound
echo ✓ 실행 중인 서버가 없습니다.

:end
echo.
pause
