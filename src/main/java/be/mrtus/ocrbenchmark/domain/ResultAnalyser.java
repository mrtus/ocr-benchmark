package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.application.config.properties.ResultAnalyserConfig;
import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import be.mrtus.ocrbenchmark.persistence.BenchmarkResultRepository;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepository;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;

public class ResultAnalyser extends Thread {

	@Autowired
	private BenchmarkResultRepository benchmarkResultRepository;
	@Autowired
	private ResultAnalyserConfig config;
	private ExecutorService executorService;
	private volatile boolean isDone = false;
	private final Logger logger = Logger.getLogger(ResultAnalyser.class.getName());
	@Autowired
	private ProcessResultRepository processResultRepository;
	private ArrayBlockingQueue<ProcessResult> queue;

	@Override
	public void run() {
		this.logger.info("Processing results");

		long start = System.currentTimeMillis();

		this.queue = new ArrayBlockingQueue<>(this.config.getQueueSize());

		int threads = this.config.getThreads();
		this.executorService = Executors.newFixedThreadPool(threads);
		IntStream.range(0, threads)
				.forEach(i -> this.executorService.execute(this.createWorker()));

		BenchmarkResult result = this.benchmarkResultRepository.findById(this.config.getBenchmarkResultId());

		List<ProcessResult> results = new ArrayList<>();

		int offset = 0;
		int size = 100_000;
		List<ProcessResult> partialResults;
		do {
			this.logger.info("Retreiving for offset " + offset);

			partialResults = this.processResultRepository.findAllByBenchmarkResultId(
					result.getId(),
					offset * size,
					size
			);

			results.addAll(partialResults);

			partialResults.forEach(r -> {
				try {
					this.queue.put(r);
				} catch(InterruptedException ex) {
					this.logger.log(Level.SEVERE, null, ex);
				}
			});

			offset++;
		} while(partialResults.size() > 0);

		this.isDone = true;

		try {
			this.executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
		} catch(InterruptedException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		}

		OptionalDouble avgDuration = results.stream()
				.parallel()
				.mapToDouble(r -> r.getDuration())
				.average();

		OptionalDouble avgErrorRate = results.stream()
				.parallel()
				.mapToDouble(r -> r.getErrorRate())
				.average();

		long end = System.currentTimeMillis();

		System.out.println("avg duration " + avgDuration.getAsDouble());
		System.out.println("avg accuracy: " + (1 - avgErrorRate.getAsDouble()));

		this.logger.info("Processing results ended after " + Util.durationToString(end - start));
	}

	private void calculatePixels(ProcessResult result) {
		try {
			BufferedImage bi = ImageIO.read(result.getPath().toFile());

			result.setImageWidth(bi.getWidth());
			result.setImageHeight(bi.getHeight());

			this.logger.info("Size " + bi.getWidth() + " x " + bi.getHeight());
		} catch(IOException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		}
	}

	private Runnable createWorker() {
		return () -> {
			while(true) {
				while(this.queue.peek() == null) {
					if(this.isDone) {
						break;
					}
				}

				ProcessResult result = this.queue.poll();
				if(result == null) {
					break;
				}

				this.calculatePixels(result);

				this.processResultRepository.save(result);

				this.logger.info("Processed result from queue, remaining items: " + this.queue.size());
			}
		};
	}
}
