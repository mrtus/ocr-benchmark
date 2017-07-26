package be.mrtus.ocrbenchmark.application.config.properties.converters;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fileconverter.a")
public class FileConverterAConfig {

	private String file;
	private String output;
	private String root;

	public String getRoot() {
		return this.root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getFile() {
		return this.file;
	}

	public String getOutput() {
		return this.output;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void setOutput(String output) {
		this.output = output;
	}
}
