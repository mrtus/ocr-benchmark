package be.mrtus.ocrbenchmark.application.config;

import be.mrtus.ocrbenchmark.domain.Benchmark;
import be.mrtus.ocrbenchmark.domain.FileLoader;
import be.mrtus.ocrbenchmark.domain.ResultAnalyser;
import be.mrtus.ocrbenchmark.domain.fileconverters.FileConverterA;
import be.mrtus.ocrbenchmark.domain.fileconverters.FileConverterC;
import be.mrtus.ocrbenchmark.domain.fileconverters.FileConverterFactory;
import be.mrtus.ocrbenchmark.domain.libraries.OCRLibraryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

	@Bean
	public Benchmark benchmark() {
		return new Benchmark();
	}

	@Bean
	public FileConverterA fileConverterA() {
		return new FileConverterA();
	}

	@Bean
	public FileConverterC fileConverterC() {
		return new FileConverterC();
	}

	@Bean
	public FileConverterFactory fileConverterFactory() {
		return new FileConverterFactory();
	}

	@Bean
	public FileLoader fileLoader() {
		return new FileLoader();
	}

	@Bean
	public OCRLibraryFactory libraryFactory() {
		return new OCRLibraryFactory();
	}

	@Bean
	public ResultAnalyser resultAnalyser() {
		return new ResultAnalyser();
	}
}
