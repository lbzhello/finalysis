package xyz.liujin.finalysis.start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@MapperScan(basePackages = "xyz.liujin.finalysis.**.mapper")
@ComponentScan(basePackages = "xyz.liujin.finalysis")
@SpringBootApplication
public class FinalysisApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinalysisApplication.class, args);
	}

}
