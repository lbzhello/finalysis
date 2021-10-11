package xyz.liujin.finalysis.analysis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.checkerframework.checker.nullness.qual.PolyNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import xyz.liujin.finalysis.analysis.entity.Score;
import xyz.liujin.finalysis.analysis.mapper.ScoreMapper;
import xyz.liujin.finalysis.analysis.score.ScoreUtil;
import xyz.liujin.finalysis.analysis.score.Scoreable;
import xyz.liujin.finalysis.analysis.score.annotation.ScoreConfig;
import xyz.liujin.finalysis.base.util.MyLogger;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class ScoreService extends ServiceImpl<ScoreMapper, Score> implements IService<Score> {
    private static final MyLogger logger = MyLogger.getLogger(ScoreService.class);

    private Cache<String, Score> cache = Caffeine.newBuilder()
            .expireAfterWrite(12, TimeUnit.HOURS)
            .build();

    /**
     * 计算并获取分数，具有缓存功能
     *
     * 同时更新分数码数据库
     *
     * 目标类 obj 需要带有 {@link ScoreConfig} 注解
     *
     * @param obj 根据 obj 计算分数
     * @return 分数
     *
     * @see ScoreConfig
     * @see xyz.liujin.finalysis.analysis.score.annotation.ScoreField
     * @see Scoreable
     */
    public @Nullable Score getScore(@Nullable Object obj) {
        Score score = ScoreUtil.calculateScore(obj);
        if (Objects.isNull(score)) {
            return null;
        }

        @PolyNull Score cacheScore = cache.get(score.getScoreCode(), k -> {
            logger.debug("get score from db", "score", score);
            Score exist = getById(k);
            if (Objects.isNull(exist)) {
                logger.debug("insert into db");
                save(score);
                return score;
            }

            return exist;
        });
        // 不能放在 cache lambda 里面更新，会报错：循环更新异常
        updateIfChange(score, cacheScore);

        return score;
    }

    // 字段值有变动，需要更新
    private void updateIfChange(Score score, Score exist) {
        if (!Objects.equals(score.getScore(), exist.getScore())) {
            score.setUpdateTime(OffsetDateTime.now());
            logger.debug("update score", "old", exist, "new", score);
            updateById(score);
            // 清除缓存，下次查询时自动缓存
            cache.invalidate(score.getScoreCode());
        }
    }

    public void saveIfNotExist(Score score) {
        Score exist = getById(score.getScoreCode());
        // 新增
        if (Objects.isNull(exist)) {
            save(score);
        }
    }
}
