drop table if exists stock;
create table stock (
    stock_code varchar(6) not null,
    stock_name varchar(32) not null default '',
    board smallint not null default 0,
    stat smallint not null default 0,
    listing_date date not null default now()
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
	date date not null default now(),
	open decimal(7, 2) not null default 0.00,
	close decimal(7, 2) not null default 0.00,
	high decimal(7, 2) not null default 0.00,
	low decimal(7, 2) not null default 0.00,
    change decimal(7, 2) not null default 0.00,
    pct_change decimal(6, 2) not null default 0.00,
	volume bigint not null default 0,
	amount decimal(14, 2) not null default 0.00,
	volume_ratio decimal(6, 2) not null default 0.00,
	turn decimal(6, 2) not null default 0.00,
	committee decimal(5, 2) not null default 0.00,
	selling decimal(14, 2) not null default 0.00,
	buying decimal(14, 2) not null default 0.00
) partition by range (date);

comment on table k_line is '日 K 线';
comment on column k_line.id is '自增主键';
comment on column k_line.stock_code is '股票代码';
comment on column k_line.date is '日期';
comment on column k_line.open is '开盘价';
comment on column k_line.close is '收盘价';
comment on column k_line.high is '最高价';
comment on column k_line.low is '最低价';
comment on column k_line.change is '增量';
comment on column k_line.pct_change is '增幅';
comment on column k_line.volume is '成交量（股）';
comment on column k_line.amount is '成交额（元）';
comment on column k_line.volume_ratio is '量比';
comment on column k_line.turn is '换手率';
comment on column k_line.committee is '委比';
comment on column k_line.buying is '买盘/内盘';
comment on column k_line.selling is '卖盘/外盘';

-- 创建分区
create table k_line_1990_2019 partition of k_line
for values from ('1990-01-01') to ('2020-01-01');

alter table k_line_1990_2019 add constraint pk_k_line_1990_2019 primary key (id);
create unique index uk_k_line_1990_2019_stock_code_date on k_line_1990_2019(stock_code, date);

create table k_line_2020_2039 partition of k_line
for values from ('2020-01-01') to ('2039-01-01');

alter table k_line_2020_2039 add constraint pk_k_line_2020_2039 primary key (id);
create unique index uk_k_line_2020_2039_stock_code_date on k_line_2020_2039(stock_code, date);

-- 股票日均线表
drop table if exists avg_line;
create table avg_line (
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

alter table avg_line add constraint pk_avg_line primary key (id);
create unique index uk_avg_line_stock_code_date_count on avg_line (stock_code, date, count);