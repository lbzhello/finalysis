# To add a new cell, type '# %%'
# To add a new markdown cell, type '# %% [markdown]'
# %%
import psycopg2 as pg
import pandas as pd
from datetime import datetime
from pystock.database.model.kline import KLine

# %%
__host = '127.0.0.1'
__port = '5432'
__database = 'stock'
__user = 'postgres'
__password = '191908577'


# %%
# 连接到一个给定的数据库
conn = pg.connect(host=__host, port=__port, database=__database, user=__user, password=__password)
# 建立游标，用来执行数据库操作
cursor = conn.cursor()


# %%
"""用于测试"""
if __name__ == '__main__':
    print('stock_db runing as main')


# %%
def close():
    if cursor is not None:
        cursor.close()
    if conn is not None:
        conn.close()


# %%
def insert(kline: KLine):
    # SQLAlchemy
    sql = '''insert into k_line (stock_code) values ('{}')'''.format(kline.stock_code)
    # 执行SQL
    cursor.execute(sql)
    # 提交才会生效
    conn.commit()

    

# %%
k = KLine(stock_code='600602')
insert(k)

# %%
# 执行SQL SELECT命令
cursor.execute("select * from k_line")
# 获取SELECT返回的元组
rows = cursor.fetchall()
rows

# %%
cursor.execute("delete from k_line where stock_code = 1")


# %%
cursor.fetchall()


# %%
# 关闭游标
cursor.close()

# 关闭数据库连接
conn.close()


# %%



