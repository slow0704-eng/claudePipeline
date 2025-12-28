import psycopg2

DB_HOST = "dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com"
DB_PORT = "5432"
DB_NAME = "boarddb_0u9z"
DB_USER = "boarduser"
DB_PASSWORD = "RgdjzmPYsWj5GgxHs3feHNCDOQqbZ4aV"

print("Clearing ALL data from Render PostgreSQL...")
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

    # Delete all data from all tables in correct order
    tables = ['bookmarks', 'follows', 'likes', 'comments', 'attachments', 'board', 'categories', 'users']

    print("Deleting data from tables...")
    for table in tables:
        cursor.execute(f"DELETE FROM {table}")
        print(f"  - Cleared {table}")

    # Reset all sequences
    print()
    print("Resetting sequences...")
    sequences = ['users_id_seq', 'categories_id_seq', 'board_id_seq', 'comments_id_seq']
    for seq in sequences:
        cursor.execute(f"SELECT setval('{seq}', 1, false)")
        print(f"  - Reset {seq}")

    conn.commit()
    print()
    print("[OK] All data cleared!")

    cursor.close()
    conn.close()

except Exception as e:
    print(f"[ERROR] {e}")
    import traceback
    traceback.print_exc()
    if 'conn' in locals():
        conn.rollback()
        conn.close()
