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
	private int benchmarkThreads;
	private int saveQueueSize;
	@Min(1)
	private int saveThreads;
	@NotNull
	private String tessdataPath;

	public String getLibrary() {
		return this.library;
	}

	public String getMode() {
		return this.mode;
	}

	public int getBenchmarkThreads() {
		return this.benchmarkThreads;
	}

	public int getSaveQueueSize() {
		return this.saveQueueSize;
	}

	public void setSaveQueueSize(int saveQueueSize) {
		this.saveQueueSize = saveQueueSize;
	}

	public int getSaveThreads() {
		return this.saveThreads;
	}

	public void setSaveThreads(int saveThreads) {
		this.saveThreads = saveThreads;
	}

	public void setBenchmarkThreads(int benchmarkThreads) {
		this.benchmarkThreads = benchmarkThreads;
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

	public void setTessdataPath(String tessdataPath) {
		this.tessdataPath = tessdataPath;
	}
}
