#%%
from datetime import datetime, timezone, timedelta
import time

## 中国时间
PRC_DATE_FMT = '%Y-%m-%d 00:00:00+08:00'

DATE_FMT = '%Y-%m-%d'

#%%
def now():
    return(datetime.now())
#%%
def now_str() -> str:
    return(time.strftime(DATE_FMT))

#%%
now_str()
# %%
def add_days(d, num) -> datetime:
    return(d + timedelta(days=num))
# %%
add_days(datetime.now(), 1)
#%%
def add_days_str(datestr, num) -> str:
    d = datetime.strptime(datestr, DATE_FMT)
    nd = d + timedelta(days=num)
    return nd.strftime(DATE_FMT)
#%%
def addHours(datestr, num) -> str:
    pass
#%% 

# %%
