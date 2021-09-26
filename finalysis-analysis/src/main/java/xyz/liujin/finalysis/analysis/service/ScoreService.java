package xyz.liujin.finalysis.analysis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.checkerframework.checker.nullness.qual.PolyNull;
import org.springframework.stereotype.Service;
import xyz.liujin.finalysis.analysis.entity.Score;
import xyz.liujin.finalysis.analysis.mapper.ScoreMapper;
import xyz.liujin.finalysis.analysis.score.Scoreable;
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
     * 获取分数
     * 入库或更新，具有缓存功能
     * @param scoreable 需要入库或更新的分数对象
     * @return 分数
     */
    public Score getScore(Scoreable scoreable) {
        Score score = scoreable.getScore();
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
