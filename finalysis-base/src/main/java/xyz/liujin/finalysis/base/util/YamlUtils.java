package xyz.liujin.finalysis.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

/**
 * YAML工具类
 */
public class YamlUtils {
    private static Logger logger = LoggerFactory.getLogger(YamlUtils.class);

    /**
     * yaml 文件解析成 mapper
     * @param path
     * @return
     */
    public static Mono<Map<String, Object>> parse(Path path) {
        return Mono.justOrEmpty(path)
                .map(it -> {
                    try {
                        Yaml yaml = new Yaml();
                        Map<String, Object> mapper = yaml.load(Files.newInputStream(path));
                        return mapper;
                    } catch (Exception e) {
                        logger.error("error parse yaml file", e);
                    }
                    return Collections.emptyMap();
                });
    }
}
