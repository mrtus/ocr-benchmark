package be.mrtus.ocrbenchmark.domain.fileconverters;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileConverterA extends Thread {

	@Override
	public void run() {
		Path path = Paths.get("x:/Challenge1_Test_Task3_GT.txt");
		Path output = Paths.get("x:/output");

		try {
			Files.readAllLines(path)
					.forEach(l -> {
						String[] split = l.split(", ");
						String fileName = split[0];
						fileName = fileName.substring(0, fileName.length() - 4);

						String fileContents = split[1].replaceAll("\"", "");

						Path newFile = output.resolve(fileName + ".txt");

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
