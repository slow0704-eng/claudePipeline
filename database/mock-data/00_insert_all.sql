-- Master script to insert all mock data in correct order
-- Execute this file to populate the database with sample data

-- Disable foreign key checks temporarily for clean insertion
SET session_replication_role = 'replica';

-- Clear existing data (optional - uncomment if needed)
-- TRUNCATE TABLE bookmark, follow, likes, comment, attachment, board, category, users RESTART IDENTITY CASCADE;

-- Insert data in dependency order
\i 01_mock_users.sql
\i 02_mock_categories.sql
\i 03_mock_boards.sql
\i 04_mock_comments.sql
\i 05_mock_interactions.sql

-- Re-enable foreign key checks
SET session_replication_role = 'origin';

-- Verify data insertion
SELECT 'Users: ' || COUNT(*) FROM users;
SELECT 'Categories: ' || COUNT(*) FROM category;
SELECT 'Boards: ' || COUNT(*) FROM board;
SELECT 'Comments: ' || COUNT(*) FROM comment;
SELECT 'Likes: ' || COUNT(*) FROM likes;
SELECT 'Follows: ' || COUNT(*) FROM follow;
SELECT 'Bookmarks: ' || COUNT(*) FROM bookmark;
