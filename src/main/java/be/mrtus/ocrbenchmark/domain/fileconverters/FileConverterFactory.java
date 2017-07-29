package be.mrtus.ocrbenchmark.domain.fileconverters;

import org.springframework.beans.factory.annotation.Autowired;

public class FileConverterFactory {

	@Autowired
	private FileConverterA a;
	@Autowired
	private FileConverterC c;

	public Thread create(String type) {
		switch(type.toLowerCase()) {
			case "a":
				return this.a;
			case "c":
				return this.c;
			default:
				throw new IllegalArgumentException("File converter is not known for type " + type);
		}
	}
}
