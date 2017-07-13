package be.mrtus.ocrbenchmark.application.config.properties;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("benchmark")
public class BenchmarkConfig {

	@Min(1)
	private int parallelBenchmarks;
	@NotNull
	private String processorUrl;

	public int getParallelBenchmarks() {
		return this.parallelBenchmarks;
	}

	public String getProcessorUrl() {
		return this.processorUrl;
	}

	public void setProcessorUrl(String processorUrl) {
		this.processorUrl = processorUrl;
	}

	public void setParallelBenchmarks(int parallelBenchmarks) {
		this.parallelBenchmarks = parallelBenchmarks;
	}
}
