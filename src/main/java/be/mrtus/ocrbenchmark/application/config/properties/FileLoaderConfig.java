package be.mrtus.ocrbenchmark.application.config.properties;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("benchmark.fileLoader")
public class FileLoaderConfig {

	@NotNull
	private String fileDir;
	@Min(1)
	private int queueSize;

	public String getFileDir() {
		return this.fileDir;
	}

	public int getQueueSize() {
		return this.queueSize;
	}

	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}
}
