package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.application.config.properties.FileLoaderConfig;
import be.mrtus.ocrbenchmark.domain.entities.LoadedFile;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

public class FileLoader extends Thread {

	@Autowired
	private FileLoaderConfig config;
	private volatile boolean loading;
	private final Logger logger = Logger.getLogger(FileLoader.class.getName());
	private ArrayBlockingQueue<LoadedFile> queue;

	public FileLoader() {
		this.setDaemon(true);
		this.setName("Fileloader");
	}

	public ArrayBlockingQueue<LoadedFile> getQueue() {
		return this.queue;
	}

	public boolean isLoading() {
		return this.loading;
	}

	@Override
	public void run() {
		this.queue = new ArrayBlockingQueue<>(this.config.getQueueSize());

		this.loading = true;

		Path path = Paths.get(this.config.getFileDir());

		this.logger.info("Processing files in " + path.toString());

		try {
			Files.walk(path)
					.filter(Files::isRegularFile)
					.forEach(p -> this.processFile(p));
		} catch(IOException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		}

		this.loading = false;

		this.logger.info("All files were processed in " + path.toString());
	}

	private String loadFileContents(Path p) throws IOException {
		this.logger.finer("Loading file contents for " + p.toString());

		Map<String, Charset> charsets = Charset.availableCharsets();

		for(Map.Entry<String, Charset> entry : charsets.entrySet()) {
			try {
				return Files.readAllLines(p, entry.getValue())
						.stream()
						.collect(Collectors.joining());
			} catch(MalformedInputException | UnmappableCharacterException e) {
			}
		}

		return null;
	}

	private void processFile(Path p) {
		try {
//			String contents = this.loadFileContents(p);

//			if(contents == null) {
//				return;
//			}
			LoadedFile file = new LoadedFile(p, "");

			this.logger.fine("File was processed " + p.toString());

			this.queue.put(file);
		} catch(InterruptedException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		}
	}
}
