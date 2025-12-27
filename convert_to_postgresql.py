#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
MySQL dump 파일을 PostgreSQL 호환 형식으로 변환하는 스크립트
"""

import re
import sys
import os

def convert_mysql_to_postgresql(input_file, output_file):
    """MySQL dump를 PostgreSQL 형식으로 변환"""

    print(f"입력 파일: {input_file}")
    print(f"출력 파일: {output_file}")
    print("변환 중...")

    try:
        with open(input_file, 'r', encoding='utf-8') as f:
            content = f.read()
    except FileNotFoundError:
        print(f"✗ 오류: {input_file} 파일을 찾을 수 없습니다.")
        return False

    # MySQL 특수 구문 제거
    content = re.sub(r'/\*!40\d{3}.*?\*/;?', '', content)
    content = re.sub(r'--.*?\n', '\n', content)

    # AUTO_INCREMENT를 SERIAL로 변환
    content = re.sub(r'`(\w+)` bigint\(20\) NOT NULL AUTO_INCREMENT', r'"\1" BIGSERIAL PRIMARY KEY', content)
    content = re.sub(r'`(\w+)` int\(11\) NOT NULL AUTO_INCREMENT', r'"\1" SERIAL PRIMARY KEY', content)
    content = re.sub(r'AUTO_INCREMENT=\d+', '', content)

    # 백틱(`)을 큰따옴표(")로 변경
    content = content.replace('`', '"')

    # MySQL 데이터 타입을 PostgreSQL로 변환
    content = re.sub(r'tinyint\(1\)', 'boolean', content)
    content = re.sub(r'tinyint\(\d+\)', 'smallint', content)
    content = re.sub(r'int\(\d+\)', 'integer', content)
    content = re.sub(r'bigint\(\d+\)', 'bigint', content)
    content = re.sub(r'double', 'double precision', content)
    content = re.sub(r'datetime', 'timestamp', content)
    content = re.sub(r'longtext', 'text', content)
    content = re.sub(r'mediumtext', 'text', content)
    content = re.sub(r'varchar\((\d+)\)', r'varchar(\1)', content)

    # ENGINE, CHARSET 등 MySQL 전용 옵션 제거
    content = re.sub(r'ENGINE=\w+', '', content)
    content = re.sub(r'DEFAULT CHARSET=\w+', '', content)
    content = re.sub(r'COLLATE=\w+', '', content)
    content = re.sub(r'CHARACTER SET \w+', '', content)

    # KEY, INDEX 제거 (PostgreSQL은 별도로 처리)
    content = re.sub(r',\s*KEY ".*?".*?\n', '', content)
    content = re.sub(r',\s*UNIQUE KEY ".*?".*?\n', '', content)
    content = re.sub(r',\s*INDEX ".*?".*?\n', '', content)

    # PRIMARY KEY 정의 제거 (이미 SERIAL로 변환됨)
    content = re.sub(r',\s*PRIMARY KEY \(".*?"\)', '', content)

    # LOCK TABLES, UNLOCK TABLES 제거
    content = re.sub(r'LOCK TABLES.*?;', '', content)
    content = re.sub(r'UNLOCK TABLES;', '', content)

    # INSERT 문에서 0000-00-00 날짜를 NULL로 변환
    content = re.sub(r"'0000-00-00 00:00:00'", 'NULL', content)
    content = re.sub(r"'0000-00-00'", 'NULL', content)

    # SET 명령어 제거
    content = re.sub(r'SET .*?;', '', content)

    # 빈 줄 정리
    content = re.sub(r'\n\s*\n\s*\n', '\n\n', content)

    # 출력 파일에 쓰기
    try:
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write(content)
        print("✓ 변환 완료!")
        print(f"파일 위치: {os.path.abspath(output_file)}")
        return True
    except Exception as e:
        print(f"✗ 오류: 파일 쓰기 실패 - {e}")
        return False

if __name__ == "__main__":
    input_file = "boarddb_export.sql"
    output_file = "boarddb_postgresql.sql"

    if len(sys.argv) > 1:
        input_file = sys.argv[1]
    if len(sys.argv) > 2:
        output_file = sys.argv[2]

    success = convert_mysql_to_postgresql(input_file, output_file)
    sys.exit(0 if success else 1)
