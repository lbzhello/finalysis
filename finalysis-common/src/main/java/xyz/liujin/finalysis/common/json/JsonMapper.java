package xyz.liujin.finalysis.common.json;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import xyz.liujin.finalysis.common.util.ArrayUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public interface JsonMapper {
    Logger logger = LoggerFactory.getLogger(JsonMapper.class);

    // 表达式前缀
    static final String EXPR_PRE = "/";

    /**
     * 从 JSON 中提取值
     *
     * @param json
     * @return
     */
    Object eval(Map<String, ?> json);

    default <T> T eval(Map<String, ?> json, Class<T> clazz) {
        Object obj = eval(json);
        return JSONUtil.toBean(JSONUtil.parseObj(obj), clazz);
    }

    /**
     * 解析 obj 的值，生成映射
     *
     * @param obj
     * @return
     */
    static JsonMapper parse(Object obj) {
        if (JsonMapper.isExpr(obj)) {
            return JsonMapper.parseExpr((String) obj);
        } else if (obj instanceof List) {
            return JsonMapper.parseList((List<?>) obj);
        } else if (obj instanceof Map) {
            return JsonMapper.parseMap((Map<String, ?>) obj);
        }

        return JsonMapper.literal(obj);
    }

    /**
     * 生成一个字面值 Mapper，即返回原值
     *
     * @return
     */
    static JsonMapper literal(Object obj) {
        return it -> obj;
    }

    /**
     * 解析 map 生成映射
     *
     * @param map
     * @return
     */
    static JsonMapper parseMap(Map<String, ?> map) {
        return new MapMapper(map);
    }

    /**
     * 解析 list 生成映射
     *
     * @param list
     * @return
     */
    static JsonMapper parseList(List<?> list) {
        return new ListMapper(list);
    }

    /**
     * 解析路径表达式
     * path          非 / 开头，直接返回;
     * /path/to      解析原值，默认 null;
     * /path/to:str  解析成字符串，默认 "";
     * /path/to=22   解析成数字，默认 null;
     * /path/to?true 解析成 bool 值，默认 null;
     *
     * @return
     */
    String NIL = "null";

    static JsonMapper parseExpr(String pathStr) {
        if (CharSequenceUtil.isBlank(pathStr)) {
            return JsonMapper.literal(pathStr);
        }

        if (!pathStr.startsWith("/")) {
            return JsonMapper.literal(pathStr);
        }

        String pathExpr = pathStr;
        // 将要返回的默认值
        Object defaultValue = null;
        // 需要转换的类型 0 原值 1 ':' 2 '=' 3 '?'
        int type = 0;
        // 提供字符串默认值，或转成字符串
        if (pathStr.contains(":")) {
            type = 1;
            String[] arr = pathStr.split(":");
            pathExpr = arr[0];
            defaultValue = arr.length == 1 ? "" : arr[1];
        }
        // 提供数字默认值，或转成数字
        if (pathStr.contains("=")) {
            type = 2;
            String[] arr = pathStr.split("=");
            pathExpr = arr[0];
            if (arr.length != 1 && !Objects.equals(arr[1], NIL)) {
                defaultValue = new BigDecimal(arr[1]);
            }
        }
        // 提供 bool 默认值，或转成 bool
        if (pathStr.contains("?")) {
            type = 3;
            String[] arr = pathStr.split("[?]");
            pathExpr = arr[0];
            if (arr.length != 1 && !Objects.equals(arr[1], NIL)) {
                defaultValue = Boolean.valueOf(arr[1]);
            }
        }

        // 路径语句
        Path.Node[] nodes = Arrays.stream(pathExpr.split("/"))
                .filter(CharSequenceUtil::isNotBlank)
                .map(Path::createNode)
                .toArray(Path.Node[]::new);

        return new Path(nodes, type, defaultValue);
    }

    // 判断 value 是否是一个表达式；nonNull && isString && startWith("/")
    private static boolean isExpr(Object value) {
        if (Objects.nonNull(value)
                && value instanceof String
                && ((String) value).startsWith("/")) {
            return true;
        }
        // 或者本身就是一个路径表达式
        if (Objects.nonNull(value) && value instanceof Path) {
            return true;
        }

        return false;
    }

    /**
     * 哈希映射
     */
    @Data
    class MapMapper implements JsonMapper {
        private Map<String, ?> map;

        public MapMapper(Map<String, ?> map) {
            this.map = map.entrySet().stream()
                    // 将 value 转成 Mapper
                    .map(entry -> {
                        Object value = JsonMapper.parse(entry.getValue());
                        return Tuples.of(entry.getKey(), value);
                    })
                    .collect(Collectors.toMap(Tuple2::getT1, Tuple2::getT2));
        }

        @Override
        public Object eval(Map<String, ?> json) {
            Map<String, Object> rstMap = new HashMap<>();
            Mono.justOrEmpty(map)
                    .flatMapIterable(map -> map.entrySet())
                    .subscribe(entry -> {
                        Object value = entry.getValue();
                        if (value instanceof JsonMapper) {
                            value = ((JsonMapper) value).eval(json);
                        }
                        rstMap.put(entry.getKey(), value);
                    }, e -> logger.error("failed to eval MapMapper", e));

            return rstMap;
        }

    }

    /**
     * 列表映射
     */
    @Data
    class ListMapper implements JsonMapper {
        private List<?> list;

        public ListMapper(List<?> list) {
            this.list = Mono.justOrEmpty(list)
                    .flatMapIterable(it -> it)
                    .map(it -> JsonMapper.parse(it))
                    .collectList()
                    .block();
        }

        @Override
        public Object eval(Map<String, ?> json) {
            List<Object> rstList = new ArrayList<>();
            Mono.justOrEmpty(list)
                    .flux()
                    .flatMap(Flux::fromIterable)
                    .subscribe(it -> {
                        if (it instanceof JsonMapper) {
                            rstList.add(((JsonMapper) it).eval(json));
                        } else {
                            rstList.add(it);
                        }
                    }, e -> logger.error("failed to eval ListMapper", e));

            return rstList;
        }
    }


    /**
     * 表示一个 JSON 路径表达式，如 /path/to/name
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class Path implements JsonMapper {
        // 表示路径指向的值的类型
        public static final String STRING = "string";
        public static final String NUMBER = "number";
        public static final String BOOLEAN = "bool";

        private Node[] nodes;
        // 值类型 0 string, 1 number 2 bool
        private int type;
        private Object defaultValue;

        /**
         * 根据 Path 从 json 取值
         *
         * @param json
         * @return
         */
        @Override
        public Object eval(Map<String, ?> json) {
            if (!(json instanceof Map)) {
                return defaultValue;
            }
            Object curVal = json;
            for (Node node : nodes) {
                curVal = node.get(curVal);
                if (curVal == null) {
                    return defaultValue;
                }
            }

            return switch (type) {
                case 1 -> String.valueOf(curVal);
                case 2 -> new BigDecimal(String.valueOf(curVal));
                case 3 -> Boolean.valueOf(String.valueOf(curVal));
                default -> curVal;
            };
        }


        /**
         * 创建一个节点
         *
         * @param nodeExpr
         * @return
         */
        public static Node createNode(String nodeExpr) {
            // arr[0]
            String[] arr = nodeExpr.replace("]", "").split("\\[");
            if (arr.length == 1) {
                return new Node(arr[0], null);
            } else {
                String k = arr[0];
                int index = Integer.parseInt(arr[1]);
                return new Node(arr[0], index);
            }
        }

        /**
         * 路径中的某一个节点
         * e.g.
         * path： /name/arr[0]
         * node：  name, arr[0]
         */
        @Data
        @AllArgsConstructor
        public static class Node {
            private String name;
            // 下标值，如 arr[1]
            private Integer index;

            /**
             * 根据当前 Node 从数据源取值
             *
             * @param source
             * @return
             */
            private Object get(Object source) {
                Object obj = null;
                if (source instanceof Map) {
                    obj = ((Map) source).get(name);
                    // arr[0]
                    if (Objects.nonNull(obj) && Objects.nonNull(index)) {
                        if (obj instanceof List) {
                            obj = ((List) obj).get(index);
                        } else if (ArrayUtils.isArray(obj)) {
                            obj = ArrayUtils.asArray(obj, Object[].class)[index];
                        }
                    }
                }
                return obj;
            }
        }
    }

}
