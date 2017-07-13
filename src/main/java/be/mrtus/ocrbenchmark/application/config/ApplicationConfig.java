package be.mrtus.ocrbenchmark.application.config;

import be.mrtus.ocrbenchmark.application.config.properties.FileLoaderConfig;
import be.mrtus.ocrbenchmark.domain.Benchmark;
import be.mrtus.ocrbenchmark.domain.FileLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

	@Autowired
	private FileLoaderConfig fileLoaderConfig;

	@Bean
	public Benchmark benchmark() {
		return new Benchmark();
	}

	@Bean
	public FileLoader fileLoader() {
		return new FileLoader();
	}
}
