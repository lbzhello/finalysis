#%%
from datetime import datetime, timezone, timedelta
import pytz
import time

datetime.now(tz=pytz.timezone('PRC'))

# %%
time.strftime('%Y-%m-%d 00:00:00+800')
# %%
time.strptime('2020-12-12', '%Y-%m-%d')
# %%
datetime.today()
# %%
