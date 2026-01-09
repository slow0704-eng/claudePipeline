@echo off
chcp 65001 >nul

REM 배치 파일이 있는 디렉토리로 이동
cd /d "%~dp0"

echo ==========================================
echo 현재 데이터베이스 상태 확인
echo ==========================================
echo.

set LOCAL_HOST=localhost
set LOCAL_PORT=5432
set LOCAL_DB=boarddb
set LOCAL_USER=postgres
set LOCAL_PASSWORD=1
set PGPASSWORD=%LOCAL_PASSWORD%

echo [1] 사용자 수 확인
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -c "SELECT COUNT(*) as user_count FROM users;"
echo.

echo [2] 사용자 목록 (최근 10명)
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -c "SELECT id, username, nickname, created_at FROM users ORDER BY id DESC LIMIT 10;"
echo.

echo [3] 게시글 수 확인
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -c "SELECT COUNT(*) as board_count FROM board;"
echo.

echo [4] 게시글 목록 (최근 5개)
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -c "SELECT id, title, view_count, like_count, created_at FROM board ORDER BY id DESC LIMIT 5;"
echo.

echo [5] admin 계정 확인
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -c "SELECT id, username, nickname, role FROM users WHERE username = 'admin';"
echo.

echo [6] user001 계정 확인 (목 데이터)
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -c "SELECT id, username, nickname, role FROM users WHERE username = 'user001';"
echo.

echo ==========================================
echo 분석 결과
echo ==========================================
echo.
echo - user_count가 1-2개면: 목 데이터가 삽입되지 않음
echo - user_count가 50+개면: 목 데이터 삽입 성공
echo - board_count가 0개면: 게시글 데이터 미삽입
echo - board_count가 300+개면: 게시글 데이터 삽입 성공
echo - admin 계정이 있으면: 기존 데이터가 남아있음
echo - user001 계정이 있으면: 목 데이터가 삽입됨
echo.

set PGPASSWORD=
pause
