package be.mrtus.ocrbenchmark.domain.libraries;

import be.mrtus.ocrbenchmark.domain.entities.LoadedFile;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class TesseractLibrary implements OCRLibrary {

	private final Logger logger = Logger.getLogger(TesseractLibrary.class.getName());
	private final ITesseract tesseract;

	public TesseractLibrary(String tessdataPath) {
		this.tesseract = new Tesseract();

		this.tesseract.setDatapath(tessdataPath);
	}

	@Override
	public String doOCR(LoadedFile loadedFile) {
		File file = loadedFile.getPath().toFile();

		try {
			return this.tesseract.doOCR(file);
		} catch(TesseractException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		}

		return "";
	}
}
