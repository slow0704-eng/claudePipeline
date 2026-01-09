@echo off
REM Validate Reaction Migration
REM Date: 2025-12-30

echo ========================================
echo Reaction Migration Validation
echo ========================================
echo.

REM Read database connection info from application.properties
for /f "tokens=2 delims==" %%a in ('findstr "spring.datasource.url" src\main\resources\application.properties') do set DB_URL=%%a
for /f "tokens=2 delims==" %%a in ('findstr "spring.datasource.username" src\main\resources\application.properties') do set DB_USER=%%a

REM Extract database name from JDBC URL
for /f "tokens=5 delims=/" %%a in ("%DB_URL%") do set DB_NAME=%%a

echo Database: %DB_NAME%
echo User: %DB_USER%
echo.

REM Execute validation queries
psql -U %DB_USER% -d %DB_NAME% -f database\migrations\validate_reactions.sql

pause
