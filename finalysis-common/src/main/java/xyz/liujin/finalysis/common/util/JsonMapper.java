package xyz.liujin.finalysis.common.util;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.text.CharSequenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * json 映射器
 */
public class JsonMapper {
    public static final Logger logger = LoggerFactory.getLogger(JsonMapper.class);

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
     * 将 json 数据根据 mapper 映射成 map
     * @param json
     * @param mapper
     * @return
     */
    public static <V extends Object> Map<String, Object> toMap(Map<String, Object> json, Map<String, V> mapper) {
        if (Objects.isNull(mapper)) {
            return Collections.emptyMap();
        }

        // 结果集
        Map<String, Object> rstMap = new HashMap<>();

        Flux.fromIterable(mapper.entrySet())
                .subscribe(entry -> {
                    String k = entry.getKey();
                    Object v = entry.getValue();
                    if (v instanceof Map) {
                        // 递归获取值
                        rstMap.put(k, toMap(json, (Map<String, V>) v));
                    } else if (v instanceof String) {
                        rstMap.put(k, parseDSL(json, String.valueOf(v)));
                    } else {
                        rstMap.put(k, v);
                    }
                }, e -> logger.error("parse json mapper failed", e));
        return rstMap;
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
