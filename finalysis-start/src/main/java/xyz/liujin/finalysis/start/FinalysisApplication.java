package xyz.liujin.finalysis.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan(basePackages = "xyz.liujin.finalysis")
// 扫描 jpa; 不同包下需要这 2 个注解
@EntityScan(basePackages = "xyz.liujin.finalysis")
@EnableJpaRepositories(basePackages = "xyz.liujin.finalysis")
@SpringBootApplication
public class FinalysisApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinalysisApplication.class, args);
	}

}
