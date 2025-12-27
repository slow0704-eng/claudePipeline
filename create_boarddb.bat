@echo off
set PGPASSWORD=1
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -h localhost -c "DROP DATABASE IF EXISTS boarddb;"
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -h localhost -c "CREATE DATABASE boarddb WITH ENCODING='UTF8';"
echo Database boarddb created successfully!
pause
