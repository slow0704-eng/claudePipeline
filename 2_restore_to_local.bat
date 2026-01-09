@echo off
chcp 65001 >nul
echo ==========================================
echo 로컬 PostgreSQL로 데이터 복원
echo ==========================================
echo.

REM 로컬 DB 설정
set LOCAL_HOST=localhost
set LOCAL_PORT=5432
set LOCAL_DB=boarddb
set LOCAL_USER=postgres
set LOCAL_PASSWORD=1

echo 로컬 DB 정보:
echo Host: %LOCAL_HOST%
echo Port: %LOCAL_PORT%
echo Database: %LOCAL_DB%
echo User: %LOCAL_USER%
echo.

echo 주의: 기존 로컬 DB 데이터가 있다면 삭제됩니다!
echo.
pause

echo.
echo 1단계: 기존 DB 삭제 및 재생성
echo.

REM PostgreSQL 비밀번호 설정
set PGPASSWORD=%LOCAL_PASSWORD%

REM 기존 DB 삭제
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U %LOCAL_USER% -h %LOCAL_HOST% -c "DROP DATABASE IF EXISTS %LOCAL_DB%;"

REM 새 DB 생성
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U %LOCAL_USER% -h %LOCAL_HOST% -c "CREATE DATABASE %LOCAL_DB% WITH ENCODING='UTF8';"

echo.
echo 2단계: 덤프 파일 복원
echo.

REM pg_restore 실행
"C:\Program Files\PostgreSQL\16\bin\pg_restore.exe" ^
  -h %LOCAL_HOST% ^
  -p %LOCAL_PORT% ^
  -U %LOCAL_USER% ^
  -d %LOCAL_DB% ^
  --no-owner ^
  --no-privileges ^
  --clean ^
  --if-exists ^
  render_backup.dump

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ 복원 완료!
    echo.
    echo 로컬 DB에 운영 데이터가 성공적으로 복원되었습니다.
    echo.
    echo 애플리케이션을 시작하세요: mvnw.cmd spring-boot:run
) else (
    echo.
    echo ✗ 복원 중 일부 오류 발생
    echo 대부분의 데이터는 복원되었을 수 있습니다.
)

REM 환경변수 초기화
set PGPASSWORD=

echo.
pause
