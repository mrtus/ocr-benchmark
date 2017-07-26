package be.mrtus.ocrbenchmark.domain.fileconverters;

import be.mrtus.ocrbenchmark.application.config.properties.FileConverterAConfig;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FileConverterA extends Thread {

	@Autowired
	private FileConverterAConfig config;

	@Override
	public void run() {
		Path path = Paths.get(this.config.getRoot()).resolve(this.config.getFile());
		Path output = Paths.get(this.config.getRoot()).resolve(this.config.getOutput());

		try {
			Files.readAllLines(path)
					.forEach(l -> {
						String[] split = l.split(", ");
						String filename = split[0];
						filename = filename.substring(0, filename.length() - 4);

						String fileContents = split[1].replaceAll("\"", "");

						Path newFile = output.resolve(filename + ".txt");

						try {
							Files.createFile(newFile);
						} catch(IOException ex) {
							Logger.getLogger(FileConverterA.class.getName()).log(Level.SEVERE, null, ex);
						}

						try(BufferedWriter bw = new BufferedWriter(new FileWriter(newFile.toFile()))) {
							bw.write(fileContents);
						} catch(Exception e) {
							Logger.getLogger(FileConverterA.class.getName()).log(Level.SEVERE, null, e);
						}
					});
		} catch(IOException e) {
			Logger.getLogger(FileConverterA.class.getName()).log(Level.SEVERE, null, e);
		}
	}
}
