package xyz.liujin.finalysis.spider.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "k_line", schema = "public", catalog = "stock")
@DynamicInsert
@DynamicUpdate // 字段为 null 更新对应的字段
@Data
public class KLine {
    @Id
    @Column(name = "stock_code")
    private Integer stockCode;

    @Basic
    @Column(name = "start_time")
    private Timestamp startTime;

    @Basic
    @Column(name = "end_time")
    private Timestamp endTime;

    @Basic
    @Column(name = "open")
    private BigDecimal open;

    @Basic
    @Column(name = "close")
    private BigDecimal close;

    @Basic
    @Column(name = "high")
    private BigDecimal high;

    @Basic
    @Column(name = "low")
    private BigDecimal low;

    @Basic
    @Column(name = "volume")
    private Integer volume;

    @Basic
    @Column(name = "turnover")
    private BigDecimal turnover;

    @Basic
    @Column(name = "volume_ratio")
    private BigDecimal volumeRatio;

    @Basic
    @Column(name = "turnover_rate")
    private BigDecimal turnoverRate;

    @Basic
    @Column(name = "committee")
    private BigDecimal committee;

    @Basic
    @Column(name = "selling")
    private BigDecimal selling;

    @Basic
    @Column(name = "buying")
    private BigDecimal buying;

}
