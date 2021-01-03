package xyz.liujin.finalysis.spider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpiderApplication {
    private Logger logger = LoggerFactory.getLogger(SpiderApplication.class);

    @Bean
    public ApplicationRunner onSpiderStartUp() {
        logger.debug("onSpiderStartUp running...");

        return args -> logger.debug("onSpiderStartUp running...");
    }
}
