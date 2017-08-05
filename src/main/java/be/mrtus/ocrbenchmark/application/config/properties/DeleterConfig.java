package be.mrtus.ocrbenchmark.application.config.properties;

import java.util.UUID;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("benchmark.deleter")
public class DeleterConfig {

	private UUID benchmarkResultId;
	private int maxWidth;
	private int minTargetLength;

	public UUID getBenchmarkResultId() {
		return this.benchmarkResultId;
	}

	public int getMaxWidth() {
		return this.maxWidth;
	}

	public int getMinTargetLength() {
		return this.minTargetLength;
	}

	public void setBenchmarkResultId(String benchmarkResultId) {
		this.benchmarkResultId = UUID.fromString(benchmarkResultId);
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public void setMinTargetLength(int minTargetLength) {
		this.minTargetLength = minTargetLength;
	}
}
