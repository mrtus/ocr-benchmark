package be.mrtus.ocrbenchmark.domain.fileconverters;

import be.mrtus.ocrbenchmark.application.config.properties.FileConverterCConfig;
import be.mrtus.ocrbenchmark.domain.Util;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

public class FileConverterC extends Thread {

	@Autowired
	private FileConverterCConfig config;
	private final AtomicInteger count = new AtomicInteger();
	private ExecutorService executorService;
	private volatile boolean isLoading = true;
	private final Logger log = Logger.getLogger(FileConverterA.class.getName());
	private final ArrayBlockingQueue<Path> queue = new ArrayBlockingQueue<>(1000);

	@PostConstruct
	public void init() {
		int threads = this.config.getThreads();

		this.executorService = Executors.newFixedThreadPool(threads);

		IntStream.range(0, threads)
				.forEach(i -> this.executorService.submit(() -> {
					FileConverterC c = FileConverterC.this;

					while(true) {
						while(queue.peek() == null) {
							if(!isLoading) {
								break;
							}
						}

						Path p = queue.poll();

						if(p == null) {
							break;
						}

						try {
							System.out.println("Processing file " + count.addAndGet(1) + " : " + p);

							String filename = p.getFileName().toString();
							String parentPathName = p.getParent().toString();

							parentPathName = parentPathName.replace(this.config.getImages(), this.config.getOutput());

							String[] split = filename.split("_");
							String fileContents = split[1];

							Path parentPath = Paths.get(parentPathName);

							filename = filename.substring(0, filename.length() - 4);
							Path newFile = parentPath.resolve(filename + ".txt");

							try {
								Files.createDirectories(parentPath);
								Files.createFile(newFile);
							} catch(IOException ex) {
								Logger.getLogger(FileConverterA.class.getName()).log(Level.SEVERE, null, ex);
							}

							try(BufferedWriter bw = new BufferedWriter(new FileWriter(newFile.toFile()))) {
								bw.write(fileContents);
							} catch(Exception e) {
								Logger.getLogger(FileConverterA.class.getName()).log(Level.SEVERE, null, e);
							}
						} catch(Exception e) {
							log.log(Level.SEVERE, null, e);
							System.exit(0);
						}
					}
				}));
	}

	@Override
	public void run() {
		Path path = Paths.get(this.config.getRoot()).resolve(this.config.getImages());

		long start = System.currentTimeMillis();

		try {
			Files.walk(path)
					.parallel()
					.filter(Files::isRegularFile)
					.forEach(p -> {
						try {
							this.queue.put(p);
						} catch(InterruptedException ex) {
							Logger.getLogger(FileConverterC.class.getName()).log(Level.SEVERE, null, ex);
						}
					});
		} catch(IOException ex) {
			log.log(Level.SEVERE, null, ex);
		}

		this.isLoading = false;

		try {
			this.executorService.awaitTermination(7, TimeUnit.DAYS);
		} catch(InterruptedException ex) {
			Logger.getLogger(FileConverterC.class.getName()).log(Level.SEVERE, null, ex);
		}

		long end = System.currentTimeMillis();

		System.out.println("Converting took: " + Util.durationToString(end - start));
	}
}
