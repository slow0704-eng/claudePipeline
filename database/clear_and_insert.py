import psycopg2

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

    # Check existing users
    print("Checking existing data...")
    cursor.execute("SELECT id, username, email FROM users")
    existing_users = cursor.fetchall()

    if existing_users:
        print(f"Found {len(existing_users)} existing user(s):")
        for user in existing_users:
            print(f"  ID={user[0]}, username={user[1]}, email={user[2]}")
        print()
        print("Clearing existing data to insert mock data...")

        # Helper function to check if table exists
        def table_exists(table_name):
            cursor.execute("""
                SELECT EXISTS (
                    SELECT FROM information_schema.tables
                    WHERE table_schema = 'public'
                    AND table_name = %s
                )
            """, (table_name,))
            return cursor.fetchone()[0]

        # Delete in reverse order of dependencies (only if table exists)
        tables_to_clear = ['bookmarks', 'follows', 'likes', 'comments', 'attachments', 'board', 'categories', 'users']
        for table in tables_to_clear:
            if table_exists(table):
                cursor.execute(f"DELETE FROM {table}")
                print(f"  - Cleared {table}")

        # Reset sequences (only if they exist)
        sequences = [
            ('users_id_seq', 'users'),
            ('categories_id_seq', 'categories'),
            ('board_id_seq', 'board'),
            ('comments_id_seq', 'comments')
        ]
        for seq_name, table_name in sequences:
            if table_exists(table_name):
                cursor.execute(f"SELECT setval('{seq_name}', 1, false)")
                print(f"  - Reset {seq_name}")

        conn.commit()
        print("[OK] Existing data cleared!")
        print()
    else:
        print("No existing data found.")
        print()

    # Read and execute SQL file
    sql_file = "mock-data/insert_all_fixed.sql"
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

    cursor.execute("SELECT COUNT(*) FROM categories")
    categories_count = cursor.fetchone()[0]
    print(f"Categories: {categories_count}")

    cursor.execute("SELECT COUNT(*) FROM board")
    boards_count = cursor.fetchone()[0]
    print(f"Boards: {boards_count}")

    cursor.execute("SELECT COUNT(*) FROM comments")
    comments_count = cursor.fetchone()[0]
    print(f"Comments: {comments_count}")

    cursor.execute("SELECT COUNT(*) FROM likes")
    likes_count = cursor.fetchone()[0]
    print(f"Likes: {likes_count}")

    cursor.execute("SELECT COUNT(*) FROM follows")
    follows_count = cursor.fetchone()[0]
    print(f"Follows: {follows_count}")

    cursor.execute("SELECT COUNT(*) FROM bookmarks")
    bookmarks_count = cursor.fetchone()[0]
    print(f"Bookmarks: {bookmarks_count}")

    print("-" * 50)
    print()
    print("[OK] All mock data inserted successfully!")
    print()
    print("You can now visit: https://spring-board-app.onrender.com/board")
    print()
    print("Login credentials (all users have password: test1234):")
    print("  - kimcoder / test1234")
    print("  - leespring / test1234")
    print("  - admin / test1234")

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
    import traceback
    traceback.print_exc()
    if 'conn' in locals():
        conn.rollback()
        conn.close()
