package be.mrtus.ocrbenchmark.application.config;

import be.mrtus.ocrbenchmark.persistence.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Repositories {

	@Bean
	public AnnotationRepository annotationRepository() {
		return new AnnotationRepositoryJPA();
	}

	@Bean
	public BenchmarkResultRepository benchmarkResultRepository() {
		return new BenchmarkResultRepositoryJPA();
	}

	@Bean
	public ProcessResultRepository processResultRepository() {
		return new ProcessResultRepositoryJPA();
	}
}
