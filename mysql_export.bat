@echo off
chcp 65001 > nul
echo ====================================
echo MySQL 데이터 Export
echo ====================================
echo.

REM MySQL 설정
set MYSQL_USER=root
set MYSQL_PASSWORD=
set MYSQL_DATABASE=boarddb
set OUTPUT_FILE=boarddb_export.sql

echo MySQL에서 데이터를 export합니다...
echo 출력 파일: %OUTPUT_FILE%
echo.

REM mysqldump 실행
if "%MYSQL_PASSWORD%"=="" (
    mysqldump -u %MYSQL_USER% --single-transaction --skip-triggers --no-create-db --compatible=postgresql %MYSQL_DATABASE% > %OUTPUT_FILE%
) else (
    mysqldump -u %MYSQL_USER% -p%MYSQL_PASSWORD% --single-transaction --skip-triggers --no-create-db --compatible=postgresql %MYSQL_DATABASE% > %OUTPUT_FILE%
)

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ Export 완료!
    echo 파일 위치: %CD%\%OUTPUT_FILE%
    echo.
    echo 다음 단계:
    echo 1. PostgreSQL이 설치되어 있는지 확인하세요
    echo 2. postgres_setup.bat을 실행하여 데이터베이스를 생성하세요
    echo 3. postgres_import.bat을 실행하여 데이터를 import하세요
) else (
    echo.
    echo ✗ Export 실패!
    echo mysqldump 명령어를 찾을 수 없거나 오류가 발생했습니다.
    echo MySQL이 설치되어 있고 PATH에 등록되어 있는지 확인하세요.
)

echo.
pause
