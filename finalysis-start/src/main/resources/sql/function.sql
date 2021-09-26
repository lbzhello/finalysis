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

-- 创建函数 amount_rank(date) amount_rank(); 用于计算日成交额排行
drop function if exists  create_amount_rank();
create or replace function create_amount_rank() returns text as
$$
begin
    -- 用于函数返回股票日数据信息
    drop type if exists stock_daily cascade;
    create type stock_daily as
    (
        stock_code   varchar(6),
        stock_name   varchar(32),
        close        decimal(7, 2),
        pct_change   decimal(6, 2),
        amount       decimal(14, 2),
        volume_ratio decimal(6, 2)
    );

    -- 获取日成交额排行
    -- day 计算日期
    drop function if exists amount_rank(day date);
    create or replace function amount_rank(day date)
        returns setof stock_daily
    as
    $f$
    begin
        return query select s.stock_code, s.stock_name, k.close, k.pct_change, k.amount, k.volume_ratio
                     from k_line_2020_2039 k
                              inner join stock s
                                         on k.stock_code = s.stock_code
                     where date = day
                     order by amount desc
                     limit 100;
    end;
    $f$ language plpgsql;

    -- 获取当日成交额排行
    drop function if exists amount_rank();
    create or replace function amount_rank() returns setof stock_daily as
    $f$
    declare
        cur_date date := now();
    begin
        select into cur_date date from k_line_2020_2039 order by date desc limit 1;
        return query select * from amount_rank(cur_date);
    end;
    $f$ language plpgsql;

    return 'created function: ' || 'amount_rank()' || ' ' || 'amount_rank(date)';
end
$$ language plpgsql;

select create_amount_rank();

-- select * from amount_rank();

-- 获取均线最新日期，5 10 20 30 最新日期中最小的
drop function if exists avg_latest_date();
create or replace function avg_latest_date() returns date as $$
declare
    latest_date date := now();
    latest_date_5 date := now();
    latest_date_10 date := now();
    latest_date_20 date := now();
    latest_date_30 date := now();
begin
    select into latest_date_5 date from avg_line_5 order by date desc limit 1;
    select into latest_date_10 date from avg_line_10 order by date desc limit 1;
    select into latest_date_20 date from avg_line_20 order by date desc limit 1;
    select into latest_date_30 date from avg_line_30 order by date desc limit 1;

    if latest_date > latest_date_5 then
        latest_date := latest_date_5;
    end if;

    if latest_date > latest_date_10 then
        latest_date := latest_date_10;
    end if;

    if latest_date > latest_date_20 then
        latest_date := latest_date_20;
    end if;

    if latest_date > latest_date_30 then
        latest_date := latest_date_30;
    end if;

    return latest_date;
end
$$ language plpgsql;

-- 2021-06-06
-- 创建【删除推荐表多余数据】函数
-- 保留每日量额前 500 的数据
drop function if exists create_retain_recommend_500();
create function create_retain_recommend_500() returns int as $$
begin
    -- 保留每日量额前 500 的数据
    drop function if exists retain_recommend_500(date);
    create function retain_recommend_500(day date) returns integer as $f$
    declare
        sum integer := 0;
    begin
        delete from recommend as a where exists(
            select *
            from (
                select row_number() over (order by vol_amount desc) as num, *
                from recommend as r
                where r.date = day
            ) as b
            where a.id = b.id and b.date = day and b.num > 500
        );
        -- 查询总数，应该小于等于 500
        select into sum count(*) from recommend where date = day;
        return sum;
    end
    $f$ language plpgsql;

    -- 保留当日量额前 500 的数据
    drop function if exists retain_recommend_500();
    create function retain_recommend_500() returns int as $f$
    declare
        day date := now();
        sum int := 0;
    begin
        select into day date from recommend order by date desc limit 1;
        select into sum retain_recommend_500(day);
        return sum;
    end
    $f$ language plpgsql;

    return 0;
end
$$ language plpgsql;

select create_retain_recommend_500();

-- 最近 days 天，量额排名前 head 的股票出现频次排行
drop function if exists hot_recommend(int, int);
create function hot_recommend(days int, head int) returns table (stock_code varchar(6), stock_name varchar(32), count bigint, max_vol_amount decimal(24, 2)) as $$
declare
    end_date date := now(); -- 统计日期，默认数据库最新
    start_date date := now(); -- 统计开始日期 end_date - days
begin
    select date into end_date from recommend order by date desc limit 1;
    start_date := end_date - (days - 1  || ' day')::interval;

    -- 最近 5 日热门股票，量额前 100 出现频次
    return query select v.stock_code, v.stock_name, count(*) as ct, max(v.vol_amount) as max_vol_amount
    from (
             select dense_rank() over (partition by date order by vol_amount desc) as rk, * from v_recommend
         ) as v
    where v.rk <= head
      and v.date >= start_date
    group by v.stock_code, v.stock_name
    order by ct desc, max_vol_amount desc;

end
$$ language plpgsql;

-- 2021-08-16
-- 持续放量股票查询
-- 最近 recStart ~ recEnd 天内的平均成交额与历史 hisStart ~ hisEnd 天内的平均成交额比值数据
drop function if exists sustain_high_vol(recStart date, recEnd date, hisStart date, hisEnd date);
create or replace function sustain_high_vol(recStart date, recEnd date, hisStart date, hisEnd date)
returns table
(
    stock_code     varchar(6),
    recent_amount  decimal(15, 2), -- 最近日均成交额
    history_amount decimal(15, 2), -- 历史日均成交额
    ratio          decimal(8, 4)   -- 最近均成交额与历史均成交额的比值
)
as
$$
declare

begin
    return query with rec as (
        select k.stock_code, sum(amount) as amount, count(*) as total
        from k_line_2020_2039 as k
        where date >= recStart and date <= recEnd
        group by k.stock_code
    ), his as (
        select k.stock_code, sum(amount) as amount, count(*) as total
        from k_line_2020_2039 as k
        where date >= hisStart and date <= hisEnd
        group by k.stock_code
    )
    select rec.stock_code,
           round(rec.amount/rec.total::numeric, 4) as recent_amount,
           round(his.amount/his.total::numeric, 4) as history_amount,
           round(rec.amount/his.amount::numeric, 4) * round(his.total/rec.total::numeric, 4) as ratio
    from rec
    join his on rec.stock_code = his.stock_code;
end;
$$ language plpgsql;

-- 持续放量股票查询
-- 最近 recDays 天内的平均成交额与历史 hisDays 天内的平均成交额比值数据
drop function if exists sustain_high_vol(recDays int, hisDays int);
create or replace function sustain_high_vol(recDays int, hisDays int) returns
table
(
    stock_code     varchar(6),
    recent_amount  decimal(15, 2), -- 最近日均成交额
    history_amount decimal(15, 2), -- 历史日均成交额
    ratio          decimal(8, 4)   -- 最近均成交额与历史均成交额的比值
)
as
$$
declare
    dateArr date[];
    total int := recDays + hisDays;
    recStart date;
    recEnd date;
    hisStart date;
    hisEnd date;
begin
    -- 获取需要统计的日期
    select array_agg(date) into dateArr
    from (
        select date from k_line_2020_2039 group by date order by date desc limit total
    ) tmp;

    recEnd := dateArr[1];
    recStart := dateArr[recDays];

    hisEnd := dateArr[recDays + 1];
    hisStart := dateArr[total];

    return query select * from sustain_high_vol(recStart, recEnd, hisStart, hisEnd);

end;
$$ language plpgsql;

-- select * from sustain_high_vol(3, 5) order by ratio desc;

-- 2021-09-26
-- 增加 data_statistic 创建函数，方便统一管理
drop function if exists create_data_statistic();
create or replace function create_data_statistic() returns text as $c$
begin

-- 2021-09-11
-- 股票历史数据统计表
drop function if exists data_statistic(recdays integer, hisdays integer);
drop function if exists data_statistic(recDays int, hisDays int, curDay date);
drop table if exists data_statistic;
create table data_statistic (
    stock_code   varchar(6)  not null default '',
    stock_name   varchar(32) not null default '',

    rec_days int not null default 0,
    his_days int not null default 0,

    rec_pct_change decimal(7, 2) not null default 0,
    his_pct_change decimal(7, 2) not null default 0,

    rec_avg_turn_f decimal(5, 2) not null default 0,
    his_avg_turn_f decimal(5, 2) not null default 0,

    rec_avg_amount decimal(17, 2) not null default 0,
    his_avg_amount decimal(17, 2) not null default 0,

    rec_avg_vol_ratio decimal(7, 2) not null default 0,
    his_avg_vol_ratio decimal(7, 2) not null default 0,

    rec_turn_f decimal(7, 2) not null default 0,
    his_turn_f decimal(7, 2) not null default 0,

    rec_amount decimal(17, 2) not null default 0,
    his_amount decimal(17, 2) not null default 0,

    rec_vol_ratio decimal(7, 2) not null default 0,
    his_vol_ratio decimal(7, 2) not null default 0,

    pct_change_ratio decimal(7, 2) not null default 0,
    turn_ratio decimal(7, 2) not null default 0
);

comment on table data_statistic is '股票历史数据统计';
comment on column data_statistic.stock_code is '股票代码';
comment on column data_statistic.stock_name is '股票名称';
comment on column data_statistic.rec_days is '最近统计天数';
comment on column data_statistic.his_days is '过去统计天数';
comment on column data_statistic.rec_pct_change is '最近几日涨幅';
comment on column data_statistic.his_pct_change is '过去几日涨幅';
comment on column data_statistic.rec_avg_turn_f is '最近几日平均自由换手';
comment on column data_statistic.his_avg_turn_f is '过去几日平均自由换手';
comment on column data_statistic.rec_avg_amount is '最近几日平均成交额';
comment on column data_statistic.his_avg_amount is '过去几日平均成交额';
comment on column data_statistic.rec_avg_vol_ratio is '最近几日平均量比';
comment on column data_statistic.his_avg_vol_ratio is '过去几日平均量比';
comment on column data_statistic.rec_turn_f is '最近几日自由换手';
comment on column data_statistic.his_turn_f is '过去几日自由换手';
comment on column data_statistic.rec_amount is '最近几日成交额';
comment on column data_statistic.his_amount is '过去几日成交额';
comment on column data_statistic.rec_vol_ratio is '最近几日量比';
comment on column data_statistic.his_vol_ratio is '过去几日量比';
comment on column data_statistic.pct_change_ratio is '最近与过去增幅比值';
comment on column data_statistic.turn_ratio is '最近与过去平均自由换手比值';

-- 股票历史数据统计
-- recDays 最近天数
-- hisDays 过去天数（不包括最近天数）
-- curDay 统计日期
create or replace function data_statistic(recDays int, hisDays int, curDay date) returns setof data_statistic as $$
declare

begin
    return query
        -- 龙抬头，最近三日涨幅,量比
        with rec_dt as ( -- 最近日期
            select date
            from k_line_2020_2039
            where date <= curDay
            group by date
            order by date desc
            limit recDays
        ), his_dt as ( -- 过去日期
            select date
            from k_line_2020_2039
            where date <= curDay
            group by date
            order by date desc
            limit hisDays offset recDays
        ), his as ( -- 过去成交数据
            select k.stock_code,
                   sum(k.pct_change) as his_pct_change,
                   sum(k.amount) as his_amount,
                   sum(di.volume_ratio) as his_vol_ratio,
                   sum(di.turnover_rate_f) as his_turn_f
            from his_dt
                 join k_line_2020_2039 k on k.date = his_dt.date
                 join daily_indicator di on di.date = k.date and di.stock_code = k.stock_code
            group by k.stock_code
        ), rec as ( -- 最近成交数据
            select k.stock_code,
                   sum(k.pct_change) as rec_pct_change,
                   sum(k.amount) as rec_amount,
                   sum(di.volume_ratio) as rec_vol_ratio,
                   sum(di.turnover_rate_f) as rec_turn_f
            from rec_dt
                 join k_line_2020_2039 k on k.date = rec_dt.date
                 join daily_indicator di on di.date = k.date and di.stock_code = k.stock_code
            group by k.stock_code
        )
        select s.stock_code,
               s.stock_name,

               recDays rec_days,
               hisDays his_days,

               rec_pct_change::decimal(7, 2),
               his_pct_change::decimal(7, 2),

               round(rec_turn_f/recDays, 2)::decimal(5, 2) rec_avg_turn_f,
               round(his_turn_f/hisDays, 2)::decimal(5, 2) his_avg_turn_f,

               round(rec_amount/recDays, 2)::decimal(17, 2) rec_avg_amount,
               round(his_amount/hisDays, 2)::decimal(17, 2) his_avg_amount,

               round(rec_vol_ratio/recDays, 2)::decimal(7, 2) rec_avg_vol_ratio,
               round(his_vol_ratio/hisDays, 2)::decimal(7, 2) his_avg_vol_ratio,

               rec_turn_f::decimal(7, 2),
               his_turn_f::decimal(7, 2),

               rec_amount::decimal(17, 2),
               his_amount::decimal(17, 2),

               rec_vol_ratio::decimal(7, 2),
               his_vol_ratio::decimal(7, 2),
               (rec_pct_change/(case when abs(his_pct_change) < 1 then 1 else abs(his_pct_change) end))::decimal(7, 2) pct_change_ratio,
               (round(rec_turn_f/recDays, 2)/round(his_turn_f/hisDays, 2))::decimal(7, 2) turn_ratio
        from stock s
             join rec on s.stock_code = rec.stock_code
             join his on s.stock_code = his.stock_code;
end
$$ language plpgsql;

-- 股票历史数据统计
-- recDays 最近天数
-- hisDays 过去天数（不包括最近天数）
create or replace function data_statistic(recDays int, hisDays int) returns setof data_statistic as $$
declare
    curDay date := now();
begin
    select date into curDay from k_line_2020_2039 order by date desc limit 1;
    return query select * from data_statistic(recDays, hisDays, curDay);
end;
$$ language plpgsql;

return 'success';
end;
$c$ language plpgsql;

select create_data_statistic();