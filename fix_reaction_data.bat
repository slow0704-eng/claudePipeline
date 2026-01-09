@echo off
chcp 65001 >nul

cd /d "%~dp0"

echo ========================================
echo 반응 시스템 데이터 수정
echo ========================================
echo.
echo 이 스크립트는 Hibernate 오류를 해결합니다.
echo.
pause

set PGPASSWORD=1

"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -h localhost -d boarddb -f fix_reaction_data.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo ✓ 데이터 수정 완료!
    echo ========================================
    echo.
    echo 이제 Spring Boot를 다시 시작하세요:
    echo   mvnw spring-boot:run
    echo.
) else (
    echo.
    echo ========================================
    echo ✗ 오류 발생!
    echo ========================================
)

pause
