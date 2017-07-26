package be.mrtus.ocrbenchmark.domain.fileconverters;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileConverterC extends Thread {

	@Override
	public void run() {
		Path path = Paths.get("x:/images3");
		Path output = Paths.get("x:/output3");

		AtomicInteger count = new AtomicInteger();
		
		try {
			Files.walk(path)
					.filter(Files::isRegularFile)
					.forEach(p -> {
						try {
							System.out.println("Processing file: " + p);

							String filename = p.getFileName().toString();
							String parentPathName = p.getParent().toString();

							parentPathName = parentPathName.replace("images3", "output3");

							String[] split = filename.split("_");
							String fileContents = split[1];

							Path parentPath = Paths.get(parentPathName);

							filename = filename.substring(0, filename.length() - 4);
							Path newFile = parentPath.resolve(filename + ".txt");

							try {
								Files.createDirectories(parentPath);
								Files.createFile(newFile);
							} catch(IOException ex) {
								Logger.getLogger(FileConverterC.class.getName()).log(Level.SEVERE, null, ex);
							}

							try(BufferedWriter bw = new BufferedWriter(new FileWriter(newFile.toFile()))) {
								bw.write(fileContents);
							} catch(Exception e) {
								Logger.getLogger(FileConverterA.class.getName()).log(Level.SEVERE, null, e);
							}

							count.addAndGet(1);
							
							System.out.print("Processed the " + count.get() + " file");
						} catch(Exception e) {
							Logger.getLogger(FileConverterA.class.getName()).log(Level.SEVERE, null, e);
						}
					});
		} catch(IOException ex) {
			Logger.getLogger(FileConverterC.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
