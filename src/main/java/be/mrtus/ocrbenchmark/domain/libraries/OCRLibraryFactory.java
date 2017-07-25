package be.mrtus.ocrbenchmark.domain.libraries;

public class OCRLibraryFactory {

	public OCRLibrary build(String library) {
		switch(library) {
			default:
				throw new IllegalArgumentException("OCR Library could not be build");
		}
	}
}
