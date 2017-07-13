package be.mrtus.ocrbenchmark.application.config;

import be.mrtus.ocrbenchmark.application.config.properties.BenchmarkConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Properties {

	@Bean
	public BenchmarkConfig benchmarkConfig() {
		return new BenchmarkConfig();
	}
}
