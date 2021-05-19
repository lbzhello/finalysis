package xyz.liujin.finalysis.base.util;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    /**
     * csv 格式转 json
     * @param fields
     * @param items
     * @return
     */
    public static Flux<Map<String, ?>> parseCsv(@Nullable List<String> fields, @Nullable List<? extends List<?>> items) {
        if (CollectionUtil.isEmpty(fields) || CollectionUtil.isEmpty(items)) {
            return Flux.just();
        }
        return Flux.fromIterable(items)
                .map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    int flen = fields.size();
                    int ilen = item.size();
                    for (int i = 0; i < flen; i++) {
                        if (i < ilen) {
                            itemMap.put(fields.get(i), item.get(i));
                        } else {
                            itemMap.put(fields.get(i), null);
                        }
                    }
                    return itemMap;
                });
    }

    public static void main(String[] args) {
        JsonUtils.parseCsv(List.of("a", "b", "c"), List.of(List.of("1", "2"), List.of("100", "200", 4, 6)))
                .subscribe(it -> {
                    System.out.println(it);
                });

    }

}
