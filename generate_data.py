import csv
from faker import Faker
from tqdm import tqdm
import os

# --- 配置 ---
NUM_ROWS = 1_000_000  # 要生成的总行数
OUTPUT_FILE = 'users_1M.csv'  # 输出文件名

# --- 脚本开始 ---
print(f"准备生成包含 {NUM_ROWS:,} 条记录的CSV文件...")

# 初始化 Faker，用于生成模拟数据
fake = Faker('en_US') # 使用英文数据，也可以用 'zh_CN'

# 获取当前脚本所在目录
script_dir = os.path.dirname(os.path.abspath(__file__))
file_path = os.path.join(script_dir, OUTPUT_FILE)

# 使用 with 语句确保文件被正确关闭
# newline='' 是写入CSV文件的标准做法，防止出现空行
with open(file_path, mode='w', newline='', encoding='utf-8') as csv_file:
    # 创建CSV写入器
    csv_writer = csv.writer(csv_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)

    # 1. 写入表头
    csv_writer.writerow(['name', 'email'])

    # 2. 循环生成并写入数据行
    # tqdm 是一个漂亮的进度条库，让等待过程不那么枯燥
    for _ in tqdm(range(NUM_ROWS), desc="正在生成数据", unit=" 条记录"):
        name = fake.name()
        email = fake.email()
        csv_writer.writerow([name, email])

print("\n🎉 文件生成成功！")
print(f"文件位置: {file_path}")
print(f"总行数: {NUM_ROWS + 1} (包含表头)")