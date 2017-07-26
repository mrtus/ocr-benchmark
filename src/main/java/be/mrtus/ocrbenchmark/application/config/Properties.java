package be.mrtus.ocrbenchmark.application.config;

import be.mrtus.ocrbenchmark.application.config.properties.BenchmarkConfig;
import be.mrtus.ocrbenchmark.application.config.properties.FileConverterAConfig;
import be.mrtus.ocrbenchmark.application.config.properties.FileConverterCConfig;
import be.mrtus.ocrbenchmark.application.config.properties.FileLoaderConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Properties {

	@Bean
	public BenchmarkConfig benchmarkConfig() {
		return new BenchmarkConfig();
	}

	@Bean
	public FileConverterAConfig fileConverterAConfig() {
		return new FileConverterAConfig();
	}

	@Bean
	public FileConverterCConfig fileConverterCConfig() {
		return new FileConverterCConfig();
	}

	@Bean
	public FileLoaderConfig fileLoaderConfig() {
		return new FileLoaderConfig();
	}
}
