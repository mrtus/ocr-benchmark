package be.mrtus.ocrbenchmark;

import be.mrtus.ocrbenchmark.application.config.properties.BenchmarkConfig;
import be.mrtus.ocrbenchmark.application.config.properties.FileConverterConfig;
import be.mrtus.ocrbenchmark.domain.Benchmark;
import be.mrtus.ocrbenchmark.domain.fileconverters.FileConverterFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application extends Thread {

	@Autowired
	private Benchmark benchmark;
	@Autowired
	private BenchmarkConfig config;
	@Autowired
	private FileConverterFactory factory;
	@Autowired
	private FileConverterConfig fileConverterConfig;

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(Application.class, args);

		Application application = context.getBean(Application.class);

		application.start();
	}

	@Override
	public void run() {
		if(this.config.getMode().equalsIgnoreCase("BENCHMARK")) {
			this.benchmark.start();
		} else if(this.config.getMode().equalsIgnoreCase("CONVERT")) {
			Thread thread = this.factory.create(this.fileConverterConfig.getType());

			thread.start();

			try {
				thread.join();
			} catch(InterruptedException ex) {
				Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
