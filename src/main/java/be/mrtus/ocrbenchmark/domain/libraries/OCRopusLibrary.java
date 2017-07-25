package be.mrtus.ocrbenchmark.domain.libraries;

import be.mrtus.ocrbenchmark.domain.entities.LoadedFile;

public class OCRopusLibrary implements OCRLibrary{

	@Override
	public String doOCR(LoadedFile file) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}