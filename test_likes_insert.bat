@echo off
chcp 65001 >nul
cd /d "%~dp0"

set PGPASSWORD=1

echo Testing likes insertion...
echo.

"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -f "mock-data/04_likes.sql"

echo.
echo Done.
pause
