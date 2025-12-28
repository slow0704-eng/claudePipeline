import psycopg2

DB_HOST = "dpg-d57mkije5dus73depkf0-a.oregon-postgres.render.com"
DB_PORT = "5432"
DB_NAME = "boarddb_0u9z"
DB_USER = "boarduser"
DB_PASSWORD = "RgdjzmPYsWj5GgxHs3feHNCDOQqbZ4aV"

print("Testing connection to Render PostgreSQL...")
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

    cursor = conn.cursor()

    print("[OK] Connection successful!")
    print()

    # Check PostgreSQL version
    cursor.execute("SELECT version();")
    version = cursor.fetchone()[0]
    print(f"PostgreSQL version: {version}")
    print()

    # List all tables
    cursor.execute("""
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema = 'public'
        ORDER BY table_name;
    """)
    tables = cursor.fetchall()

    print(f"Tables in database ({len(tables)} total):")
    for table in tables:
        print(f"  - {table[0]}")
    print()

    # Check data counts
    if tables:
        print("Data counts:")
        for table in tables:
            table_name = table[0]
            cursor.execute(f"SELECT COUNT(*) FROM {table_name}")
            count = cursor.fetchone()[0]
            print(f"  {table_name}: {count} rows")

    cursor.close()
    conn.close()

except Exception as e:
    print(f"[ERROR] {e}")
    import traceback
    traceback.print_exc()
