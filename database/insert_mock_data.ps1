$env:PGPASSWORD = 'RgdjzmPYsWj5GgxHs3feHNCDOQqbZ4aV'

Write-Host "Connecting to Render PostgreSQL..." -ForegroundColor Yellow

Get-Content 'mock-data/all_mock_data.sql' | & 'C:\Program Files\PostgreSQL\16\bin\psql.exe' `
    -h dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com `
    -p 5432 `
    -U boarduser `
    -d boarddb_0u9z

if ($LASTEXITCODE -eq 0) {
    Write-Host "Mock data inserted successfully!" -ForegroundColor Green
} else {
    Write-Host "Error inserting data. Exit code: $LASTEXITCODE" -ForegroundColor Red
}
