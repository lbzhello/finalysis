package xyz.liujin.finalysis.common.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.text.CharSequenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * json 映射器，数据提取器
 *
 * json
 * {
 *     "code": 0,
 *     "msg": "success"
 *     "data": {
 *         "date": "20210112",
 *         "name": "hello kitty",
 *         "description": "some test file"
 *     }
 * }
 *
 * extractor
 * {
 *     "code": "/code",
 *     "time": "/data/date",
 *     "name": "/data/name",
 *     "info": "/data/info:empty"
 * }
 *
 * json + extractor
 * {
 *     "code": 0,
 *     "time": "20210112",
 *     "name": "hello kitty",
 *     "info": "empty
 * }
 */
public class JsonExtractor {
    public static final Logger logger = LoggerFactory.getLogger(JsonExtractor.class);

    public static final String NIL = "null";
    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", Dict.of("name", "hh", "size", 5.6));
        map.put("arr", new Object[]{"v1", "v2", Dict.of("arr", "arrInv")});

        Dict mapper = Dict.of("name", "/data/name", "arr0", "/arr[0]", "arrv", "/arr[2]/arr", "data", "/data", "nil", "/data/hello=99");
        Map<String, Object> map1 = toMap(map, mapper);
        parseDSL(map, "/data");

        System.out.println();
    }

    /**
     * csv 转 map
     * @param fields csv 字段名
     * @param values csv 数组
     * @param extractor
     * @return
     */
    public static Flux<Map<String, Object>> csvMap(Flux<String> fields, Flux<Flux<String>> values, Map<String, Object> extractor) {
        if (Objects.isNull(extractor)) {
            return Flux.just();
        }

        return fields.index()
                // 获取 field 对应索引
                .collectMap(Tuple2::getT2, Tuple2::getT1)
                .flux()
                .flatMap(fieldMap -> values.flatMap(Flux::collectList).map(items -> Tuples.of(fieldMap, items)))
                .flatMap(tuple -> {
                    Map<String, Long> fieldMap = tuple.getT1();
                    List<String> item = tuple.getT2();
                    // 每条记录转 map
                    return Flux.fromIterable(extractor.entrySet())
                            .map(entry -> {
                                Object vk = entry.getValue();

                                // 以 ‘/’ 开头为 dsl 语句，如 /name /2
                                if (Objects.nonNull(vk)
                                        && vk instanceof String
                                        && ((String) vk).startsWith("/")) {
                                    // 去掉开头的 ‘/’，获取 dsl
                                    String kDsl = ((String) vk).substring(1);
                                    Long ki;
                                    try {
                                        // 直接使用索引，如 /2
                                        ki = Long.parseLong(kDsl);
                                    } catch (NumberFormatException e) {
                                        // 根据标题名字获取索引，如 /name
                                        ki = fieldMap.get(kDsl);
                                    }

                                    // 根据索引获取 values 对应的值
                                    String value = item.get(ki.intValue());
                                    return Tuples.of(entry.getKey(), value);
                                } else
                                    return Tuples.of(entry.getKey(), entry.getValue());

                            })
                            .collectMap(Tuple2::getT1, Tuple2::getT2);
                });

    }

    /**
     * 将 json 数据根据 extractor 映射成 map
     * @param json
     * @param extractor
     * @return
     */
    public static Map<String, Object> toMap(Map<String, Object> json, Map<String, Object> extractor) {
        if (Objects.isNull(extractor)) {
            return Collections.emptyMap();
        }

        // 结果集
        Map<String, Object> rstMap = new HashMap<>();

        Flux.fromIterable(extractor.entrySet())
                .subscribe(entry -> {
                    Object pv = parseValue(json, entry.getValue());
                    rstMap.put(entry.getKey(), pv);
                }, e -> logger.error("parse json extractor failed", e));
        return rstMap;
    }

    /**
     * 将 json 根据 listExtractor 映射成 list
     * @param json
     * @param listExtractor
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List toList(Map<String, Object> json, List listExtractor) {
        if (CollectionUtil.isEmpty(listExtractor)) {
            return Collections.emptyList();
        }
        List list = new ArrayList();
        Flux.fromIterable(listExtractor)
                .map(v -> parseValue(json, v))
                .subscribe(it -> {
                    list.add(it);
                }, e -> logger.error("parse list mapper failed", e));
        return list;
    }

    /**
     * 解析值映射语句
     * @param json
     * @param v
     * @return
     */
    private static Object parseValue(Map<String, Object> json, Object v) {
        if (v instanceof String) {
            return parseDSL(json, String.valueOf(v));
        } else if (v instanceof Map) {
            // 递归获取值
            return toMap(json, (Map<String, Object>) v);
        } else if (v instanceof List) {
            return toList(json, (List) v);
        } else if (ArrayUtils.isArray(v)) {
            Object[] vArr = ArrayUtils.asArray(v, Object[].class);
            return toList(json, Arrays.asList(vArr));
        } else {
            return v;
        }
    }

    /**
     * 解析 Mapper 语句
     * path          非 / 开头，直接返回;
     * /path/to      解析原值，默认 null;
     * /path/to:str  解析成字符串，默认 "";
     * /path/to=22   解析成数字，默认 null;
     * /path/to?true 解析成 bool 值，默认 null;
     * @return
     */
    private static Object parseDSL(Map<String, Object> src, String pathDsl) {
        if (CharSequenceUtil.isBlank(pathDsl)) {
            return pathDsl;
        }

        if (!pathDsl.startsWith("/")) {
            return pathDsl;
        }

        String pathStr = pathDsl;
        // 将要返回的默认值
        Object defaultValue = null;
        // 需要转换的类型 0 ':' 1 '=' 2 '?'
        int flag = 0;
        // 提供字符串默认值，或转成字符串
        if (pathDsl.contains(":")) {
            flag = 0;
            String[] arr = pathDsl.split(":");
            pathStr = arr[0];
            defaultValue = arr.length == 1 ? "" : arr[1];
        }
        // 提供数字默认值，或转成数字
        if (pathDsl.contains("=")) {
            flag = 1;
            String[] arr = pathDsl.split("=");
            pathStr = arr[0];
            if (arr.length != 1 && !Objects.equals(arr[1], NIL) ) {
                defaultValue = new BigDecimal(arr[1]);
            }
        }
        // 提供 bool 默认值，或转成 bool
        if (pathDsl.contains("?")) {
            flag = 2;
            String[] arr = pathDsl.split("[?]");
            pathStr = arr[0];
            if (arr.length != 1 && !Objects.equals(arr[1], NIL)) {
                defaultValue = Boolean.valueOf(arr[1]);
            }
        }

        // 路径语句
        List<String> paths = Arrays.stream(pathStr.split("/"))
                .filter(CharSequenceUtil::isNotBlank)
                .collect(Collectors.toList());
        Map<String, Object> curSrc = src; // 当前所处上下文
        for (String path : paths) {
            Object curValue = getValue(curSrc, path);
            if (curValue == null) {
                return defaultValue;
            } else if (curValue instanceof Map) {
                curSrc = (Map<String, Object>) curValue;
            } else {
                return switch (flag) {
                    case 0 -> String.valueOf(curValue);
                    case 1 -> new BigDecimal(String.valueOf(curValue));
                    case 2 -> Boolean.valueOf(String.valueOf(curValue));
                    default -> defaultValue;
                };
            }
        }
        return curSrc;
    }

    /**
     * key -> jsonMap.get(key)
     * key[0] -> jsonMap.getArr(key)[0]
     * @param jsonMap
     * @param key
     * @return
     */
    private static Object getValue(Map<String, Object> jsonMap, String key) {
        String[] arr = key.replace("]", "").split("\\[");
        if (arr.length == 1) {
            return jsonMap.get(key);
        } else {
            String k = arr[0];
            int index = Integer.parseInt(arr[1]);
            Object v = jsonMap.get(k);
            if (v instanceof List) {
                return ((List) v).get(index);
            } else {
                return Array.get(v, index);
            }
        }

    }

}
