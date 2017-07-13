package be.mrtus.ocrbenchmark.application.config.properties;

import javax.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("benchmark")
public class BenchmarkConfig {
	
	@Min(1)
	private int parallelBenchmarks;

	public int getParallelBenchmarks() {
		return this.parallelBenchmarks;
	}

	public void setParallelBenchmarks(int parallelBenchmarks) {
		this.parallelBenchmarks = parallelBenchmarks;
	}
}
