package be.mrtus.ocrbenchmark.application.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fileconverter.c")
public class FileConverterCConfig {

	private String images;
	private String output;
	private String root;
	private int threads;

	public String getImages() {
		return this.images;
	}

	public String getOutput() {
		return this.output;
	}

	public String getRoot() {
		return this.root;
	}

	public int getThreads() {
		return this.threads;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}
}
