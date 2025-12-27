@echo off
chcp 65001 > nul
echo ====================================
echo PostgreSQL 데이터베이스 생성
echo ====================================
echo.

REM PostgreSQL 설정
set PGUSER=postgres
set PGPASSWORD=1q2w3e4r!
set PGDATABASE=boarddb
set PGHOST=localhost
set PGPORT=5432

echo PostgreSQL 데이터베이스를 생성합니다...
echo 데이터베이스명: %PGDATABASE%
echo.

REM 기존 데이터베이스 삭제 (선택사항)
echo 기존 데이터베이스가 있다면 삭제합니다...
psql -U %PGUSER% -h %PGHOST% -p %PGPORT% -c "DROP DATABASE IF EXISTS %PGDATABASE%;" 2>nul

REM 새 데이터베이스 생성
echo 새 데이터베이스를 생성합니다...
psql -U %PGUSER% -h %PGHOST% -p %PGPORT% -c "CREATE DATABASE %PGDATABASE% WITH ENCODING='UTF8' LC_COLLATE='ko_KR.UTF-8' LC_CTYPE='ko_KR.UTF-8' TEMPLATE=template0;"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ 데이터베이스 생성 완료!
    echo.
    echo 다음 단계:
    echo 1. mysql_export.bat을 실행하여 MySQL 데이터를 export하세요 (아직 안했다면)
    echo 2. postgres_import.bat을 실행하여 데이터를 import하세요
    echo 3. 서버시작.bat을 실행하여 애플리케이션을 시작하세요
) else (
    echo.
    echo ✗ 데이터베이스 생성 실패!
    echo PostgreSQL이 설치되어 있고 실행 중인지 확인하세요.
    echo 사용자명과 비밀번호가 올바른지 확인하세요.
    echo.
    echo PostgreSQL 서비스 상태 확인:
    sc query postgresql-x64-16
)

echo.
pause
