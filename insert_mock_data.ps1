# PowerShell script to insert mock data
# UTF-8 인코딩 설정
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "로컬 PostgreSQL에 목 데이터 삽입" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# 로컬 DB 설정
$env:PGPASSWORD = "1"
$PSQL = "C:\Program Files\PostgreSQL\16\bin\psql.exe"
$HOST = "localhost"
$PORT = "5432"
$DB = "boarddb"
$USER = "postgres"

Write-Host "로컬 DB 정보:"
Write-Host "Host: $HOST"
Write-Host "Port: $PORT"
Write-Host "Database: $DB"
Write-Host "User: $USER"
Write-Host ""

Write-Host "주의: 기존 데이터와 중복될 수 있습니다!" -ForegroundColor Yellow
Write-Host "깨끗한 DB에서 시작하려면 README.md의 'DB 초기화' 섹션을 참고하세요." -ForegroundColor Yellow
Write-Host ""
Read-Host "계속하려면 Enter를 누르세요"

$sqlFiles = @(
    @{Name="사용자 데이터"; File="mock-data/01_users.sql"; Count="50명"},
    @{Name="게시글 데이터"; File="mock-data/02_boards.sql"; Count="300+개"},
    @{Name="팔로우 관계"; File="mock-data/03_follows.sql"; Count="200개"},
    @{Name="좋아요 데이터"; File="mock-data/04_likes.sql"; Count="300+개"},
    @{Name="북마크 데이터"; File="mock-data/05_bookmarks.sql"; Count="100개"},
    @{Name="댓글 데이터"; File="mock-data/06_comments.sql"; Count="60개"},
    @{Name="해시태그 데이터"; File="mock-data/07_hashtags.sql"; Count="78개 + 90+개 연결"}
)

$step = 1
$success = $true

foreach ($sqlFile in $sqlFiles) {
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host "$step단계: $($sqlFile.Name) 삽입 ($($sqlFile.Count))" -ForegroundColor Cyan
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host ""

    & $PSQL -h $HOST -p $PORT -U $USER -d $DB -f $sqlFile.File

    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ $($sqlFile.Name) 삽입 완료" -ForegroundColor Green
    } else {
        Write-Host "✗ $($sqlFile.Name) 삽입 실패" -ForegroundColor Red
        $success = $false
        break
    }

    $step++
}

Write-Host ""
if ($success) {
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "✓ 모든 목 데이터 삽입 완료!" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "삽입된 데이터 요약:"
    Write-Host "- 사용자: 50명 (admin 포함 51명)"
    Write-Host "- 게시글: 300+개 (임시저장 5개 포함)"
    Write-Host "- 팔로우: 200개"
    Write-Host "- 좋아요: 300+개"
    Write-Host "- 북마크: 100개"
    Write-Host "- 댓글: 60개 (답글 포함)"
    Write-Host "- 해시태그: 78개 (90+개 연결)"
    Write-Host ""
    Write-Host "테스트 계정 정보:"
    Write-Host "- 관리자: admin / 1234"
    Write-Host "- 일반 사용자: user001~user050 / 1234"
    Write-Host ""
    Write-Host "애플리케이션을 시작하세요: mvnw.cmd spring-boot:run" -ForegroundColor Yellow
} else {
    Write-Host "==========================================" -ForegroundColor Red
    Write-Host "✗ 오류 발생!" -ForegroundColor Red
    Write-Host "==========================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "일부 데이터 삽입 중 오류가 발생했습니다." -ForegroundColor Red
    Write-Host "자세한 내용은 mock-data/README.md의 '문제 해결' 섹션을 참고하세요."
}

# 환경변수 초기화
$env:PGPASSWORD = ""

Write-Host ""
Read-Host "종료하려면 Enter를 누르세요"
