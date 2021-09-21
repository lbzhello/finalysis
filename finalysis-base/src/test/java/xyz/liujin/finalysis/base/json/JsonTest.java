package xyz.liujin.finalysis.base.json;

import cn.hutool.core.lang.Dict;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JsonTest {
    @Test
    public void csvMapperTest() {
        Map<String, Object> map = Dict.of(
                "name", "hhh",
                "age", 22,
                "desc", "some value"
        );
    }

    @Test
    public void jsonMapperTest() {
        Map<String, Object> map = new HashMap<>();
        map.put("data", Dict.of("name", "hh", "size", 5.6));
        map.put("arr", new Object[]{"v1", "v2", Dict.of("arr", "arrInv")});

        Dict mapper = Dict.of("name", "/data/name", "arr0", "/arr[0]", "arrv", "/arr[2]/arr", "data", "/data", "nil", "/data/hello=99");

        Object mapObj = JsonMapper.json(mapper).eval(map);

        Object listObj = JsonMapper.list(Arrays.asList("name", "/data/name", "arr0", "/arr[0]", "arrv", "/arr[2]/arr", "data", "/data", "nil", "/data/hello=99"))
                .eval(map);
        System.out.println();
    }
}
