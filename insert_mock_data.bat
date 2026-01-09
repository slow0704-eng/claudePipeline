@echo off
chcp 65001 >nul

REM 배치 파일이 있는 디렉토리로 이동
cd /d "%~dp0"

echo ==========================================
echo 로컬 PostgreSQL에 목 데이터 삽입
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

echo ==========================================
echo 기존 데이터 삭제 여부 선택
echo ==========================================
echo.
echo 기존 데이터가 있으면 중복 오류가 발생할 수 있습니다.
echo.
echo 1. 기존 데이터 삭제 후 삽입 (권장)
echo 2. 기존 데이터 유지하고 삽입
echo.
set /p CLEAR_CHOICE="선택하세요 (1 또는 2): "

if "%CLEAR_CHOICE%"=="1" (
    echo.
    echo 기존 데이터를 삭제합니다...
    echo.

    REM PostgreSQL 비밀번호 설정
    set PGPASSWORD=%LOCAL_PASSWORD%

    "C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "clear_mock_data.sql"

    if %ERRORLEVEL% EQU 0 (
        echo ✓ 기존 데이터 삭제 완료
        echo.
    ) else (
        echo ✗ 기존 데이터 삭제 실패
        echo.
        pause
        goto :end
    )
)

echo.
echo 목 데이터 삽입을 시작합니다...
echo.
pause

echo.
echo ==========================================
echo 1단계: 사용자 데이터 삽입 (50명)
echo ==========================================
echo.

REM PostgreSQL 비밀번호 설정
set PGPASSWORD=%LOCAL_PASSWORD%

"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/01_users.sql"

if %ERRORLEVEL% NEQ 0 (
    echo ✗ 사용자 데이터 삽입 실패
    goto :error
)
echo ✓ 사용자 데이터 삽입 완료
echo.

echo ==========================================
echo 2단계: 게시글 데이터 삽입 (300+개)
echo ==========================================
echo.

"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/02_boards.sql"

if %ERRORLEVEL% NEQ 0 (
    echo ✗ 게시글 데이터 삽입 실패
    goto :error
)
echo ✓ 게시글 데이터 삽입 완료
echo.

echo ==========================================
echo 3단계: 팔로우 관계 삽입 (200개)
echo ==========================================
echo.

"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/03_follows.sql"

if %ERRORLEVEL% NEQ 0 (
    echo ✗ 팔로우 데이터 삽입 실패
    goto :error
)
echo ✓ 팔로우 데이터 삽입 완료
echo.

echo ==========================================
echo 4단계: 좋아요 데이터 삽입 (300+개)
echo ==========================================
echo.

"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/04_likes.sql"

if %ERRORLEVEL% NEQ 0 (
    echo ✗ 좋아요 데이터 삽입 실패
    goto :error
)
echo ✓ 좋아요 데이터 삽입 완료
echo.

echo ==========================================
echo 5단계: 북마크 데이터 삽입 (100개)
echo ==========================================
echo.

"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/05_bookmarks.sql"

if %ERRORLEVEL% NEQ 0 (
    echo ✗ 북마크 데이터 삽입 실패
    goto :error
)
echo ✓ 북마크 데이터 삽입 완료
echo.

echo ==========================================
echo 6단계: 댓글 데이터 삽입 (60개)
echo ==========================================
echo.

"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/06_comments.sql"

if %ERRORLEVEL% NEQ 0 (
    echo ✗ 댓글 데이터 삽입 실패
    goto :error
)
echo ✓ 댓글 데이터 삽입 완료
echo.

echo ==========================================
echo 7단계: 해시태그 데이터 삽입 (78개 + 90+개 연결)
echo ==========================================
echo.

"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h %LOCAL_HOST% -p %LOCAL_PORT% -U %LOCAL_USER% -d %LOCAL_DB% -f "mock-data/07_hashtags.sql"

if %ERRORLEVEL% NEQ 0 (
    echo ✗ 해시태그 데이터 삽입 실패
    goto :error
)
echo ✓ 해시태그 데이터 삽입 완료
echo.

echo ==========================================
echo ✓ 모든 목 데이터 삽입 완료!
echo ==========================================
echo.
echo 삽입된 데이터 요약:
echo - 사용자: 50명 (admin 포함 51명)
echo - 게시글: 300+개 (임시저장 5개 포함)
echo - 팔로우: 200개
echo - 좋아요: 300+개
echo - 북마크: 100개
echo - 댓글: 60개 (답글 포함)
echo - 해시태그: 78개 (90+개 연결)
echo.
echo 테스트 계정 정보:
echo - 관리자: admin / 1234
echo - 일반 사용자: user001~user050 / 1234
echo.
echo 애플리케이션을 시작하세요: mvnw.cmd spring-boot:run
echo.
goto :end

:error
echo.
echo ==========================================
echo ✗ 오류 발생!
echo ==========================================
echo.
echo 일부 데이터 삽입 중 오류가 발생했습니다.
echo.
echo 가능한 원인:
echo 1. PostgreSQL이 실행 중이지 않음
echo 2. 기존 데이터와 충돌 (중복 키 오류)
echo 3. 데이터베이스 연결 오류
echo.
echo 해결 방법:
echo.
echo [방법 1] 기존 데이터 삭제 후 재시도 (권장)
echo   1. clear_mock_data.bat 실행
echo   2. insert_mock_data.bat 다시 실행
echo.
echo [방법 2] DB 완전 초기화
echo   1. 애플리케이션 종료
echo   2. application.properties에서 spring.jpa.hibernate.ddl-auto=create
echo   3. 애플리케이션 재시작 (테이블 재생성)
echo   4. 애플리케이션 종료
echo   5. spring.jpa.hibernate.ddl-auto=update로 변경
echo   6. insert_mock_data.bat 실행 (옵션 1 선택)
echo.

:end
REM 환경변수 초기화
set PGPASSWORD=

echo.
pause
