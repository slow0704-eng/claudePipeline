@echo off
chcp 65001 >nul

cd /d "%~dp0"

set PGPASSWORD=1

echo ====================================
echo 사용자 데이터 확인
echo ====================================
echo.

"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -h localhost -d boarddb -c "SELECT COUNT(*) as total_users, MIN(id) as min_id, MAX(id) as max_id FROM users;"

echo.
echo ID 43 사용자 확인:
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -h localhost -d boarddb -c "SELECT id, username, nickname FROM users WHERE id = 43;"

echo.
echo 전체 사용자 목록 (처음 10명):
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -h localhost -d boarddb -c "SELECT id, username, nickname FROM users ORDER BY id LIMIT 10;"

echo.
pause
