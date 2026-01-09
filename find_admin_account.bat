@echo off
chcp 65001 >nul

cd /d "%~dp0"

set PGPASSWORD=1

echo ========================================
echo 관리자 계정 조회
echo ========================================
echo.

"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -h localhost -d boarddb -c "SELECT id, username, nickname, email, role, created_at, enabled FROM users WHERE role = 'ADMIN' ORDER BY id;"

echo.
echo ========================================
echo 전체 사용자 role 분포:
echo ========================================
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -h localhost -d boarddb -c "SELECT role, COUNT(*) as count FROM users GROUP BY role;"

echo.
pause
