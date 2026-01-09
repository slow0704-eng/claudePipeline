@echo off
chcp 65001 >nul

REM 배치 파일이 있는 디렉토리로 이동
cd /d "%~dp0"

echo ==========================================
echo 목 데이터 삽입 (초간단 버전)
echo ==========================================
echo.
echo 이 스크립트는:
echo 1. 기존 데이터 삭제 (admin 제외)
echo 2. user001~user050 생성
echo 3. 게시글 300+개 생성
echo 4. 좋아요, 북마크, 댓글 등 생성
echo.
pause

set PGPASSWORD=1

echo.
echo [1/7] 사용자 데이터...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -f "mock-data/01_users.sql" 2>&1 | findstr /C:"INSERT" /C:"DELETE"
if %ERRORLEVEL% EQU 0 (echo ✓ 완료) else (echo ✗ 확인 필요)

echo [2/7] 게시글 데이터...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -f "mock-data/02_boards.sql" 2>&1 | findstr /C:"INSERT"
if %ERRORLEVEL% EQU 0 (echo ✓ 완료) else (echo ✗ 확인 필요)

echo [3/7] 팔로우 데이터...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -f "mock-data/03_follows.sql" 2>&1 | findstr /C:"INSERT"
if %ERRORLEVEL% EQU 0 (echo ✓ 완료) else (echo ✗ 확인 필요)

echo [4/7] 좋아요 데이터...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -f "mock-data/04_likes.sql" 2>&1 | findstr /C:"INSERT"
if %ERRORLEVEL% EQU 0 (echo ✓ 완료) else (echo ✗ 확인 필요)

echo [5/7] 북마크 데이터...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -f "mock-data/05_bookmarks.sql" 2>&1 | findstr /C:"INSERT"
if %ERRORLEVEL% EQU 0 (echo ✓ 완료) else (echo ✗ 확인 필요)

echo [6/7] 댓글 데이터...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -f "mock-data/06_comments.sql" 2>&1 | findstr /C:"INSERT"
if %ERRORLEVEL% EQU 0 (echo ✓ 완료) else (echo ✗ 확인 필요)

echo [7/7] 해시태그 데이터...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U postgres -d boarddb -f "mock-data/07_hashtags.sql" 2>&1 | findstr /C:"INSERT"
if %ERRORLEVEL% EQU 0 (echo ✓ 완료) else (echo ✗ 확인 필요)

echo.
echo ==========================================
echo ✓ 완료!
echo ==========================================
echo.
echo 브라우저에서 확인:
echo   http://localhost:8080/debug/db-info
echo.
echo 게시판 확인:
echo   http://localhost:8080/board
echo.
echo 로그인:
echo   - admin / admin1234 (기존 계정)
echo   - user001 / 1234 (목 데이터)
echo.

set PGPASSWORD=
pause
