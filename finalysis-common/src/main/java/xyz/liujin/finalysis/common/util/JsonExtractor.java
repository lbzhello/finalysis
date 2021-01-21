package xyz.liujin.finalysis.common.util;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import xyz.liujin.finalysis.common.json.JsonMapper;

import java.nio.file.Path;
import java.util.*;

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

        Object obj = JsonMapper.parse(mapper).eval(map);

        JsonMapper listMapper = JsonMapper.parse(Arrays.asList("name", "/data/name", "arr0", "/arr[0]", "arrv", "/arr[2]/arr", "data", "/data", "nil", "/data/hello=99"));
        Object obj2 = listMapper.eval(map);

        System.out.println();
    }

    /**
     * csv 转实体类
     * @param fields
     * @param values
     * @param path
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Flux<T> csvMap(Flux<String> fields, Flux<Flux<String>> values, Path path, Class<T> clazz) {
        return YamlUtils.parse(path)
                .flux()
                .flatMap(mapper -> csvMap(fields, values, mapper))
                .map(items -> JSONUtil.toBean(JSONUtil.parseObj(items), clazz));
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
                                try {
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
                                    }
                                } catch (Exception e) {
                                    logger.error("failed execute csvMap", e);
                                }
                                return Tuples.of(entry.getKey(), entry.getValue());
                            })
                            .collectMap(Tuple2::getT1, Tuple2::getT2);
                }, 10, 100000);

    }

}
