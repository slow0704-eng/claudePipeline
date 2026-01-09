@echo off
chcp 65001 >nul

REM 배치 파일이 있는 디렉토리로 이동
cd /d "%~dp0"

echo ==========================================
echo 기존 목 데이터 삭제
echo ==========================================
echo.

REM 로컬 DB 설정
set LOCAL_HOST=localhost
set LOCAL_PORT=5432
set LOCAL_DB=boarddb
set LOCAL_USER=postgres
set LOCAL_PASSWORD=1

echo 로컬 DB 정보:
echo Host: %LOCAL_HOST%
echo Port: %LOCAL_PORT%
echo Database: %LOCAL_DB%
echo User: %LOCAL_USER%
echo.

echo ⚠️  경고: 다음 데이터가 모두 삭제됩니다!
echo.
echo - 게시글-해시태그 연결
echo - 해시태그
echo - 댓글
echo - 북마크
echo - 게시글 좋아요
echo - 팔로우 관계
echo - 첨부파일
echo - 게시글
echo - 사용자 (user001~user050)
echo.
echo ⚠️  admin 계정은 유지됩니다.
echo ⚠️  이 작업은 되돌릴 수 없습니다!
echo.
pause

echo.
echo 데이터 삭제 중...
echo.

REM PostgreSQL 비밀번호 설정
set PGPASSWORD=%LOCAL_PASSWORD%

"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "clear_mock_data.sql"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ==========================================
    echo ✓ 기존 데이터 삭제 완료!
    echo ==========================================
    echo.
    echo 이제 목 데이터를 삽입할 수 있습니다:
    echo   insert_mock_data.bat
    echo.
) else (
    echo.
    echo ==========================================
    echo ✗ 데이터 삭제 중 오류 발생
    echo ==========================================
    echo.
    echo PostgreSQL이 실행 중인지 확인하세요.
    echo.
)

REM 환경변수 초기화
set PGPASSWORD=

echo.
pause
