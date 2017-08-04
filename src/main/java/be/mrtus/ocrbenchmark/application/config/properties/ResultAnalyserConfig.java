package be.mrtus.ocrbenchmark.application.config.properties;

import java.util.UUID;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("benchmark.analyser")
public class ResultAnalyserConfig {

	private UUID benchmarkResultId;
	private int queueSize;
	private int threads;

	public UUID getBenchmarkResultId() {
		return this.benchmarkResultId;
	}

	public int getQueueSize() {
		return this.queueSize;
	}

	public int getThreads() {
		return this.threads;
	}

	public void setBenchmarkResultId(String benchmarkResultId) {
		this.benchmarkResultId = UUID.fromString(benchmarkResultId);
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}
}
