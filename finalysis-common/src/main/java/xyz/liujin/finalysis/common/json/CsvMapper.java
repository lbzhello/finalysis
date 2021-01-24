package xyz.liujin.finalysis.common.json;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 用于从 csv 格式的 json 中提取数据
 * 例如
 * csv 格式的 json:
 *      {
 *          "fields": ["name", "price", "author", "sum"],
 *          "items": [
 *                ["zhao", "21.23", "lbz", "998"],
 *                ["qian", "33.44", "lbz", "899"],
 *                ["sun", "22.88", "lbz", "699"]
 *          ]
 *      }
 *
 * mapper:
 *      {
 *          "bookName": "/name",
 *          "price": "/price",
 *          "count": "/sum
 *      }
 *
 * 提取出的 JSON 数据:
 *      [
 *          {
 *              "bookName": "zhao",
 *              "price": "21.23",
 *              "count": "998"
 *          }, {
 *              "bookName": "qian",
 *              "price": "33.44",
 *              "count": "899"
 *          }, {
 *              "bookName": "sun",
 *              "price": "22.88",
 *              "count": "699"
 *          }
 *      ]
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

    public static CsvMapper EMPTY_MAPPER = new CsvMapper() {
        @Override
        public <T> Flux<T> eval(Map<String, ?> json, Class<T> clazz) {
            return Flux.just();
        }
    };

    /**
     * 由于未提供 fieldPath 因此 mapper 的表达式只能是数字索引
     * @param itemsPath
     * @param mapper
     * {
     *     "name": "/1",
     *     "age": "/2",
     *     "count": "/3"
     * }
     * @return
     */
    public static CsvMapper create(@Nullable String itemsPath, @Nullable Map<String, ?> mapper) {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.setItemsPath(JsonMapper.parseExpr(itemsPath));
        csvMapper.parseMap(mapper);
        return csvMapper;
    }

    /**
     * 创建一个 Mapper 用于从 CSV 格式 JSON 提取数据
     * @param fieldPath
     * @param itemsPath
     * @param mapper
     *      {
     *          "bookName": "/name",
     *          "price": "/price",
     *          "sum": "/count
     *      }
     */
    public static CsvMapper create(@Nullable String fieldPath, @Nullable String itemsPath, @Nullable Map<String, ?> mapper) {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.setFieldPath(JsonMapper.parseExpr(fieldPath));
        csvMapper.setItemsPath(JsonMapper.parseExpr(itemsPath));
        csvMapper.parseMap(mapper);
        return csvMapper;
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
        // fieldIndex 可以直接提供，若为 null 则从 fieldPath 获取
        if (Objects.nonNull(this.fieldPath)) {
            List<String> fields = (List<String>) this.fieldPath.eval(json);
            this.fieldIndex = Flux.fromIterable(fields)
                    .index()
                    .collectMap(Tuple2::getT2, Tuple2::getT1)
                    .block();
        }

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



    private void parseMap(Map<String, ?> map) {
        this.mapper = Mono.justOrEmpty(map)
                .flatMapIterable(Map::entrySet)
                .map(entry -> Pair.of(entry.getKey(), this.parseExpr((String) entry.getValue())))
                .collectMap(Pair::getKey, Pair::getValue)
                .block();
    }

    /**
     * 实际的解析逻辑
     * name  非表达式，直接返回
     * /name 获取 csv 中标题名为 name 对应的值
     * /1    获取 csv 中第 2 个索引位置的值
     *
     * @param pathExpr
     * @return
     */
    private static final Node EMPTY_NODE = Node.literal(null);
    private Node parseExpr(String pathExpr) {
        if (Objects.isNull(pathExpr)) {
            return EMPTY_NODE;
        }

        if (!isExpr(pathExpr)) {
            return Node.literal(pathExpr);
        }

        // 去掉开头的 "/"
        String field = pathExpr.substring(1);
        if (NumberUtil.isInteger(field)) {
            Integer index = Integer.valueOf(field);
            return new IndexNode(index);
        }

        return new NameNode(field);
    }

    // 判断 value 是否是一个表达式；nonNull && isString && startWith("/")
    private static boolean isExpr(Object value) {
        if (Objects.nonNull(value)
                && value instanceof String
                && ((String) value).startsWith("/")) {
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
         * @param item 数据记录
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
