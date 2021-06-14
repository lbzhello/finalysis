# 获取股票行业分类和概念分类
#%%
import akshare as ak

#%% 获取行业板块
ind = ak.stock_board_industry_name_ths()
# %%
ind.head(10)
# %%
