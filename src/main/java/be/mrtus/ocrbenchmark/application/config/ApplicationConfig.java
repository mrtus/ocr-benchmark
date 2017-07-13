package be.mrtus.ocrbenchmark.application.config;

import be.mrtus.ocrbenchmark.domain.Benchmark;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

	@Bean
	public Benchmark benchmark() {
		return new Benchmark();
	}
}
