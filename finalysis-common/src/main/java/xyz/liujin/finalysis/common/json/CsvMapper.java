package xyz.liujin.finalysis.common.json;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;
import reactor.util.function.Tuple2;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 *
 */
@Data
public class CsvMapper {
    /**
     * 标题行在 JSON 中的位置；如 /data/fields
     */
    private JsonMapper fieldPath;
    /**
     * 记录在 JSON 中的位置；如 /data/items
     */
    private JsonMapper itemsPath;
    /**
     * 需要映射成的 JSON 结构
     */
    private Map<String, ?> mapper;

    // csv 标题和索引对应关系， eval 方法执行后才可用
    private Map<String, Long> fieldIndex;

    private static CsvMapper EMPTY_MAPPER = new CsvMapper(null, null, null) {
        @Override
        public <T> Flux<T> eval(Map<String, ?> json, Class<T> clazz) {
            return Flux.just();
        }
    };

    private CsvMapper(@Nullable String fieldPath, @Nullable String itemsPath, @Nullable Map<String, ?> mapper) {
        this.fieldPath = JsonMapper.parseExpr(fieldPath);
        this.itemsPath = JsonMapper.parseExpr(itemsPath);
        this.mapper = parseMap(mapper);
    }

    public static CsvMapper parse(@Nullable String fieldPath, @Nullable String itemsPath, @Nullable Reader reader) {
        if (Objects.isNull(reader)) {
            return EMPTY_MAPPER;
        }
        String mapper = IoUtil.read(reader);
        return parse(fieldPath, itemsPath, mapper);
    }

    public static CsvMapper parse(@Nullable String fieldPath, @Nullable String itemsPath, @Nullable String jsonMapper) {
        return parse(fieldPath, itemsPath, JSONUtil.parseObj(jsonMapper));
    }

    public static CsvMapper parse(@Nullable String fieldPath, @Nullable String itemsPath, @Nullable Map<String, ?> mapper) {
        return new CsvMapper(fieldPath, itemsPath, mapper);
    }

    /**
     * 提取 csv 中记录
     * @param csvJson csv 格式的 json
     *      格式示例
     *      {
     *          "fields": ["name", "price", "author", "sum"],
     *          "items": [
     *                ["RHEL", "21.23", "hm", "998"],
     *                ["ubuntu", "33.44", "ut", "899"],
     *                ["xiao", "22.88", "x", "699"]
     *          ]
     *      }
     * @param cls csv 记录需要转成的实体 class
     * @param <T> 实体
     * @return
     */
    public <T> Flux<T> eval(String csvJson, Class<T> cls) {
        Map<String, ?> csvMap = JSONUtil.toBean(csvJson, new TypeReference<>() {}, false);
        return eval(csvMap, cls);
    }

    /**
     *
     * @param json json 格式 csv, 需要指明字段，和数据记录所在位置
     * @return
     */
    public <T> Flux<T> eval(Map<String, ?> json, Class<T> clazz) {
        // 初始化字段索引对应关系
        List<String> fields = (List<String>) fieldPath.eval(json);
        fieldIndex = Flux.fromIterable(fields)
                .index()
                .collectMap(Tuple2::getT2, Tuple2::getT1)
                .block();

        List<List<?>> items = (List<List<?>>) itemsPath.eval(json);
        return Flux.fromIterable(items)
                .flatMap(item -> Flux.fromIterable(mapper.entrySet())
                        .map(entry -> {
                            Object v = entry.getValue();
                            if (v instanceof Node) {
                                v = ((Node) v).get(item);
                            }
                            return Pair.of(entry.getKey(), v);
                        })
                        .collectMap(Pair::getKey, Pair::getValue)
                        .map(map -> JSONUtil.toBean(JSONUtil.parseObj(map), clazz)));
    }



    private Map<String, ?> parseMap(Map<String, ?> map) {
        return Mono.justOrEmpty(map)
                .flatMapIterable(Map::entrySet)
                .map(entry -> Pair.of(entry.getKey(), parseExpr((String) entry.getValue())))
                .collectMap(Pair::getKey, Pair::getValue)
                .block();
    }

    /**
     * 实际的解析逻辑
     * /name 获取 csv 中标题名为 name 对应的值
     * /1    获取 csv 中第 2 个索引位置的值
     * @param pathExpr
     * @return
     */
    private Node parseExpr(String pathExpr) {
        return null;
    }

    // 判断 value 是否是一个表达式；nonNull && isString && startWith("/")
    private boolean isExpr(Object value) {
        if (Objects.nonNull(value)
                && value instanceof String
                && ((String) value).startsWith("/")) {
            return true;
        }
        // 或者本身就是一个路径表达式
        if (Objects.nonNull(value) && value instanceof Node) {
            return true;
        }

        return false;
    }

    /**
     * 表示一个名字或索引节点，用来获取 CSV 记录中某个位置的值
     */
    interface Node {
        /**
         * 值 Node, 翻译原值
         * @param obj
         * @return
         */
        static Node literal(Object obj) {
            return item -> obj;
        }

        /**
         * 从一行记录 item 中获取值
         * @param item
         * @return
         */
        Object get(List<?> item);
    }

    @Data
    private class IndexNode implements Node{
        private int index;

        public IndexNode(int index) {
            this.index = index;
        }

        public Object get(List<?> item) {
            return item.get(index);
        }
    }

    @Data
    private class NameNode implements Node {
        private String name;

        public NameNode(String name) {
            this.name = name;
        }

        public Object get(List<?> item) {
            return Optional.ofNullable(fieldIndex)
                    .map(it -> it.get(name))
                    .map(Long::intValue)
                    .map(item::get)
                    .orElse(null);
        }
    }

}
