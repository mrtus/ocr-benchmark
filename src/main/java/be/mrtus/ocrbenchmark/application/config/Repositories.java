package be.mrtus.ocrbenchmark.application.config;

import be.mrtus.ocrbenchmark.persistence.BenchmarkResultRepository;
import be.mrtus.ocrbenchmark.persistence.BenchmarkResultRepositoryJPA;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepository;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepositoryJPA;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Repositories {

	@Bean
	public BenchmarkResultRepository benchmarkResultRepository() {
		return new BenchmarkResultRepositoryJPA();
	}

	@Bean
	public ProcessResultRepository processResultRepository() {
		return new ProcessResultRepositoryJPA();
	}
}
