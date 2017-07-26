package be.mrtus.ocrbenchmark.domain.fileconverters;

import be.mrtus.ocrbenchmark.domain.entities.Annotation;
import be.mrtus.ocrbenchmark.persistence.AnnotationRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FileConverterA extends Thread {

	@Autowired
	private AnnotationRepository annotationRepository;

	@Override
	public void run() {
		Path path = Paths.get("x:/Challenge1_Test_Task3_GT.txt");

		try {
			Files.readAllLines(path)
					.forEach(l -> {
						String[] split = l.split(", ");
						String filename = split[0];
						String fileContents = split[1].replaceAll("\"", "");

						this.annotationRepository.save(new Annotation(filename, fileContents));
					});
		} catch(IOException e) {
			Logger.getLogger(FileConverterA.class.getName()).log(Level.SEVERE, null, e);
		}
	}
}
