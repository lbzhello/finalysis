select *
from stock
where stock_code = '002594';

select *
from k_line
where stock_code = '002594';

select count(*)
from k_line;

delete
from k_line
where stock_code = '000826';

insert into k_line (stock_code, date_time)
values ('000826', '2020-12-31 00:00:00+08:00');

delete
from k_line
where stock_code = '000826';

select *
from k_line
where stock_code = '002594';

insert into k_line (stock_code, date_time)
values ('002594', '2020-12-31 00:00:00');

delete
from k_line
where stock_code = '002594'
  and date_time = '2020-12-31 00:00:00';

select extract(epoch from date_time)
from k_line
where stock_code = '002594';

select extract(epoch from now());

show time zone;

set time zone 'Asia/Shanghai';

select *
from k_line
where stock_code = '002594';

select *
from k_line;

alter table k_line
    add column id bigserial;

alter table k_line
    add constraint pk_k_line primary key (id);

select *
from k_line
where stock_code = '002594';

select *
from stock
where stock_code = '000760';

select count(*)
from k_line;

select count(*)
from stock;

select *
from stock;

select *
from k_line
where stock_code = '002131';

select count(distinct stock_code)
from k_line;

select *
from stock
where stock_code not in (
    select distinct stock_code
    from k_line
    where date_time = '2021-01-04'
);

select count(*)
from k_line;

select *
from k_line
order by date_time desc
limit 10;

select *
from stock;

drop index k_line_stock_code;

create unique index uk_k_line_stock_code_date_time on k_line (stock_code, date_time);

alter table k_line
    rename inc to change;
alter table k_line
    rename inc_rate to pcg_change;
alter table k_line
    rename pcg_change to pct_change;

-- 2021-01-08
select *
from k_line
where stock_code = '002594'
order by date_time desc
limit 10;


alter table k_line
    alter column volume type bigint;

update k_line
set volume = volume * 100;

select *
from k_line
order by volume desc
limit 100;


select *
from k_line
where stock_code = '002594'
order by date_time desc
limit 100;

select count(*)
from k_line;

select *
from k_line
order by stock_code desc
limit 100;


select *
from k_line
order by date_time desc
limit 100;

select *
from stock
limit 10;

select count(*)
from stock;

select count(*)
from stock
where stat = 0;


select *
from k_line
order by date_time desc
limit 10;

select *
from k_line
where stock_code = '600000'
order by date_time desc;


select *
from k_line
where stock_code = '000001';

select count(*)
from k_line;

set time zone 'Asia/Shanghai';

show timezone;

select count(*)
from k_line
where date_time >= '2015-01-01 00:00:00';

alter table k_line
    alter column date_time type timestamp using date_time::date;

select *
from k_line
limit 10;

create table tmp
(
    dt timestamp
);

alter table tmp
    alter column dt type date;

select *
from tmp;

drop table tmp;

alter table k_line
    alter column date_time type date;

select *
from k_line
limit 10;

alter table k_line
    rename column date_time to date;

-- 2020-01-16
select count(*)
from k_line
where date >= '2010-01-01';

select count(*)
from k_line
where date >= '2015-01-01';

-- 创建 hash 分区表
create table t_list
(
    id   int,
    info text
) PARTITION BY LIST (id);

-- 创建子分区; 表示满足条件 id/4 余 0
CREATE TABLE t_list_0 PARTITION OF t_list
    FOR VALUES IN (0, 1);

CREATE TABLE t_list_2 PARTITION OF t_list
    FOR VALUES IN (2, 3);

create table t_list_4
(
    like t_list including defaults including constraints
);

alter table t_list
    attach partition t_list_4 for values in (4, 5);

insert into t_list
values (1, 'hello');
insert into t_list
values (3, 'hello');
insert into t_list
values (4, 'hello');

insert into t_list
values (8, 'hello');

select TABLEOID::regclass, *
from t_list;

select count(*)
from k_line
where date between '2020-01-01' and '2020-12-31';

create table k_line_2015_2025
(
    like k_line including defaults including constraints
);

select count(*)
from k_line_2015_2025;

select count(*)
from k_line
where date >= '2015-01-01';

select *
from k_line
order by date desc
limit 100;

select count(*)
from k_line;

select count(*)
from k_line
where date >= '2015-01-01';

select count(*)
from k_line_2015_2025
where date >= '2015-01-01';

insert into k_line_2015_2025 (select * from k_line where date >= '2015-01-01');

delete
from k_line
where date >= '2015-01-01';

insert into k_line (select * from k_line_2015_2025 where date < '2020-01-01');
delete
from k_line_2015_2025
where date < '2020-01-01';

select count(*)
from k_line_2015_2025
where date < '2020-01-01';
select count(*)
from k_line
where date < '2020-01-01'
  and date >= '2015-01-01';

select count(*)
from k_line_2015_2025;

select *
from k_line
order by date desc
limit 100;

alter table k_line_2015_2025
    rename to k_line_2020_2039;

select *
from k_line_2020_2039;

alter table k_line
    rename to k_line_1990_2019;

create unique index uk_t_list_0 on t_list_0 (id);

select *
from t_list_0;
insert into t_list_0
values (1, 'hello');

select *
from t_list_2;
insert into t_list_2
values (1, 'hello');

select *
from k_line;

-- check 约束，避免加入分区时全表扫描
alter table k_line_1990_2019
    add constraint ck_k_line_1990_2019_date
        check ( date >= date '1990-01-01' and date < date '2020-01-01');

alter table k_line_1990_2019
    add constraint pk_k_line_1990_2019 primary key (id);
create unique index uk_k_line_1990_2019_stock_code_date on k_line_1990_2019 (stock_code, date);

-- 加入 k_line 分区
alter table k_line
    attach partition k_line_1990_2019
        for values from ('1990-01-01') to ('2020-01-01');

-- check 约束，避免加入分区时全表扫描
alter table k_line_2020_2039
    add constraint ck_k_line_2020_2039
        check ( date >= date '2020-01-01' and date < date '2040-01-01');

alter table k_line_2020_2039
    add constraint pk_k_line_2020_2039 primary key (id);

create unique index uk_k_line_2020_2039_stock_code_date on k_line_2020_2039 (stock_code, date);

-- 加入 k_line 分区
alter table k_line
    attach partition k_line_2020_2039
        for values from ('2020-01-01') to ('2040-01-01');

select count(*)
from k_line;

select *
from k_line
where date >= '2020-01-01'
order by date desc
limit 100;

-- 2021-01-24
select *
from k_line
where date > '2020-01-01'
order by date desc
limit 100;

select *
from k_line
order by id desc
limit 1;

select nextval(pg_get_serial_sequence('k_line', 'id'));

-- 设置自增 ID 值
select setval(pg_get_serial_sequence('k_line', 'id'), 11068174, true);

select count(*) as ct, stock_code, date
from k_line
where date > '2020-01-01'
group by stock_code, date
order by ct desc;

select *
from stock
order by listing_date desc;

-- 2021-01-26
select *
from stock
order by listing_date desc;

-- 1924
delete
from k_line_2020_2039
where date >= '2021-01-23';

select *
from k_line_2020_2039
order by date desc
limit 100;

-- 1016784
select count(*)
from k_line
where date >= '2020-01-01';

-- 8274
select count(*)
from k_line_2020_2039
where date >= '2021-01-23';

-- 1023134
select count(*)
from k_line
where date >= '2020-01-01';

select *
from k_line_2020_2039
where date = '2021-01-26'
order by pct_change desc
limit 100;

select *
from k_line_2020_2039
where stock_code = '000002'
  and date = '2020-08-10';

-- 2021-01-31
select *
from stock
order by listing_date desc;

select count(*)
from k_line_2020_2039;

select *
from k_line_2020_2039
order by date desc
limit 100;

select *
from k_line_2020_2039
where stock_code = '000026'
limit 1;

select *
from avg_line;

select *
from k_line_2020_2039
where stock_code = '000032'
order by date desc;

select *
from avg_line
where stock_code = '000001'
order by date desc
limit 100;

select count(*)
from avg_line;

select count(*)
from k_line_2020_2039
where date >= '2021-01-01';

select *
from avg_line
where stock_code = '000725'
order by date desc
limit 100;

select *
from k_line_2020_2039
where date >= '2021-01-01'
limit 100;

-- 2021-02-07
select *
from k_line_2020_2039
order by date desc
limit 100;

select *
from stock
order by listing_date desc;

select count(*)
from k_line_2020_2039;

select count(*)
from avg_line;

-- 2021-02-12
select count(*)
from stock;

select *
from stock
order by listing_date desc;

select count(*)
from k_line_2020_2039;

select *
from k_line_2020_2039
order by date desc
limit 100;

-- 2021-02-15
SELECT date
FROM avg_line
order by date desc
limit 1;

-- 2021-02-18

select *
from k_line_2020_2039
order by date desc
limit 100;

select count(*)
from k_line_2020_2039;

select count(*)
from avg_line;

-- 2021-02-20

-- 重构 avg_line 均线表
drop table if exists avg_line;
create table avg_line
(
    id         bigserial,
    stock_code varchar(6)    not null,
    date       date          not null default now(),
    current    decimal(7, 2) not null default 0,
    days       integer       not null default 0,
    avg        decimal(7, 2) not null default 0
);

comment on table avg_line is '均线数据';
comment on column avg_line.id is '自增主键';
comment on column avg_line.stock_code is '股票代码。如 000001';
comment on column avg_line.date is '日期';
comment on column avg_line.days is '计算均线天数';
comment on column avg_line.current is '当日价格';
comment on column avg_line.avg is 'days 均值';

-- 先不加索引用于快速批量插入
alter table avg_line
    add constraint pk_avg_line primary key (id);
create unique index uk_avg_line_stock_code_date_days on avg_line (stock_code, date, days);

delete
from avg_line
where id != 1;

select *
from avg_line;

insert into avg_line
(stock_code, date, count, current, avg)
values ('1', '2020-02-02', 882, 5, 89),
       ('1', '2020-02-02', 88, 6, 810)
on conflict (stock_code, date, count) do update set avg=excluded.avg,
                                                    current=excluded.current;

alter table avg_line
    drop constraint pk_avg_line;
drop index uk_avg_line_stock_code_date_days;

select count(*) * 4
from k_line_2020_2039;

select count(*)
from avg_line;

-- 000157 2021-02-19 30
select *
from avg_line
order by id
limit 10;

-- m5 >= m10
select stock_code, current
from avg_line t1
where date = '2021-02-19'
  and count = 5
  and avg >= (
    select avg
    from avg_line t2
    where t2.stock_code = t1.stock_code
      and t2.date = t1.date
      and t2.count = 10
)
order by current desc;

select count(*)
from (
         select stock_code,
                date,
                current,
                sum(case count when 5 then avg else 0 end)  as avg5,
                sum(case count when 10 then avg else 0 end) as avg10,
                sum(case count when 20 then avg else 0 end) as avg20,
                sum(case count when 30 then avg else 0 end) as avg30
         from avg_line
         where date = '2021-02-19'
         group by stock_code, date, current
     ) t
where t.avg5 > t.avg10;

-- 2021-02-22
select *
from k_line_2020_2039
order by date desc
limit 10;

select count(*)
from k_line_2020_2039;

-- 2021-02-28
select *
from avg_line
order by date desc
limit 10;

select *
from avg_line
order by date desc
limit 10;

select *
from k_line_2020_2039
order by date desc
limit 10;

select *
from stock
order by listing_date desc;

-- 2021-03-07
select count(*)
from k_line_2020_2039;
select count(*)
from avg_line;

select *
from avg_line
order by date desc
limit 10;

-- 2021-03-13
select *
from k_line_2020_2039
order by date desc
limit 10;

select *
from avg_line
order by stock_code, date desc;

select *
from (
         select stock_code,
                date,
                current,
                sum(case statistic when 5 then avg else 0 end)  as avg5,
                sum(case statistic when 10 then avg else 0 end) as avg10,
                sum(case statistic when 20 then avg else 0 end) as avg20,
                sum(case statistic when 30 then avg else 0 end) as avg30
         from avg_line
         where date = '2021-03-12'
         group by stock_code, date, current
     ) as t
where t.avg5 > t.avg10
limit 3 offset 0;

-- 5 日线小于 10 日线
select distinct t1.stock_code
from avg_line as t1,
     avg_line as t2
where t1.date >= '2021-03-08'
  and t1.stock_code = t2.stock_code
  and t1.date = t2.date
  and t1.statistic = 5
  and t2.statistic = 10
  and t1.avg < t2.avg;

-- DATE 日期以来上升趋势的股票
select *
from stock
where stock_code not in (
    select distinct t1.stock_code
    from avg_line as t1,
         avg_line as t2
    where t1.date >= '2021-03-08'
      and t1.stock_code = t2.stock_code
      and t1.date = t2.date
      and t1.statistic = 5
      and t2.statistic = 10
      and t1.avg < t2.avg
)
order by stock_code;

-- 2021-03-15
select *
from k_line_2020_2039
order by date desc
limit 5;

-- 2021-03-19
select *
from k_line_2020_2039
order by stock_code, date desc
limit 10;

-- 2021-03-20
select *
from avg_line
order by date desc
limit 10;

select *
from k_line_2020_2039
limit 5;

-- 计算量比
select stock_code, sum(case date when '2021-03-19' then amount else 0 end) as day0
from k_line_2020_2039
where stock_code = '000063'
group by stock_code;

select date - interval '1 day'
from k_line_2020_2039
order by date desc
limit 10;

select row_number() over (order by date desc), *
from k_line_2020_2039
where stock_code = '000063'
order by date desc
limit 6;

-- 量比
select calculate_volume_ratio('000063', '2021-03-22');

-- 2021-03-24
select *
from k_line_2020_2039
order by date desc
limit 100;

-- and id = 11155880
select *
from k_line_2020_2039
where stock_code = '000001'
order by date desc;

select date, close, volume_ratio
from k_line_2020_2039
where stock_code = '000001'
order by date desc;

select *
from k_line_2020_2039
order by stock_code desc, date desc
limit 100;

-- 300676
select *
from k_line_2020_2039
where id = 1642;

-- todo
-- why?
UPDATE k_line
SET date='2021-01-22'::date,
    volume_ratio='1.8639'::numeric
WHERE id = 1642

select *
from k_line_2020_2039
where volume_ratio = 0;

select *
from k_line_2020_2039
where stock_code = '300033'
order by date desc;

select *
from k_line_2020_2039
order by date desc
limit 100;

-- 2021-04-03
select *
from k_line_2020_2039
order by date desc
limit 10;

select *
from avg_line
order by date desc;

SELECT stock_code, stock_name, board, stat, listing_date
FROM stock
WHERE stock_code IN ('');

-- 2021-04-05
select *
from k_line_2020_2039
order by date desc;

-- 2021-04-05

-- 分页查询函数
drop function if exists page_query(text, text);
create or replace function page_query(fields text, from_clause text) returns setof record as
$$
declare
    rec        record;
    sql_clause varchar := '';
    ct_clause  text    := '';
    ct         int     := 0;
begin
    sql_clause := 'select ' || fields || ' ' || from_clause;
    ct_clause := 'select count(*) ' || from_clause;
    execute ct_clause into ct;
    for rec in
        execute sql_clause
        loop
            return next rec;
        end loop;
    return;
end;
$$ language plpgsql;

-- select * from page_query('stock_code, date', 'from avg_line limit 4')
-- as avg_line(stock_code varchar(6), date date);

create or replace function test_page(count_clause text, query_clause text) returns setof avg_line as
$$
declare
    ct  int := 0;
    rec record;
begin
    execute count_clause into ct;
    for rec in execute query_clause
        loop
            return next rec;
        end loop;
    return;
end;
$$ language plpgsql;

select *
from test_page('select count(*) from avg_line',
               'select * from avg_line limit 5');

-- 2021-04-11
select *
from k_line_2020_2039
order by date desc
limit 100;

-- 获取日成交额排行
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

select * from amount_rank('2021-04-16');

select * from avg_line order by date desc limit 100;

-- 2021-04-17
select * from k_line_2020_2039 order by date desc limit 100;

-- 2021-04-24
select * from k_line_2020_2039 order by date desc;

-- 2021-05-01
select * from avg_line order by date desc limit 100;

drop view if exists v_avg_line;
create view v_avg_line as
select a5.stock_code, a5.date, a5.current,
       a5.avg avg5, a10.avg avg10, a20.avg avg20, a30.avg avg30
from avg_line_5 as a5
         join avg_line_10 as a10
              on a5.stock_code = a10.stock_code
                  and a5.date = a10.date
         join avg_line_20 as a20
              on a5.stock_code = a20.stock_code
                  and a5.date = a20.date
         join avg_line_30 as a30
              on a5.stock_code = a30.stock_code
                  and a5.date = a30.date;

select * from v_avg_line
where date = '2021-04-23'
  and avg5 < avg10;

select * from stock as s
where not exists (
        select * from v_avg_line as v
        where s.stock_code = v.stock_code
          and date = '2021-04-23'
          and avg5 < avg10
    );

select * from avg_line order by date desc limit 100;

select * from v_avg_line;

-- 2021-05-02
select * from avg_line_5 order by id desc limit 100;

delete from avg_line_5 where stock_code in ('123', '1232');

delete from avg_line where stock_code = '8577';

select * from avg_line where stock_code = '8577';

select * from avg_line order by date desc limit 100;

select * from avg_line_5 order by id desc limit 100;

select * from avg_line_10 order by id desc limit 100;

select * from avg_line_20 order by id desc limit 100;

select * from avg_line_30 order by id desc limit 100;

delete from avg_line_5 where stock_code = '8577';
delete from avg_line_10 where stock_code = '8577';
delete from avg_line_20 where stock_code = '8577';
delete from avg_line_30 where stock_code = '8577';

select avg_latest_date();

select * from avg_line order by date desc limit 100;

select count(*) from avg_line_5;
select count(*) from avg_line_10;

-- 2021-05-03
explain select * from v_avg_line order by date, stock_code desc limit 100;
select * from v_avg_line order by date, stock_code desc limit 100;

-- 2021-05-04
select * from k_line_2020_2039 order by date desc limit 100;

-- 2021-05-05
select * from daily_indicator order by date desc;

select * from k_line_2020_2039 where volume_ratio is not null and date = '2020-01-03' order by date;

select * from daily_indicator order by date desc;

select count(*) from daily_indicator;

select count(*) from k_line_2020_2039;

-- 2021-04-08
select * from k_line_2020_2039 order by date desc limit 100;

select * from daily_indicator order by date desc limit 100;

select count(*) from avg_line_5;

-- 2021-05-09
select * from daily_indicator where date <= '2021-04-30' order by date desc limit 100;


select * from daily_indicator where dv_ttm = 0 order by date asc ;

select * from daily_indicator where stock_code = '689009' order by date desc;

select * from daily_indicator order by date desc;

select * from k_line_2020_2039 order by id desc;

insert into k_line_2020_2039 (stock_code, date, open, close, high, low, change, pct_change, volume, amount,
                              volume_ratio, turn, committee, selling, buying)
values
('000683', '2021-05-07', 3.09, 3.21, 3.36, 3.08, 0.15, 4.90, 5, 745580911.00, 0.00, 0.00, 0.00, 0.00, 0.00)
on conflict (stock_code, date) do update
    set open         = excluded.open,
        close        = excluded.close,
        high         = excluded.high,
        low          = excluded.low,
        change       = excluded.change,
        pct_change   = excluded.pct_change,
        volume       = excluded.volume,
        amount       = excluded.amount,
        volume_ratio = excluded.volume_ratio,
        turn         = excluded.turn,
        committee    = excluded.committee,
        selling      = excluded.selling,
        buying       = excluded.buying;

select * from k_line_2020_2039 order by date desc limit 1;


insert into k_line_2020_2039 (stock_code, date, open, close, high, low, change, pct_change, volume, amount,
                              volume_ratio, turn, committee, selling, buying)
values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
on conflict (stock_code, date) do update set open         = excluded.open,
                                             close        = excluded.close,
                                             high         = excluded.high,
                                             low          = excluded.low,
                                             change       = excluded.change,
                                             pct_change   = excluded.pct_change,
                                             volume       = excluded.volume,
                                             amount       = excluded.amount,
                                             volume_ratio = excluded.volume_ratio,
                                             turn         = excluded.turn,
                                             committee    = excluded.committee,
                                             selling      = excluded.selling,
                                             buying       = excluded.buying;

select * from k_line_2020_2039 where id= '11070142';

select stock_code, date, id from k_line_2020_2039 order by date desc;

select * from k_line_2020_2039 where id = 11070470;

select * from k_line_2020_2039 where id <= 11070470 order by id desc;

select id from k_line_2020_2039 order by id desc limit 1;

select currval('k_line_id_seq');

-- 11367787
select setval('k_line_id_seq', max(id)) from k_line_2020_2039;

select * from k_line_2020_2039 order by id desc limit 100;

select * from daily_indicator where stock_code = '600906';

select * from v_avg_line order by date desc limit 100;

select * from daily_indicator where date = '2021-05-07' and total_mv >= 50000000000 order by volume_ratio desc;

-- 2021-05-14
select * from daily_indicator order by date desc limit 6000;

select count(*) from daily_indicator;

select count(*) from k_line_2020_2039;

select * from k_line_2020_2039 order by date desc limit 6000;

select * from k_line_2020_2039 where volume > 1000 order by date desc limit 1000;

select count(*) from avg_line_5;

with daily_cte as (
    select s.stock_code,
           s.stock_name,
           k.date,
           k.open,
           k.close,
           k.high,
           k.low,
           k.change,
           k.pct_change,
           k.volume,
           k.volume_ratio,
           k.amount,
           d.pe,
           d.pe_ttm,
           d.turnover_rate,
           d.turnover_rate_f,
           d.total_mv,
           d.circ_mv
    from k_line_2020_2039 k
             join daily_indicator d
                  on k.date = d.date and k.stock_code = d.stock_code
             join stock s
                  on k.stock_code = s.stock_code and k.date = '2021-05-17'
    where k.amount > 1e9
    order by volume_ratio desc
)
select count(*) from daily_cte;

select count(*) from stock

-- 2021-05-18
-- daily_indicator 和 k_line_2022-2039 表数据不一致问题 todo
select count(*) from k_line_2020_2039 where date = '2021-05-31'

select count(*) from daily_indicator where date = '2021-05-31'

select * from k_line_2020_2039 order by date desc limit 100;

select * from stock order by  listing_date desc;

-- 2021-05-23
select distinct stock_code from daily_indicator where date >= '2021-05-21'

select * from daily_indicator order by date desc limit 100;

select * from stock where stock_code = '600220'

select * from stock as a where not exists(
        select * from stock b
        where a.stock_code = b.stock_code
          and b.stock_code in ('600220')
    )
                           and a.stock_code = '600217'

select stock_code
from stock as s
where not exists(select *
                 from v_avg_line as v
                 where v.stock_code = s.stock_code and v.date >= ? and v.date <= ? and v.avg5 < v.avg10)
order by stock_code;

-- 2021-06-03
select * from v_avg_line where stock_code = '605339' order by date desc

select * from daily_indicator order by date desc

-- 2021-06-06
select count(*) from recommend;

select * from daily_indicator order by date desc

-- 600371
select row_number() over (order by vol_amount desc) as num, * from recommend b


select row_number() over (order by vol_amount desc) as num, * from recommend limit 500

select count(*) from k_line_2020_2039

select count(*) from avg_line_5

select count(*) from daily_indicator

select count(*) from recommend;

-- 推荐表每日只保留 500 条数据
delete from recommend as a where exists(
                                         select *
                                         from (
                                                  select row_number() over (order by vol_amount desc) as num, *
                                                  from recommend
                                              ) as b
                                         where a.id = b.id and b.date = '2021-06-04' and num > 500
                                     );

select row_number() over (order by vol_amount desc) as num, *
from recommend as r
where r.date = '2021-06-04';

select date from recommend order by date desc limit 1;

select * from retain_recommend_500('2021-06-04');

select * from retain_recommend_500();

select count(*) from recommend where date = '2021-05-31'

select * from recommend where date = '2021-06-01' order by vol_amount desc

select count(*) from daily_indicator where date = '2021-06-04'

select count(*) from k_line_2020_2039 where date = '2021-06-04'

select * from v_recommend where date >= '2021-06-04' order by vol_amount desc limit 100

-- 2021-06-07
select count(*) from k_line_2020_2039 where date = '2021-06-10'

select count(*) from daily_indicator where date = '2021-06-10'

select * from k_line_2020_2039 order by date desc limit 100

select * from daily_indicator order by date desc limit 100

select count(*) from k_line_2020_2039 where date = '2021-07-02'

select count(*) from daily_indicator where date = '2021-07-01'

select * from hot_recommend(1, 100)

select * from v_recommend where date >= '2021-06-11' order by vol_amount desc

select * from k_line_2020_2039 where date = '2021-07-02'

select * from hot_recommend(3, 100)

select * from recommend order by date desc limit 10

-- 最近 5 日热门股票，量额前 100 出现频次
select stock_code, stock_name, count(*) as ct, max(vol_amount) as max_vol_amount, max(volume_ratio) as max_vol_ratio
from (
         select dense_rank() over (partition by date order by vol_amount desc) as rk, * from v_recommend
     ) as v
where rk <= 100
  and date >= '2021-06-21'
group by stock_code, stock_name
order by ct desc, max_vol_ratio desc;

select count(*) from k_line_2020_2039 where date = '2021-07-26'

select count(*) from daily_indicator where date = '2021-07-26'

-- 当日推荐股票量额排序
select * from v_recommend where date = '2021-08-13' order by vol_amount desc

-- 2021-08-15 串口函数
select stock_code, sum(amount) as sum3
from k_line_2020_2039
where date >= '2021-07-10'
group by stock_code
order by sum3 desc

select date from k_line_2020_2039 where date >= '2021-07-01' group by date;

select round(2/3::numeric, 4);


explain analyse select date from k_line_2020_2039 group by date limit 100

select * from (
                  with rec as ( select stock_code, sum(amount) as amount from k_line_2020_2039 WHERE date >= '2021-08-11' and date <= '2021-08-13' group by stock_code ),
                       his as ( select stock_code, sum(amount) as amount from k_line_2020_2039 WHERE date >= '2021-08-04' and date <= '2021-08-10' group by stock_code )
                  select rec.stock_code,
                         round(rec.amount/his.amount::numeric, 4) as ratio
                  from rec join his on rec.stock_code = his.stock_code) as t where ratio > 2

explain analyse with rec as ( select stock_code, sum(amount) as amount from k_line_2020_2039 WHERE date >= '2021-08-11' and date <= '2021-08-13' group by stock_code ),
                     his as ( select stock_code, sum(amount) as amount from k_line_2020_2039 WHERE date >= '2021-08-04' and date <= '2021-08-10' group by stock_code )
                select rec.stock_code,
                       round(rec.amount/his.amount::numeric, 4) as ratio
                from rec join his on rec.stock_code = his.stock_code where round(rec.amount/his.amount::numeric, 4) > 2 order by ratio desc

select * from v_recommend where date = '2021-08-27' order by vol_amount desc

select count(*) from k_line_2020_2039 where date = '2021-09-13'

select count(*) from daily_indicator where date = '2021-09-13'

-- 2021-08-17
-- 持续放量指标，成交额排序
select s.stock_name, a.* from sustain_high_vol(3, 5) as a
                                  join stock as s on a.stock_code = s.stock_code
where ratio >= 2
order by recent_amount desc

-- 龙抬头，最近三日涨幅,量比
with dt as ( -- 最近日期
    select date
    from k_line_2020_2039
    group by date
    order by date desc
    limit 3
), his_dt as ( -- 过去日期
    select date
    from k_line_2020_2039
    group by date
    order by date desc
    limit 5 offset 3
), his as ( -- 过去成交数据
    select stock_code,
           sum(volume_ratio)/5 as avg_ratio,
           sum(turnover_rate_f)/5 as avg_turn
    from his_dt
             join daily_indicator di on di.date = his_dt.date
    group by di.stock_code
), rec as ( -- 最近成交数据
    select k.stock_code,
           sum(k.pct_change) as total_inc,
           sum(di.volume_ratio)/3 as avg_ratio,
           sum(di.turnover_rate_f)/3 as avg_turn
    from dt
             join k_line_2020_2039 k on k.date = dt.date
             join daily_indicator di on di.stock_code = k.stock_code and di.date = k.date
    group by k.stock_code
)
select s.stock_name,
       s.stock_code,
       k.pct_change,
       rec.total_inc,
       rec.avg_turn /his.avg_turn     as turn_ratio,
       (rec.avg_turn/his.avg_turn)*rec.total_inc as inc_turn, -- 增幅*最近历史平均换手率比值
       di.volume_ratio * k.amount/1e8 as vol_amount
from stock as s
         join rec on s.stock_code = rec.stock_code
         join his on s.stock_code = his.stock_code
         join k_line_2020_2039 k on s.stock_code = k.stock_code
    and k.date = '2021-09-09'
         join daily_indicator di on s.stock_code = di.stock_code
    and di.date = '2021-09-09'
where total_inc >= 10
order by inc_turn desc;

-- 2021-09-12
-- 成交量指标 最近换手和历史换手比值，越大说明股票开始持续异动
select * from data_statistic(3, 10, '2021-07-27')
where rec_avg_turn_f >= 1 and rec_pct_change < 40
order by rec_avg_turn_f/his_avg_turn_f desc;

-- 价格指标 最近增幅与过去增幅的比值，越大说明股票价格开始抬头向上
select * from data_statistic(3, 10, '2021-08-16')
order by rec_pct_change/(case when abs(his_pct_change) < 1 then 1 else his_pct_change end) desc;


-- 推荐表结构修改这里备份下
create table recommend_backup_2021_09_12 as select * from recommend;

drop table recommend;


-- 2021-09-13
-- 持续放量指标，成交额排序
select s.stock_name, a.* from sustain_high_vol(3, 5) as a
                                  join stock as s on a.stock_code = s.stock_code
where ratio >= 2
order by recent_amount desc


select count(*) from k_line_2020_2039 where date = '2021-10-15'

select count(*) from daily_indicator where date = '2021-10-11'

-- 最近换手与历史换手比值， 说明股价开始异动
select * from data_statistic(3, 5)
where rec_avg_turn_f >= 1
  and turn_ratio >= 2
  and rec_avg_amount >= 1e8
order by turn_ratio desc;

select * from data_statistic(3, 5)
where rec_avg_turn_f >= 2
  and rec_avg_amount >= 1e8
order by turn_ratio desc;

-- 价格指标 最近增幅与过去增幅的比值，越大说明股票价格开始抬头向上
select * from data_statistic(3, 10)
order by pct_change_ratio desc;

select * from stock;

select * from score;

select * from stock_score order by date desc

-- 2021-09-29
-- 量价齐指标
-- 根据股票得分大小排序
select ss.stock_code,
       max(st.stock_name) as stock_name,
       sum(s.score) as score,
       max(di.turnover_rate_f) as turn
from stock_score ss
         join score s on ss.score_code = s.score_code
         join stock st on ss.stock_code = st.stock_code
         join daily_indicator di on ss.stock_code = di.stock_code and ss.date = di.date
where ss.date = '2021-10-11'
-- and s.type in ('turn_ratio', 'increase_ratio')
group by ss.stock_code
order by score desc, turn desc

select * from stock_score where score_code = 'minimum_price_support(recDays=3)'

select * from score;

select * from stock_score order by date desc

delete from stock_score where date = '2021-10-11'

select * from daily_indicator order by date desc limit 100

--2021-10-18
-- 成交额比值
select * from data_statistic(3, 5, '')


select date from daily_indicator group by date order by date desc limit 10;

--2021-11-07
-- 最近换手与历史换手比值， 说明股价开始异动
select * from data_statistic(3, 5)
where rec_avg_turn_f >= 1
  and turn_ratio >= 2
--   and rec_avg_amount >= 1e8
order by turn_ratio desc;
-- order by rec_avg_amount desc

-- 量价齐指标
-- 根据股票得分大小排序
select ss.stock_code,
       max(st.stock_name) as stock_name,
       sum(s.score) as score,
       max(di.turnover_rate_f) as turn
from stock_score ss
         join score s on ss.score_code = s.score_code
         join stock st on ss.stock_code = st.stock_code
         join daily_indicator di on ss.stock_code = di.stock_code and ss.date = di.date
where ss.date = '2021-12-24'
-- and s.type in ('turn_ratio', 'increase_ratio')
group by ss.stock_code
order by score desc, turn desc

-- 2021-12-26
-- 最低价支撑
with rec_dt as ( -- 最近日期
    select date
    from k_line_2020_2039
    where date < '2021-12-24'
    group by date
    order by date desc
    limit 1
), min_k as (
    select stock_code, min(k.low) as min_price
    from k_line_2020_2039 k
             join rec_dt d on k.date = d.date
    group by stock_code
)
-- select * from min_k where stock_code = '300002'
select k.stock_code from k_line_2020_2039 k
                             join min_k on k.stock_code = min_k.stock_code
    and k.low >= min_k.min_price
    and k.date = '2021-12-24'
-- where k.stock_code = '300002'
where k.stock_code = '300002';

select * from stock_score where  stock_code = '300026';

-- delete from stock_score where date = '2021-12-24'