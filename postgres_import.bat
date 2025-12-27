@echo off
chcp 65001 > nul
echo ====================================
echo PostgreSQL 데이터 Import
echo ====================================
echo.

REM PostgreSQL 설정
set PGUSER=postgres
set PGPASSWORD=1q2w3e4r!
set PGDATABASE=boarddb
set PGHOST=localhost
set PGPORT=5432

REM 파일 확인
if not exist boarddb_export.sql (
    echo ✗ 오류: boarddb_export.sql 파일이 없습니다.
    echo mysql_export.bat을 먼저 실행하세요.
    pause
    exit /b 1
)

echo 1단계: MySQL dump를 PostgreSQL 형식으로 변환합니다...
echo.

REM Python 스크립트로 변환
python convert_to_postgresql.py boarddb_export.sql boarddb_postgresql.sql

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ✗ 변환 실패!
    echo Python 3가 설치되어 있는지 확인하세요.
    pause
    exit /b 1
)

echo.
echo 2단계: PostgreSQL로 데이터를 import합니다...
echo.

REM JPA가 스키마를 자동 생성하도록 먼저 애플리케이션을 한 번 실행해야 합니다
echo 주의: 먼저 Spring Boot 애플리케이션을 한 번 실행하여
echo       JPA가 테이블을 생성하도록 해야 합니다.
echo.
echo 계속하려면 아무 키나 누르세요...
pause > nul

REM 변환된 파일을 PostgreSQL로 import
psql -U %PGUSER% -h %PGHOST% -p %PGPORT% -d %PGDATABASE% -f boarddb_postgresql.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ Import 완료!
    echo.
    echo 다음 단계:
    echo 1. 서버시작.bat을 실행하여 애플리케이션을 시작하세요
    echo 2. http://localhost:8080 에서 애플리케이션을 확인하세요
) else (
    echo.
    echo ✗ Import 실패!
    echo 오류 메시지를 확인하고 필요시 수동으로 수정하세요.
)

echo.
pause
