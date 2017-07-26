package be.mrtus.ocrbenchmark.application.config.properties.converters;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fileConverter")
public class FileConverterConfig {

	private String type;

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
