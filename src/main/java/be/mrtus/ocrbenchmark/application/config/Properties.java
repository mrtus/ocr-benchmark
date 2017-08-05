package be.mrtus.ocrbenchmark.application.config;

import be.mrtus.ocrbenchmark.application.config.properties.BenchmarkConfig;
import be.mrtus.ocrbenchmark.application.config.properties.DeleterConfig;
import be.mrtus.ocrbenchmark.application.config.properties.FileLoaderConfig;
import be.mrtus.ocrbenchmark.application.config.properties.ResultAnalyserConfig;
import be.mrtus.ocrbenchmark.application.config.properties.converters.FileConverterAConfig;
import be.mrtus.ocrbenchmark.application.config.properties.converters.FileConverterCConfig;
import be.mrtus.ocrbenchmark.application.config.properties.converters.FileConverterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Properties {

	@Bean
	public BenchmarkConfig benchmarkConfig() {
		return new BenchmarkConfig();
	}

	@Bean
	public DeleterConfig deleterConfig() {
		return new DeleterConfig();
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
	public FileConverterConfig fileConverterConfig() {
		return new FileConverterConfig();
	}

	@Bean
	public FileLoaderConfig fileLoaderConfig() {
		return new FileLoaderConfig();
	}

	@Bean
	public ResultAnalyserConfig resultAnalyserConfig() {
		return new ResultAnalyserConfig();
	}
}
