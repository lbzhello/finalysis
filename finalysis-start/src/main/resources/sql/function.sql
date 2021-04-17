-- 计算股票某天（day）量比
-- code 股票代码，例如 000001
-- date 计算日期，例如 2021-03-04
drop function if exists calculate_volume_ratio(text, date);
create or replace function calculate_volume_ratio(code text, day date) returns decimal as
$$
declare
    vol_ratio decimal(15, 3) := 0;
    t         record;
    v_avg     decimal        := 0;
begin
    select into t stock_code,
                  sum(case row_number when 1 then volume else 0 end) as v1,
                  sum(case row_number when 2 then volume else 0 end) as v2,
                  sum(case row_number when 3 then volume else 0 end) as v3,
                  sum(case row_number when 4 then volume else 0 end) as v4,
                  sum(case row_number when 5 then volume else 0 end) as v5,
                  sum(case row_number when 6 then volume else 0 end) as v6
    from (
             select row_number() over (order by date desc) as row_number, *
             from k_line_2020_2039
             where stock_code = code
               and date <= day
             order by date desc
             limit 6
         ) as t
    group by stock_code;
    v_avg := (t.v2 + t.v3 + t.v4 + t.v5 + t.v6) / 5;

    if v_avg = 0 then
        v_avg := 1;
    end if;

    vol_ratio := t.v1 / v_avg;
    return vol_ratio;
end;
$$ language plpgsql;

-- select calculate_volume_ratio('000063', '2021-03-19');

-- 用于函数返回股票日数据信息
drop type if exists stock_daily cascade;
create type stock_daily as (
   stock_code varchar(6),
   stock_name varchar(32),
   price      decimal(7, 2),
   pct_change decimal(6, 2),
   amount     decimal(14, 2),
   vol_ratio  decimal(6, 2)
);

-- 获取日成交额排行
-- day 计算日期
drop function if exists amount_rank(day date);
create or replace function amount_rank(day date)
    returns setof stock_daily
as
$$
begin
    return query select s.stock_code, s.stock_name, k.close, k.pct_change, k.amount, k.volume_ratio
                 from k_line_2020_2039 k
                          inner join stock s
                                     on k.stock_code = s.stock_code
                 where date = day
                 order by amount desc
                 limit 100;
end;
$$ language plpgsql;

-- select * from amount_rank('2021-04-16');

drop function if exists amount_rank();
create or replace function amount_rank() returns setof stock_daily as
$$
declare
    cur_date date := now();
begin
    select into cur_date date from k_line_2020_2039 order by date desc limit 1;
    return query select * from amount_rank(cur_date);
end;
$$ language plpgsql;

-- select * from amount_rank();