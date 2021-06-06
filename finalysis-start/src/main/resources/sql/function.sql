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
drop function if exists create_recommend_500();
create function create_recommend_500() returns int as $$
begin
    -- 保留每日量额前 500 的数据
    drop function if exists recommend_500(date);
    create function recommend_500(day date) returns integer as $f$
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
    drop function if exists recommend_500();
    create function recommend_500() returns int as $f$
    declare
        day date := now();
        sum int := 0;
    begin
        select into day date from recommend order by date desc limit 1;
        select into sum recommend_500(day);
        return sum;
    end
    $f$ language plpgsql;

    return 0;
end
$$ language plpgsql;

select create_recommend_500();