package xyz.liujin.finalysis.common.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import reactor.util.annotation.Nullable;
import xyz.liujin.finalysis.common.json.CsvMapper;
import xyz.liujin.finalysis.common.json.JsonMapper;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * json 工具类
 */
public class JsonUtils {
    /**
     * 从 readerMapper 中读取 mapper
     * @param readerMapper
     * {
     *     "name": "/path/to/name",
     *     "age": "/path/to/age"
     * }
     * @return
     */
    public static JsonMapper jsonMapper(@Nullable Reader readerMapper) {
        if (Objects.isNull(readerMapper)) {
            return JsonMapper.parseMap(null);
        }
        String mapperStr = IoUtil.read(readerMapper);
        Map<String, ?> jsonMapper = JSONUtil.parseObj(mapperStr);
        return jsonMapper(jsonMapper);
    }

    public static JsonMapper jsonMapper(@Nullable String pathExpr) {
        return JsonMapper.parseExpr(pathExpr);
    }

    public static JsonMapper jsonMapper(@Nullable Map<String, ?> map) {
        return JsonMapper.parseMap(map);
    }

    public static JsonMapper jsonMapper(@Nullable List<?> list) {
        return JsonMapper.parseList(list);
    }

    public static CsvMapper csvMapper(@Nullable String itemsPath, @Nullable Reader readerMapper) {
        return csvMapper(null, itemsPath, readerMapper);
    }

    /**
     * 根据 mapper 从 csv 格式 json 中提取数据
     * @param readerMapper 从 Reader 中获取 Mapper
     */
    public static CsvMapper csvMapper(@Nullable String fieldPath, @Nullable String itemsPath, @Nullable Reader readerMapper) {
        if (Objects.isNull(readerMapper)) {
            return CsvMapper.EMPTY_MAPPER;
        }
        String mapper = IoUtil.read(readerMapper);
        return csvMapper(fieldPath, itemsPath, mapper);
    }

    /**
     * 根据 mapper 从 csv 格式 json 中提取数据
     * @param jsonMapper
     *      {
     *          "bookName": "/name",
     *          "price": "/price",
     *          "sum": "/count
     *      }
     */
    public static CsvMapper csvMapper(@Nullable String fieldPath, @Nullable String itemsPath, @Nullable String jsonMapper) {
        return csvMapper(fieldPath, itemsPath, JSONUtil.parseObj(jsonMapper));
    }

    /**
     * 根据 mapper 从 csv 格式 json 中提取数据
     */
    public static CsvMapper csvMapper(@Nullable String fieldPath, @Nullable String itemsPath, @Nullable Map<String, ?> mapper) {
        return CsvMapper.create(fieldPath, itemsPath, mapper);
    }
}
