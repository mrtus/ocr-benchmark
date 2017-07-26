package be.mrtus.ocrbenchmark;

import be.mrtus.ocrbenchmark.application.config.properties.BenchmarkConfig;
import be.mrtus.ocrbenchmark.application.config.properties.FileConverterConfig;
import be.mrtus.ocrbenchmark.domain.Benchmark;
import be.mrtus.ocrbenchmark.domain.fileconverters.FileConverterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	@Autowired
	private Benchmark benchmark;
	@Autowired
	private BenchmarkConfig config;
	@Autowired
	private FileConverterFactory factory;
	@Autowired
	private FileConverterConfig fileConverterConfig;

	public Application() throws InterruptedException {

		if(this.config.getMode().equalsIgnoreCase("BENCHMARK")) {
			this.benchmark.start();
		} else if(this.config.getMode().equalsIgnoreCase("CONVERT")) {
			Thread thread = this.factory.create(this.fileConverterConfig.getType());

			thread.start();

			thread.join();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
