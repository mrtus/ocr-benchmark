package be.mrtus.ocrbenchmark.application.config;

import be.mrtus.ocrbenchmark.domain.Benchmark;
import be.mrtus.ocrbenchmark.domain.FileLoader;
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
	public FileLoader fileLoader() {
		return new FileLoader();
	}

	@Bean
	public OCRLibraryFactory libraryFactory() {
		return new OCRLibraryFactory();
	}
}
