package be.mrtus.ocrbenchmark.domain.fileconverters;

import be.mrtus.ocrbenchmark.domain.Util;
import be.mrtus.ocrbenchmark.domain.entities.Annotation;
import be.mrtus.ocrbenchmark.persistence.AnnotationRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

public class FileConverterC extends Thread {

	@Autowired
	private AnnotationRepository annotationRepository;
	private AtomicInteger count = new AtomicInteger();
	private final Logger log = Logger.getLogger(FileConverterA.class.getName());
	private final ArrayBlockingQueue<Path> queue = new ArrayBlockingQueue<>(100);
	private List<Thread> workers = new ArrayList<>();
	private volatile boolean isLoading = true;

	@PostConstruct
	public void init() {
		IntStream.range(0, 16)
				.forEach(i -> {
					Thread thread = new Thread(() -> {
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
								System.out.println("Processing file: " + p);

								long starts = System.currentTimeMillis();

								String filename = p.getFileName().toString();
								String[] split = filename.split("_");
								String fileContents = split[1];

								this.annotationRepository.save(new Annotation(filename, fileContents));

								count.addAndGet(1);

								long ends = System.currentTimeMillis();

								System.out.println("Processed the " + count.get() + " file and took " + Util.durationToString(ends - starts));
							} catch(Exception e) {
								log.log(Level.SEVERE, null, e);
							}
						}
					});

					thread.start();
				});
	}

	@Override
	public void run() {
		Path path = Paths.get("h:/images3");

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

		long end = System.currentTimeMillis();

		this.workers.forEach(t -> {
			try {
				t.join();
			} catch(InterruptedException ex) {
				Logger.getLogger(FileConverterC.class.getName()).log(Level.SEVERE, null, ex);
			}
		});

		System.out.println("Converting took: " + Util.durationToString(end - start));
	}
}
