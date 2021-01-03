-- 2020-01-03
-- 增加主键列，用于 k_line 表查询更新
alter table k_line add column id bigserial;
alter table k_line add constraint pk_k_line primary key (id);