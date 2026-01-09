@echo off
REM Database Backup Before Reaction Migration
REM Date: 2025-12-30

echo ========================================
echo Database Backup for Reaction Migration
echo ========================================
echo.

REM Generate timestamp for backup file
for /f "tokens=2 delims==" %%I in ('wmic os get localdatetime /value') do set datetime=%%I
set BACKUP_FILE=backup_before_reactions_%datetime:~0,8%_%datetime:~8,6%.dump

echo Creating backup: %BACKUP_FILE%
echo.

REM Read database connection info from application.properties
for /f "tokens=2 delims==" %%a in ('findstr "spring.datasource.url" src\main\resources\application.properties') do set DB_URL=%%a
for /f "tokens=2 delims==" %%a in ('findstr "spring.datasource.username" src\main\resources\application.properties') do set DB_USER=%%a

REM Extract database name from JDBC URL
for /f "tokens=5 delims=/" %%a in ("%DB_URL%") do set DB_NAME=%%a

echo Database: %DB_NAME%
echo User: %DB_USER%
echo.

REM Execute pg_dump
pg_dump -U %DB_USER% -d %DB_NAME% -F c -b -v -f "%BACKUP_FILE%"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Backup completed successfully!
    echo File: %BACKUP_FILE%
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ERROR: Backup failed!
    echo ========================================
    exit /b 1
)

pause
