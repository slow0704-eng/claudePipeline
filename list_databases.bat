@echo off
chcp 65001 >nul

REM 배치 파일이 있는 디렉토리로 이동
cd /d "%~dp0"

echo ==========================================
echo PostgreSQL 데이터베이스 목록
echo ==========================================
echo.

set PGPASSWORD=1

echo [1] 모든 데이터베이스 목록:
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -l

echo.
echo ==========================================
echo [2] boarddb 데이터베이스 테이블 확인:
echo ==========================================
echo.

"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -c "\dt"

echo.
echo ==========================================
echo [3] boarddb 데이터 개수 확인:
echo ==========================================
echo.

"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -c "SELECT 'users' as table_name, COUNT(*) as count FROM users UNION ALL SELECT 'board', COUNT(*) FROM board UNION ALL SELECT 'comment', COUNT(*) FROM comment UNION ALL SELECT 'board_like', COUNT(*) FROM board_like UNION ALL SELECT 'bookmark', COUNT(*) FROM bookmark UNION ALL SELECT 'user_follow', COUNT(*) FROM user_follow UNION ALL SELECT 'hashtag', COUNT(*) FROM hashtag;"

set PGPASSWORD=
echo.
pause
