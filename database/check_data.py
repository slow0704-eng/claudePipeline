import psycopg2

# Database connection details
DB_HOST = "dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com"
DB_PORT = "5432"
DB_NAME = "boarddb_0u9z"
DB_USER = "boarduser"
DB_PASSWORD = "RgdjzmPYsWj5GgxHs3feHNCDOQqbZ4aV"

print("Checking existing data in database...")
print()

try:
    conn = psycopg2.connect(
        host=DB_HOST,
        port=DB_PORT,
        database=DB_NAME,
        user=DB_USER,
        password=DB_PASSWORD,
        sslmode='require',
        connect_timeout=30
    )

    cursor = conn.cursor()

    print("Current data counts:")
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

    # Check if mock data already exists
    cursor.execute("SELECT username FROM users LIMIT 5")
    users = cursor.fetchall()
    if users:
        print("Sample users:")
        for user in users:
            print(f"  - {user[0]}")
        print()

    cursor.close()
    conn.close()

except Exception as e:
    print(f"[ERROR] Error: {e}")
