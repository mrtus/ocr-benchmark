package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.application.config.properties.FileLoaderConfig;
import be.mrtus.ocrbenchmark.domain.entities.LoadedFile;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

public class FileLoader extends Thread {

	@Autowired
	private FileLoaderConfig config;
	private final Logger logger = Logger.getLogger(FileLoader.class.getName());
	private ArrayBlockingQueue<LoadedFile> queue;

	public FileLoader() {
		this.setDaemon(false);
	}

	public ArrayBlockingQueue<LoadedFile> getQueue() {
		return this.queue;
	}

	@Override
	public void run() {
		this.queue = new ArrayBlockingQueue<>(this.config.getQueueSize());

		Path path = new File(this.config.getFileDir()).toPath();

		this.logger.info("Processing files in " + path.toString());

		try {
			Files.walk(path)
					.forEach(p -> {
						if(Files.isRegularFile(p)) {
							this.processFile(p);
						}
					});
		} catch(IOException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		}
	}

	private String loadFileContents(Path p) throws IOException {
		String fileContents = Files.readAllLines(p, Charset.forName("ISO-8859-1"))
				.stream()
				.collect(Collectors.joining());

		return fileContents;
	}

	private void processFile(Path p) {
		try {
			LoadedFile file = new LoadedFile(p);

			this.queue.put(file);

			file.setFileContents(this.loadFileContents(p));

			this.logger.info("File was put in queue: " + p.toString());
		} catch(InterruptedException | IOException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		}
	}
}
