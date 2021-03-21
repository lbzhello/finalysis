-- 计算股票某天（day）量比
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