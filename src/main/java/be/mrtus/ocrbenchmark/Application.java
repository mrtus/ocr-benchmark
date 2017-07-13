package be.mrtus.ocrbenchmark;

import be.mrtus.ocrbenchmark.domain.Benchmark;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(Application.class, args);

		Benchmark benchmark = context.getBean(Benchmark.class);
		
		benchmark.start();
	}
}
