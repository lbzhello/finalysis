# pandas 测试
#%%
import pandas as pd
# %%
ind = pd.read_csv('ind.csv')
# %% 前 10 行
ind.head(10)
# %% 多少列
ind.columns
# %%
ind.index
# %%
