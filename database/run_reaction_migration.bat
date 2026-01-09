@echo off
REM Execute Reaction Type Migration
REM Date: 2025-12-30

echo ========================================
echo Reaction Type Migration Execution
echo ========================================
echo.
echo WARNING: This will modify your database schema!
echo Make sure you have created a backup first.
echo.
pause

REM Read database connection info from application.properties
for /f "tokens=2 delims==" %%a in ('findstr "spring.datasource.url" src\main\resources\application.properties') do set DB_URL=%%a
for /f "tokens=2 delims==" %%a in ('findstr "spring.datasource.username" src\main\resources\application.properties') do set DB_USER=%%a

REM Extract database name from JDBC URL
for /f "tokens=5 delims=/" %%a in ("%DB_URL%") do set DB_NAME=%%a

echo Database: %DB_NAME%
echo User: %DB_USER%
echo Migration File: database\migrations\add_reaction_types.sql
echo.

REM Execute migration
psql -U %DB_USER% -d %DB_NAME% -f database\migrations\add_reaction_types.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Migration completed successfully!
    echo ========================================
    echo.
    echo Next steps:
    echo 1. Run validation: database\validate_reaction_migration.bat
    echo 2. Restart Spring Boot application
    echo 3. Test reaction functionality in browser
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ERROR: Migration failed!
    echo Check the error messages above.
    echo ========================================
    exit /b 1
)

pause
