$env:PGPASSWORD = "1"

Write-Host "Dropping existing boarddb database if exists..."
& "C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -h localhost -c "DROP DATABASE IF EXISTS boarddb;"

Write-Host "Creating boarddb database..."
& "C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -h localhost -c "CREATE DATABASE boarddb WITH ENCODING='UTF8';"

Write-Host "Database boarddb created successfully!"
Write-Host ""
Write-Host "Verifying database creation..."
& "C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -h localhost -l -t | Select-String "boarddb"
