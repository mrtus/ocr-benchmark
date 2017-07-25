package be.mrtus.ocrbenchmark.domain.libraries;

import be.mrtus.ocrbenchmark.domain.entities.LoadedFile;

public interface OCRLibrary {

	public String doOCR(LoadedFile file);
}
