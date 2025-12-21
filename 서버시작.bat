@echo off
chcp 65001 > nul
echo ========================================
echo   Spring Boot 서버 시작
echo ========================================
echo.
echo 서버를 시작합니다...
echo.

cd /d "%~dp0"
call mvnw.cmd spring-boot:run

pause
