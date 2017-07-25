package be.mrtus.ocrbenchmark.domain.libraries;

import be.mrtus.ocrbenchmark.application.config.properties.BenchmarkConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class OCRLibraryFactory {

	@Autowired
	private BenchmarkConfig config;

	public OCRLibrary build(String library) {
		switch(library) {
			case "OCROPUS":
				return new OCRopusLibrary();
			case "TESSERACT":
				String tessdata = this.config.getTessdataPath();

				return new TesseractLibrary(tessdata);
			default:
				throw new IllegalArgumentException("OCR Library could not be build");
		}
	}
}
