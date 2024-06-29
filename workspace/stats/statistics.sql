-- 查看每日爬取的数据量
select * from data_count();

-- 1. 最近换手与历史换手比值， 说明股价开始异动
select * from data_statistic(5, 10)
where rec_avg_turn_f >= 1
  and turn_ratio >= 1.5
  and rec_avg_amount >= 1e8
--   and rec_avg_turn_f >= 10 and rec_avg_turn_f <=30
order by turn_ratio desc;
-- order by rec_avg_amount desc

-- 2. 根据股票得分大小, 换手比排序
select ss.stock_code,
       max(st.stock_name) as stock_name,
       sum(s.score) as score,
       max(di.turnover_rate_f) as turn,
       max(ds.turn_ratio) as turn_ratio
from stock_score ss
         join score s on ss.score_code = s.score_code
         join stock st on ss.stock_code = st.stock_code
         join daily_indicator di on ss.stock_code = di.stock_code and ss.date = di.date
         join data_statistic(3, 5) ds on ss.stock_code = ds.stock_code
where ss.date = now()::date
-- and s.type in ('turn_ratio', 'increase_ratio')
group by ss.stock_code
order by score desc, turn_ratio desc;

-- 查询股票统计数据
select * from data_statistic(3, 5) where stock_code = '002548';
