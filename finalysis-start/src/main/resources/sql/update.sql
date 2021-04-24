-- 2021-01-01
create table if not exists stock (
    stock_code varchar(6) not null,
    stock_name varchar(32) not null default '',
    board smallint not null default 0,
    stat smallint not null default 0
);

alter table stock add constraint stock_pk primary key (stock_code);

comment on table stock is '沪深股票信息';
comment on column stock.stock_code is '股票代码';
comment on column  stock.stock_name is '股票名称';
comment on column stock.board is '交易板块。0 未知；1 沪 A；2 深 A；3 创业板；4 科创板';
comment on column stock.stat is '股票状态。-1 退市；0 正常；1 融资融券；2 ST';

-- 股票日 k 数据
create table if not exists k_line (
    id bigserial,
    stock_code varchar(6) not null,
    date_time timestamp(0) with time zone not null default now(),
    open decimal(7, 2) not null default 0.00,
    close decimal(7, 2) not null default 0.00,
    high decimal(7, 2) not null default 0.00,
    low decimal(7, 2) not null default 0.00,
    inc decimal(7, 2) not null default 0.00,
    inc_rate decimal(6, 2) not null default 0.00,
    volume integer not null default 0,
    amount decimal(14, 2) not null default 0.00,
    volume_ratio decimal(6, 2) not null default 0.00,
    turn decimal(6, 2) not null default 0.00,
    committee decimal(5, 2) not null default 0.00,
    selling decimal(14, 2) not null default 0.00,
    buying decimal(14, 2) not null default 0.00
);

alter table k_line add constraint pk_k_line primary key (id);
create unique index uk_k_line_stock_code_date_time on k_line(stock_code, date_time);

comment on table k_line is '日 K 线';
comment on column k_line.id is '自增主键';
comment on column k_line.stock_code is '股票代码';
comment on column k_line.date_time is '日期';
comment on column k_line.open is '开盘价';
comment on column k_line.close is '收盘价';
comment on column k_line.high is '最高价';
comment on column k_line.low is '最低价';
comment on column k_line.inc is '增量';
comment on column k_line.inc_rate is '增幅';
comment on column k_line.volume is '成交量';
comment on column k_line.amount is '成交额';
comment on column k_line.volume_ratio is '量比';
comment on column k_line.turn is '换手率';
comment on column k_line.committee is '委比';
comment on column k_line.buying is '买盘/内盘';
comment on column k_line.selling is '卖盘/外盘';

-- 2021-01-06
-- 修改列名符合常规
alter table k_line rename inc to change;
alter table k_line rename inc_rate to pct_change;

-- 2021-01-08
-- 成交量改为股数，int 不够存储
alter table k_line alter column volume type bigint;

-- 添加单位说明
comment on column k_line.volume is '成交量（股）';
comment on column k_line.amount is '成交额（元）';

-- 2021-01-11
alter table stock add column listing_date date not null default now();

-- 2021-01-15
-- 修改列类型
alter table k_line alter column date_time type date;
alter table k_line rename column date_time to date;

-- 重命名索引
drop index if exists uk_k_line_stock_code_date_time;
create unique index uk_k_line_stock_code_date on k_line(stock_code, date);

-- 2021-01-16
-- k_line 创建分区表
create table k_line_1990_2019 (like k_line including defaults including constraints);
create table k_line_2020_2039 (like k_line including defaults including constraints);

-- 2020-01-17
-- check 约束，避免加入分区时全表扫描
alter table k_line_1990_2019 add constraint ck_k_line_1990_2019_date
    check ( date >= date '1990-01-01' and date < date '2020-01-01');

alter table k_line_1990_2019 add constraint pk_k_line_1990_2019 primary key (id);
create unique index uk_k_line_1990_2019_stock_code_date on k_line_1990_2019(stock_code, date);

-- 加入 k_line 分区
alter table k_line attach partition k_line_1990_2019
    for values from ('1990-01-01') to ('2020-01-01');

-- check 约束，避免加入分区时全表扫描
alter table k_line_2020_2039 add constraint ck_k_line_2020_2039
    check ( date >= date '2020-01-01' and date < date '2040-01-01');

alter table k_line_2020_2039 add constraint pk_k_line_2020_2039 primary key (id);

create unique index uk_k_line_2020_2039_stock_code_date on k_line_2020_2039(stock_code, date);

-- 加入 k_line 分区
alter table k_line attach partition k_line_2020_2039
    for values from ('2020-01-01') to ('2040-01-01');

-- 2021-02-20
-- 重构 avg_line 均线表
create table if not exists avg_line (
    id bigserial,
    stock_code varchar(6) not null,
    date date not null default now(),
    count integer not null default 0,
    current decimal(7, 2) not null default 0,
    avg decimal(7, 2) not null default 0
);

comment on table avg_line is '均线数据';
comment on column avg_line.id is '自增主键';
comment on column avg_line.stock_code is '股票代码。如 000001';
comment on column avg_line.date is '日期';
comment on column avg_line.count is '计算均线天数';
comment on column avg_line.current is '当日价格';
comment on column avg_line.avg is 'count 均值';

-- 批量插入数据

alter table avg_line add constraint pk_avg_line primary key (id);
create unique index uk_avg_line_stock_code_date_count on avg_line (stock_code, date, count);

-- 2021-03-13
-- 修改均线表字段名，count -> statistic
alter table avg_line rename column count to statistic;

drop index uk_avg_line_stock_code_date_count;
create unique index uk_avg_line_stock_code_date_statistic on avg_line (stock_code, date, statistic);

-- 2021-04-24
-- 均线表分开计算

-- 股票 5 日均线表
create table if not exists avg_line_5 (
    id bigserial,
    stock_code varchar(6) not null,
    date date not null default now(),
    current decimal(7, 2) not null default 0,
    avg decimal(7, 2) not null default 0
);

comment on table avg_line_5 is '均线数据';
comment on column avg_line_5.id is '自增主键';
comment on column avg_line_5.stock_code is '股票代码。如 000001';
comment on column avg_line_5.date is '日期';
comment on column avg_line_5.current is '当日价格';
comment on column avg_line_5.avg is 'count 均值';

alter table avg_line_5 add constraint pk_avg_line_5 primary key (id);
create unique index uk_avg_line_5_stock_code_date on avg_line_5 (stock_code, date);

-- 从 avg_line 中提取 5 日均线
insert into avg_line_5(stock_code, date, current, avg)
select stock_code, date, current, avg from avg_line where statistic = 5;

select count(*) from avg_line_5;


-- 股票 10 日均线表
create table if not exists avg_line_10 (
    id bigserial,
    stock_code varchar(6) not null,
    date date not null default now(),
    current decimal(7, 2) not null default 0,
    avg decimal(7, 2) not null default 0
);

comment on table avg_line_10 is '均线数据';
comment on column avg_line_10.id is '自增主键';
comment on column avg_line_10.stock_code is '股票代码。如 000001';
comment on column avg_line_10.date is '日期';
comment on column avg_line_10.current is '当日价格';
comment on column avg_line_10.avg is 'count 均值';

alter table avg_line_10 add constraint pk_avg_line_10 primary key (id);
create unique index uk_avg_line_10_stock_code_date on avg_line_10 (stock_code, date);

-- 从 avg_line 中提取 10 日均线
insert into avg_line_10(stock_code, date, current, avg)
select stock_code, date, current, avg from avg_line where statistic = 10;

select count(*) from avg_line_10;


-- 股票 20 日均线表
create table if not exists avg_line_20 (
    id bigserial,
    stock_code varchar(6) not null,
    date date not null default now(),
    current decimal(7, 2) not null default 0,
    avg decimal(7, 2) not null default 0
);

comment on table avg_line_20 is '均线数据';
comment on column avg_line_20.id is '自增主键';
comment on column avg_line_20.stock_code is '股票代码。如 000001';
comment on column avg_line_20.date is '日期';
comment on column avg_line_20.current is '当日价格';
comment on column avg_line_20.avg is 'count 均值';

alter table avg_line_20 add constraint pk_avg_line_20 primary key (id);
create unique index uk_avg_line_20_stock_code_date on avg_line_20 (stock_code, date);

-- 从 avg_line 中提取 20 日均线
insert into avg_line_20(stock_code, date, current, avg)
select stock_code, date, current, avg from avg_line where statistic = 20;

select count(*) from avg_line_20;


-- 股票 30 日均线表
create table if not exists avg_line_30 (
    id bigserial,
    stock_code varchar(6) not null,
    date date not null default now(),
    current decimal(7, 2) not null default 0,
    avg decimal(7, 2) not null default 0
);

comment on table avg_line_30 is '均线数据';
comment on column avg_line_30.id is '自增主键';
comment on column avg_line_30.stock_code is '股票代码。如 000001';
comment on column avg_line_30.date is '日期';
comment on column avg_line_30.current is '当日价格';
comment on column avg_line_30.avg is 'count 均值';

alter table avg_line_30 add constraint pk_avg_line_30 primary key (id);
create unique index uk_avg_line_30_stock_code_date on avg_line_30 (stock_code, date);

-- 从 avg_line 中提取 30 日均线
insert into avg_line_30(stock_code, date, current, avg)
select stock_code, date, current, avg from avg_line where statistic = 30;

select count(*) from avg_line_30;

