package be.mrtus.ocrbenchmark.application.config.properties;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("benchmark")
public class BenchmarkConfig {

	@NotNull
	private String library;
	@NotNull
	private String mode;
	@Min(1)
	private int parallelBenchmarks;
	@NotNull
	private String tessdataPath;

	public String getLibrary() {
		return this.library;
	}

	public String getMode() {
		return this.mode;
	}

	public int getParallelBenchmarks() {
		return this.parallelBenchmarks;
	}

	public String getTessdataPath() {
		return this.tessdataPath;
	}

	public void setLibrary(String library) {
		this.library = library;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setParallelBenchmarks(int parallelBenchmarks) {
		this.parallelBenchmarks = parallelBenchmarks;
	}

	public void setTessdataPath(String tessdataPath) {
		this.tessdataPath = tessdataPath;
	}
}
