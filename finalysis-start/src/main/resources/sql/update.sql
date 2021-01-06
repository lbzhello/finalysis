-- 2021-01-01
drop table if exists stock;
create table stock (
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
drop table if exists k_line;
create table k_line (
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