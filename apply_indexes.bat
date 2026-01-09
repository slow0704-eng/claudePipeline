@echo off
REM ==========================================
REM Database Performance Indexes 적용 스크립트
REM ==========================================

echo.
echo ========================================
echo Database Performance Indexes 적용
echo ========================================
echo.

REM PostgreSQL 경로 설정 (환경에 맞게 수정)
set PSQL_PATH=psql

REM 데이터베이스 정보
set DB_NAME=boarddb
set DB_USER=postgres

echo 데이터베이스: %DB_NAME%
echo 사용자: %DB_USER%
echo.

REM 마이그레이션 파일 경로
set MIGRATION_FILE=database\migrations\V001__add_performance_indexes.sql

REM 파일 존재 확인
if not exist "%MIGRATION_FILE%" (
    echo [ERROR] 마이그레이션 파일을 찾을 수 없습니다: %MIGRATION_FILE%
    pause
    exit /b 1
)

echo 마이그레이션 파일: %MIGRATION_FILE%
echo.

REM 확인 메시지
echo 데이터베이스에 성능 인덱스를 추가합니다.
echo 계속하시겠습니까? (Y/N)
set /p CONFIRM=

if /i not "%CONFIRM%"=="Y" (
    echo 작업이 취소되었습니다.
    pause
    exit /b 0
)

echo.
echo 인덱스 생성 중...
echo.

REM psql로 마이그레이션 실행
%PSQL_PATH% -U %DB_USER% -d %DB_NAME% -f "%MIGRATION_FILE%"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo 인덱스가 성공적으로 생성되었습니다!
    echo ========================================
    echo.
    echo 다음 명령으로 인덱스를 확인할 수 있습니다:
    echo   psql -U %DB_USER% -d %DB_NAME% -c "\di"
    echo.
) else (
    echo.
    echo [ERROR] 인덱스 생성 중 오류가 발생했습니다.
    echo 오류 코드: %ERRORLEVEL%
    echo.
)

pause
