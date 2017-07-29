package be.mrtus.ocrbenchmark.domain.fileconverters;

import be.mrtus.ocrbenchmark.application.config.properties.converters.FileConverterCConfig;
import be.mrtus.ocrbenchmark.domain.Util;
import be.mrtus.ocrbenchmark.domain.entities.Annotation;
import be.mrtus.ocrbenchmark.persistence.AnnotationRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;

public class FileConverterC extends Thread {

	@Autowired
	private AnnotationRepository annotationRepository;
	@Autowired
	private FileConverterCConfig config;
	private final AtomicInteger count = new AtomicInteger();
	private ExecutorService executorService;
	private final Logger log = Logger.getLogger(FileConverterC.class.getName());
	private ArrayBlockingQueue<Path> queue;

	@Override
	public void run() {
		this.setupWorkers();

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
							this.log.log(Level.SEVERE, null, ex);
						}
					});
		} catch(IOException ex) {
			this.log.log(Level.SEVERE, null, ex);
		}

		try {
			this.executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
		} catch(InterruptedException ex) {
			Logger.getLogger(FileConverterC.class.getName()).log(Level.SEVERE, null, ex);
		}

		long end = System.currentTimeMillis();

		System.out.println("Converting took: " + Util.durationToString(end - start));
	}

	private Runnable createWorker() {
		return () -> {
			while(true) {
				while(this.queue.peek() == null) {
					if(this.executorService.isShutdown()) {
						break;
					}
				}

				Path p = this.queue.poll();

				if(p == null) {
					break;
				}

				try {
					long start = System.currentTimeMillis();

					String filename = p.getFileName().toString();
					String parentPathName = p.getParent().toString();

					parentPathName = parentPathName.replace(this.config.getImages(), this.config.getOutput());

					String[] split = filename.split("_");
					String fileContents = split[1];

					Path parentPath = Paths.get(parentPathName);

					filename = filename.substring(0, filename.length() - 4);
					Path newFile = parentPath.resolve(filename + ".txt");

//					try {
//						Files.createDirectories(parentPath);
//						Files.createFile(newFile);
//					} catch(IOException ex) {
//						Logger.getLogger(FileConverterA.class.getName()).log(Level.SEVERE, null, ex);
//					}
//
//					try(BufferedWriter bw = new BufferedWriter(new FileWriter(newFile.toFile()))) {
//						bw.write(fileContents);
//					} catch(Exception e) {
//						Logger.getLogger(FileConverterA.class.getName()).log(Level.SEVERE, null, e);
//					}
					this.annotationRepository.save(new Annotation(filename, fileContents));

					long end = System.currentTimeMillis();

					this.log.info("Processing file " + this.count.addAndGet(1) + " and took " + (end - start) + "ms");
				} catch(Exception e) {
					this.log.log(Level.SEVERE, null, e);
				}
			}
		};
	}

	private void setupWorkers() {
		this.queue = new ArrayBlockingQueue<>(this.config.getQueueSize());

		int threads = this.config.getThreads();
		this.executorService = Executors.newWorkStealingPool();
		IntStream.range(0, threads)
				.forEach(i -> this.executorService.execute(this.createWorker()));
	}
}
