package xyz.liujin.finalysis.analysis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;
import xyz.liujin.finalysis.analysis.entity.TagScore;
import xyz.liujin.finalysis.analysis.mapper.TagScoreMapper;
import xyz.liujin.finalysis.analysis.tag.Tagable;
import xyz.liujin.finalysis.base.util.MyLogger;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class TagScoreService extends ServiceImpl<TagScoreMapper, TagScore> implements IService<TagScore> {
    private static final MyLogger logger = MyLogger.getLogger(TagScoreService.class);

    private Cache<String, TagScore> cache = Caffeine.newBuilder()
            .expireAfterWrite(12, TimeUnit.HOURS)
            .build();

    /**
     * 获取计分标签对象
     * 将标签入库或更新
     * @param tagable 需要入库或更新的标签对象
     * @return 标签
     */
    public TagScore getTag(Tagable tagable) {
        TagScore tagScore = tagable.getTag();
        return cache.get(tagScore.getTag(), k -> {
            logger.debug("get tag from db", "tagScore", tagScore);
            TagScore exist = getById(k);
            if (Objects.isNull(exist)) {
                logger.debug("insert into db");
                save(tagScore);
                return tagScore;
            }

            // 字段值有变动，需要更新
            if (!Objects.equals(tagScore.getScore(), exist.getScore())) {
                tagScore.setUpdateTime(OffsetDateTime.now());
                logger.debug("update tag", "old", exist, "new", tagScore);
                updateById(tagScore);
            }
            return tagScore;
        });
    }

    public void saveIfNotExist(TagScore tagScore) {
        TagScore exist = getById(tagScore.getTag());
        // 新增
        if (Objects.isNull(exist)) {
            save(tagScore);
        }
    }
}
