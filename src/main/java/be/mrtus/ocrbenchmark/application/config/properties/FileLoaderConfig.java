package be.mrtus.ocrbenchmark.application.config.properties;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("benchmark.fileLoader")
public class FileLoaderConfig {

	@NotNull
	private String images;
	@NotNull
	private String output;
	@Min(1)
	private int queueSize;
	@NotNull
	private String root;

	public String getImages() {
		return this.images;
	}

	public String getOutput() {
		return this.output;
	}

	public int getQueueSize() {
		return this.queueSize;
	}

	public String getRoot() {
		return this.root;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public void setRoot(String root) {
		this.root = root;
	}
}
