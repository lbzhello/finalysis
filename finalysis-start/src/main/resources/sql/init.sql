-- 测试表 ignore
drop table if exists hello_world;
create table if not exists hello_world
(
    id      bigserial primary key,
    code    integer      not null default 0,
    date    date         not null default now(),
    message varchar(256) not null default ''
);

create table if not exists stock
(
    stock_code   varchar(6)  not null,
    stock_name   varchar(32) not null default '',
    board        smallint    not null default 0,
    stat         smallint    not null default 0,
    listing_date date        not null default now()
);

alter table stock
    add constraint stock_pk primary key (stock_code);

comment on table stock is '沪深股票信息';
comment on column stock.stock_code is '股票代码';
comment on column stock.stock_name is '股票名称';
comment on column stock.board is '交易板块。0 未知；1 沪 A；2 深 A；3 创业板；4 科创板';
comment on column stock.stat is '股票状态。-1 退市；0 正常；1 融资融券；2 ST';

-- 股票日 k 数据
create table if not exists k_line
(
    id           bigserial,
    stock_code   varchar(6)     not null,
    date         date           not null default now(),
    open         decimal(7, 2)  not null default 0.00,
    close        decimal(7, 2)  not null default 0.00,
    high         decimal(7, 2)  not null default 0.00,
    low          decimal(7, 2)  not null default 0.00,
    change       decimal(7, 2)  not null default 0.00,
    pct_change   decimal(6, 2)  not null default 0.00,
    volume       bigint         not null default 0,
    amount       decimal(14, 2) not null default 0.00,
    volume_ratio decimal(6, 2)  not null default 0.00,
    turn         decimal(6, 2)  not null default 0.00,
    committee    decimal(5, 2)  not null default 0.00,
    selling      decimal(14, 2) not null default 0.00,
    buying       decimal(14, 2) not null default 0.00
) partition by range (date);

comment on table k_line is '日 K 线';
comment on column k_line.id is '自增主键';
comment on column k_line.stock_code is '股票代码';
comment on column k_line.date is '日期';
comment on column k_line.open is '开盘价（元）';
comment on column k_line.close is '收盘价（元）';
comment on column k_line.high is '最高价（元）';
comment on column k_line.low is '最低价（元）';
comment on column k_line.change is '增量（元）';
comment on column k_line.pct_change is '增幅（%）';
comment on column k_line.volume is '成交量（股）';
comment on column k_line.amount is '成交额（元）';
comment on column k_line.volume_ratio is '量比';
comment on column k_line.turn is '换手率（%）';
comment on column k_line.committee is '委比（%）';
comment on column k_line.buying is '买盘/内盘（股）';
comment on column k_line.selling is '卖盘/外盘（股）';

-- 创建分区
create table k_line_1990_2019 partition of k_line
    for values from ('1990-01-01') to ('2020-01-01');

alter table k_line_1990_2019
    add constraint pk_k_line_1990_2019 primary key (id);

-- create unique index uk_k_line_1990_2019_stock_code_date on k_line_1990_2019 (stock_code, date);
create unique index uk_k_line_1990_2019_date_stock_code on k_line_1990_2019 (date, stock_code);

create table k_line_2020_2039 partition of k_line
    for values from ('2020-01-01') to ('2039-01-01');

alter table k_line_2020_2039
    add constraint pk_k_line_2020_2039 primary key (id);

-- create unique index uk_k_line_2020_2039_stock_code_date on k_line_2020_2039 (stock_code, date);
create unique index uk_k_line_2020_2039_date_stock_code on k_line_2020_2039 (date, stock_code);

-- 股票日均线表
create table if not exists avg_line
(
    id         bigserial,
    stock_code varchar(6)    not null,
    date       date          not null default now(),
    statistic  integer       not null default 0,
    current    decimal(7, 2) not null default 0,
    avg        decimal(7, 2) not null default 0
);

comment on table avg_line is '均线数据';
comment on column avg_line.id is '自增主键';
comment on column avg_line.stock_code is '股票代码。如 000001';
comment on column avg_line.date is '日期';
comment on column avg_line.statistic is '统计天数';
comment on column avg_line.current is '当日价格';
comment on column avg_line.avg is 'count 均值';

alter table avg_line
    add constraint pk_avg_line primary key (id);

-- create unique index uk_avg_line_stock_code_date_statistic on avg_line (stock_code, date, statistic);
create unique index uk_avg_line_date_stock_code_statistic on avg_line (date, stock_code, statistic);

-- 2021-04-24
-- 不同日期均线分割在不同表中

-- 股票 5 日均线表
create table if not exists avg_line_5
(
    id         bigserial,
    stock_code varchar(6)    not null,
    date       date          not null default now(),
    current    decimal(7, 2) not null default 0,
    avg        decimal(7, 2) not null default 0
);

comment on table avg_line_5 is '均线数据';
comment on column avg_line_5.id is '自增主键';
comment on column avg_line_5.stock_code is '股票代码。如 000001';
comment on column avg_line_5.date is '日期';
comment on column avg_line_5.current is '当日价格';
comment on column avg_line_5.avg is 'count 均值';

alter table avg_line_5
    add constraint pk_avg_line_5 primary key (id);

-- create unique index uk_avg_line_5_stock_code_date on avg_line_5 (stock_code, date);
create unique index uk_avg_line_5_date_stock_code on avg_line_5 (date, stock_code);

-- 股票 10 日均线表
create table if not exists avg_line_10
(
    id         bigserial,
    stock_code varchar(6)    not null,
    date       date          not null default now(),
    current    decimal(7, 2) not null default 0,
    avg        decimal(7, 2) not null default 0
);

comment on table avg_line_10 is '均线数据';
comment on column avg_line_10.id is '自增主键';
comment on column avg_line_10.stock_code is '股票代码。如 000001';
comment on column avg_line_10.date is '日期';
comment on column avg_line_10.current is '当日价格';
comment on column avg_line_10.avg is 'count 均值';

alter table avg_line_10
    add constraint pk_avg_line_10 primary key (id);

-- create unique index uk_avg_line_10_stock_code_date on avg_line_10 (stock_code, date);
create unique index uk_avg_line_10_date_stock_code on avg_line_10 (date, stock_code);

-- 股票 20 日均线表
create table if not exists avg_line_20
(
    id         bigserial,
    stock_code varchar(6)    not null,
    date       date          not null default now(),
    current    decimal(7, 2) not null default 0,
    avg        decimal(7, 2) not null default 0
);

comment on table avg_line_20 is '均线数据';
comment on column avg_line_20.id is '自增主键';
comment on column avg_line_20.stock_code is '股票代码。如 000001';
comment on column avg_line_20.date is '日期';
comment on column avg_line_20.current is '当日价格';
comment on column avg_line_20.avg is 'count 均值';

alter table avg_line_20
    add constraint pk_avg_line_20 primary key (id);

-- create unique index uk_avg_line_20_stock_code_date on avg_line_20 (stock_code, date);
create unique index uk_avg_line_20_date_stock_code on avg_line_20 (date, stock_code);

-- 股票 30 日均线表
create table if not exists avg_line_30
(
    id         bigserial,
    stock_code varchar(6)    not null,
    date       date          not null default now(),
    current    decimal(7, 2) not null default 0,
    avg        decimal(7, 2) not null default 0
);

comment on table avg_line_30 is '均线数据';
comment on column avg_line_30.id is '自增主键';
comment on column avg_line_30.stock_code is '股票代码。如 000001';
comment on column avg_line_30.date is '日期';
comment on column avg_line_30.current is '当日价格';
comment on column avg_line_30.avg is 'count 均值';

alter table avg_line_30
    add constraint pk_avg_line_30 primary key (id);

-- create unique index uk_avg_line_30_stock_code_date on avg_line_30 (stock_code, date);
create unique index uk_avg_line_30_date_stock_code on avg_line_30 (date, stock_code);

-- 2021-05-01
-- 均线视图，大区分查询应该基于此视图而不是具体的表
drop view if exists v_avg_line;
create view v_avg_line as
select a5.stock_code,
       a5.date,
       a5.current,
       a5.avg  avg5,
       a10.avg avg10,
       a20.avg avg20,
       a30.avg avg30
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

comment on view v_avg_line is '均线视图';
comment on column v_avg_line.stock_code is '股票代码';
comment on column v_avg_line.date is '日期';
comment on column v_avg_line.current is '当日加个，当日收盘价';
comment on column v_avg_line.avg5 is '5 日均线';
comment on column v_avg_line.avg10 is '10 日均线';
comment on column v_avg_line.avg20 is '20 日均线';
comment on column v_avg_line.avg30 is '30 日均线';

-- 2021-05-04
-- 股票日指标数据
drop table if exists daily_indicator;
create table if not exists daily_indicator
(
    id              bigserial,
    stock_code      varchar(6)     not null default '',
    date            date           not null default now(),
    close           decimal(7, 2)  not null default 0.00,
    turnover_rate   decimal(7, 2)  not null default 0.00,
    turnover_rate_f decimal(7, 2)  not null default 0.00,
    volume_ratio    decimal(6, 2)  not null default 0.00,
    pe              decimal(8, 2)  not null default 0.00,
    pe_ttm          decimal(8, 2)  not null default 0.00,
    pb              decimal(9, 2)  not null default 0.00,
    ps              decimal(9, 2)  not null default 0.00,
    ps_ttm          decimal(9, 2)  not null default 0.00,
    dv_ratio        decimal(7, 2)  not null default 0.00,
    dv_ttm          decimal(7, 2)  not null default 0.00,
    total_share     decimal(16, 2) not null default 0.00,
    float_share     decimal(16, 2) not null default 0.00,
    free_share      decimal(16, 2) not null default 0.00,
    total_mv        decimal(16, 2) not null default 0.00,
    circ_mv         decimal(16, 2) not null default 0.00
);

comment on table daily_indicator is '股票日指标';
comment on column daily_indicator.id is '自增主键';
comment on column daily_indicator.stock_code is 'TS股票代码';
comment on column daily_indicator.date is '交易日期';
comment on column daily_indicator.close is '当日收盘价';
comment on column daily_indicator.turnover_rate is '换手率（%）';
comment on column daily_indicator.turnover_rate_f is '换手率（自由流通股）';
comment on column daily_indicator.volume_ratio is '量比';
comment on column daily_indicator.pe is '市盈率（总市值/净利润， 亏损的PE为空）';
comment on column daily_indicator.pe_ttm is '市盈率（TTM，亏损的PE为空）';
comment on column daily_indicator.pb is '市净率（总市值/净资产）';
comment on column daily_indicator.ps is '市销率';
comment on column daily_indicator.ps_ttm is '市销率（TTM）';
comment on column daily_indicator.dv_ratio is '股息率 （%）';
comment on column daily_indicator.dv_ttm is '股息率（TTM）（%）';
comment on column daily_indicator.total_share is '总股本 （股）';
comment on column daily_indicator.float_share is '流通股本 （股）';
comment on column daily_indicator.free_share is '自由流通股本 （股）';
comment on column daily_indicator.total_mv is '总市值 （元）';
comment on column daily_indicator.circ_mv is '流通市值（元）';

alter table daily_indicator
    add constraint pk_daily_indicator primary key (id);

create unique index uk_daily_indicator_date_stock_code on daily_indicator (date, stock_code);

-- 每日推荐股票
drop table if exists recommend;
create table if not exists recommend
(
    id              bigserial,
    date            date           not null default now(),
    stock_code      varchar(6)     not null default '',
    vol_amount      decimal(24, 2) not null default 0
);

comment on table recommend is '每日股票推荐表';
comment on column recommend.id is '自增主键';
comment on column recommend.date is '交易日期';
comment on column recommend.stock_code is '股票代码';
comment on column recommend.vol_amount is '量额，成交量和成交额的乘积';

alter table recommend add constraint pk_recommend primary key (id);

create unique index uk_recommend_date_stock_code on recommend(date, stock_code);