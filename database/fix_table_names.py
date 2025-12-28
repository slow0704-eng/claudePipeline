import re

input_file = 'mock-data/insert_all_combined.sql'
output_file = 'mock-data/insert_all_fixed.sql'

with open(input_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace table names with plural forms
replacements = {
    r'\bcategory\b': 'categories',
    r'\bcomment\b': 'comments',
    r'\bfollow\b': 'follows',
    r'\bbookmark\b': 'bookmarks',
    r'\battachment\b': 'attachments'
}

for pattern, replacement in replacements.items():
    content = re.sub(pattern, replacement, content)

# Fix sequence names too
content = content.replace('category_id_seq', 'categories_id_seq')
content = content.replace('comment_id_seq', 'comments_id_seq')

with open(output_file, 'w', encoding='utf-8') as f:
    f.write(content)

print(f"Fixed SQL written to {output_file}")
print("Table names updated to plural form:")
print("  - category -> categories")
print("  - comment -> comments")
print("  - follow -> follows")
print("  - bookmark -> bookmarks")
print("  - attachment -> attachments")
