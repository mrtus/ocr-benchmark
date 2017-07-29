package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.application.config.properties.FileLoaderConfig;
import be.mrtus.ocrbenchmark.domain.entities.LoadedFile;
import be.mrtus.ocrbenchmark.persistence.AnnotationRepository;
import java.io.*;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	@Autowired
	private AnnotationRepository annotationRepository;

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

		Path path = Paths.get(this.config.getRoot()).resolve(this.config.getImages());

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

	private String loadFileContents(Path p) {
		this.logger.finer("Loading file contents for " + p.toString());

		try {
			return Files.readAllLines(p)
					.stream()
					.collect(Collectors.joining());
		} catch(MalformedInputException | UnmappableCharacterException e) {
			this.logger.log(Level.SEVERE, null, e);
		} catch(IOException e) {
			this.logger.log(Level.SEVERE, null, e);
		}

		return null;
	}

	private String loadTarget(Path p) {
		String path = p.getParent().toString();
		String filename = p.getFileName().toString();
		filename = filename.substring(0, filename.length() - 4);

		String outputDirectory = path.replace(this.config.getImages(), this.config.getOutput());

		Path outputPath = Paths.get(outputDirectory).resolve(filename + ".txt");

		return this.loadFileContents(outputPath);
	}

	private String loadTarget2(Path p) {
		String path = p.getParent().toString();
		String filename = p.getFileName().toString();
		filename = filename.substring(0, filename.length() - 4);

		return this.annotationRepository.findByFilename(filename).getWord();
	}

	private void processFile(Path p) {
		try {
			String target = this.loadTarget(p);

			LoadedFile file = new LoadedFile(p, target);

			this.logger.fine("File was processed " + p.toString());

			this.queue.put(file);
		} catch(InterruptedException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		}
	}
}
