@echo off
chcp 65001 > nul
set PGPASSWORD=RgdjzmPYsWj5GgxHs3feHNCDOQqbZ4aV
set PGCLIENTENCODING=UTF8

echo Connecting to Render PostgreSQL...
echo.

"C:\Program Files\PostgreSQL\16\bin\psql.exe" ^
  -h dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com ^
  -p 5432 ^
  -U boarduser ^
  -d boarddb_0u9z ^
  -c "SET session_replication_role = 'replica';"

echo.
echo Inserting users...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" ^
  -h dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com ^
  -p 5432 ^
  -U boarduser ^
  -d boarddb_0u9z ^
  -f mock-data/01_mock_users.sql

echo.
echo Inserting categories...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" ^
  -h dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com ^
  -p 5432 ^
  -U boarduser ^
  -d boarddb_0u9z ^
  -f mock-data/02_mock_categories.sql

echo.
echo Inserting boards...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" ^
  -h dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com ^
  -p 5432 ^
  -U boarduser ^
  -d boarddb_0u9z ^
  -f mock-data/03_mock_boards.sql

echo.
echo Inserting comments...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" ^
  -h dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com ^
  -p 5432 ^
  -U boarduser ^
  -d boarddb_0u9z ^
  -f mock-data/04_mock_comments.sql

echo.
echo Inserting interactions...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" ^
  -h dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com ^
  -p 5432 ^
  -U boarduser ^
  -d boarddb_0u9z ^
  -f mock-data/05_mock_interactions.sql

echo.
echo Restoring foreign key checks...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" ^
  -h dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com ^
  -p 5432 ^
  -U boarduser ^
  -d boarddb_0u9z ^
  -c "SET session_replication_role = 'origin';"

echo.
echo Verifying data...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" ^
  -h dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com ^
  -p 5432 ^
  -U boarduser ^
  -d boarddb_0u9z ^
  -c "SELECT 'Users: ' || COUNT(*) FROM users; SELECT 'Categories: ' || COUNT(*) FROM category; SELECT 'Boards: ' || COUNT(*) FROM board; SELECT 'Comments: ' || COUNT(*) FROM comment; SELECT 'Likes: ' || COUNT(*) FROM likes; SELECT 'Follows: ' || COUNT(*) FROM follow; SELECT 'Bookmarks: ' || COUNT(*) FROM bookmark;"

echo.
echo Done!
pause
