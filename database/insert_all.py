import psycopg2

DB_HOST = "dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com"
DB_PORT = "5432"
DB_NAME = "boarddb_0u9z"
DB_USER = "boarduser"
DB_PASSWORD = "RgdjzmPYsWj5GgxHs3feHNCDOQqbZ4aV"

print("Inserting mock data to Render PostgreSQL...")
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

    print("[OK] Connected!")
    print()

    # Read SQL file
    sql_file = "mock-data/insert_all_fixed.sql"
    print(f"Reading {sql_file}...")
    with open(sql_file, 'r', encoding='utf-8') as f:
        sql_content = f.read()

    print(f"[OK] Read {len(sql_content)} characters")
    print()

    # Execute SQL
    print("Executing SQL script...")
    cursor.execute(sql_content)

    conn.commit()
    print("[OK] SQL executed successfully!")
    print()

    # Verify
    print("Verifying data insertion:")
    print("-" * 50)

    cursor.execute("SELECT COUNT(*) FROM users")
    print(f"Users: {cursor.fetchone()[0]}")

    cursor.execute("SELECT COUNT(*) FROM categories")
    print(f"Categories: {cursor.fetchone()[0]}")

    cursor.execute("SELECT COUNT(*) FROM board")
    print(f"Boards: {cursor.fetchone()[0]}")

    cursor.execute("SELECT COUNT(*) FROM comments")
    print(f"Comments: {cursor.fetchone()[0]}")

    cursor.execute("SELECT COUNT(*) FROM likes")
    print(f"Likes: {cursor.fetchone()[0]}")

    cursor.execute("SELECT COUNT(*) FROM follows")
    print(f"Follows: {cursor.fetchone()[0]}")

    cursor.execute("SELECT COUNT(*) FROM bookmarks")
    print(f"Bookmarks: {cursor.fetchone()[0]}")

    print("-" * 50)
    print()
    print("[OK] All mock data inserted successfully!")
    print()
    print("Visit: https://spring-board-app.onrender.com/board")
    print()
    print("Login credentials (password: test1234):")
    print("  - kimcoder")
    print("  - leespring")
    print("  - admin")

    cursor.close()
    conn.close()

except Exception as e:
    print(f"[ERROR] {e}")
    import traceback
    traceback.print_exc()
    if 'conn' in locals():
        conn.rollback()
        conn.close()
