import psycopg2
import os

# Database connection details
DB_HOST = "dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com"
DB_PORT = "5432"
DB_NAME = "boarddb_0u9z"
DB_USER = "boarduser"
DB_PASSWORD = "RgdjzmPYsWj5GgxHs3feHNCDOQqbZ4aV"

print("Connecting to Render PostgreSQL...")
print(f"Host: {DB_HOST}")
print(f"Database: {DB_NAME}")
print()

try:
    # Connect to PostgreSQL
    conn = psycopg2.connect(
        host=DB_HOST,
        port=DB_PORT,
        database=DB_NAME,
        user=DB_USER,
        password=DB_PASSWORD,
        sslmode='require',
        connect_timeout=30
    )

    conn.autocommit = False
    cursor = conn.cursor()

    print("[OK] Connected successfully!")
    print()

    # Read the combined SQL file
    sql_file = "mock-data/insert_all_combined.sql"
    print(f"Reading SQL file: {sql_file}")

    with open(sql_file, 'r', encoding='utf-8') as f:
        sql_content = f.read()

    print(f"[OK] Read {len(sql_content)} characters")
    print()

    # Execute the SQL
    print("Executing SQL script...")
    cursor.execute(sql_content)

    # Commit the transaction
    conn.commit()
    print("[OK] SQL executed successfully!")
    print()

    # Verify data
    print("Verifying data insertion:")
    print("-" * 50)

    cursor.execute("SELECT COUNT(*) FROM users")
    users_count = cursor.fetchone()[0]
    print(f"Users: {users_count}")

    cursor.execute("SELECT COUNT(*) FROM category")
    categories_count = cursor.fetchone()[0]
    print(f"Categories: {categories_count}")

    cursor.execute("SELECT COUNT(*) FROM board")
    boards_count = cursor.fetchone()[0]
    print(f"Boards: {boards_count}")

    cursor.execute("SELECT COUNT(*) FROM comment")
    comments_count = cursor.fetchone()[0]
    print(f"Comments: {comments_count}")

    cursor.execute("SELECT COUNT(*) FROM likes")
    likes_count = cursor.fetchone()[0]
    print(f"Likes: {likes_count}")

    cursor.execute("SELECT COUNT(*) FROM follow")
    follows_count = cursor.fetchone()[0]
    print(f"Follows: {follows_count}")

    cursor.execute("SELECT COUNT(*) FROM bookmark")
    bookmarks_count = cursor.fetchone()[0]
    print(f"Bookmarks: {bookmarks_count}")

    print("-" * 50)
    print()
    print("[OK] All mock data inserted successfully!")
    print()
    print("You can now visit: https://spring-board-app.onrender.com/board")

    cursor.close()
    conn.close()

except psycopg2.OperationalError as e:
    print(f"[ERROR] Connection error: {e}")
    print()
    print("Please check:")
    print("1. Network connection")
    print("2. Database credentials")
    print("3. Firewall settings")

except Exception as e:
    print(f"[ERROR] Error: {e}")
    if 'conn' in locals():
        conn.rollback()
        conn.close()
