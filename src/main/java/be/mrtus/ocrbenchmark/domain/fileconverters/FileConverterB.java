package be.mrtus.ocrbenchmark.domain.fileconverters;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileConverterB extends Thread {

	@Override
	public void run() {
		Path path = Paths.get("x:/Challenge1_Training_Task1_GT.txt");
		Path output = Paths.get("x:/output2");

		try {
			Files.walk(path)
					.filter(Files::isRegularFile)
					.forEach(p -> {
						Path filename = p.getFileName();
						Path newFile = output.resolve(filename);

						try {
							Files.createFile(newFile);
						} catch(IOException ex) {
							Logger.getLogger(FileConverterB.class.getName()).log(Level.SEVERE, null, ex);
						}

						try(BufferedWriter bw = new BufferedWriter(new FileWriter(newFile.toFile()))) {
							StringBuilder fileContents = new StringBuilder();

							Files.readAllLines(p)
							.forEach(l -> {
								String[] split = l.split(", ");
								String value = split[4].replace("\"", "");

								fileContents.append(value);
								fileContents.append("\n");
							});

							bw.write(fileContents.toString());
						} catch(IOException ex) {

						}
					});
		} catch(IOException ex) {
			Logger.getLogger(FileConverterB.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
