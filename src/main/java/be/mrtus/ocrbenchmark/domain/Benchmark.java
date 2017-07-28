package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.application.config.properties.BenchmarkConfig;
import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import be.mrtus.ocrbenchmark.domain.libraries.OCRLibrary;
import be.mrtus.ocrbenchmark.domain.libraries.OCRLibraryFactory;
import be.mrtus.ocrbenchmark.persistence.BenchmarkResultRepository;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;

public class Benchmark extends Thread {

	@Autowired
	private BenchmarkResultRepository benchmarkResultRepository;
	@Autowired
	private BenchmarkConfig config;
	private volatile boolean done = false;
	@Autowired
	private FileLoader fileLoader;
	@Autowired
	private OCRLibraryFactory libraryFactory;
	private final Logger logger = Logger.getLogger(Benchmark.class.getName());
	@Autowired
	private ProcessResultRepository processResultRepository;
	private ExecutorService processorExecutor;
	private final List<Processor> processors = new ArrayList<>();
	private ArrayBlockingQueue<ProcessResult> queue;
	private ExecutorService savingExecutor;

	public boolean isDone() {
		return this.done;
	}

	@Override
	public void run() {
		this.startBenchmark();

		BenchmarkResult result = new BenchmarkResult();

		this.benchmarkResultRepository.save(result);

		this.prepareBenchmark(result);

		this.doBenchmark(result);

		this.processBenchmarkResults(result);

		this.endBenchmark();
	}

	private void doBenchmark(BenchmarkResult result) {
		this.logger.info("Starting benchmark");

		long start = System.currentTimeMillis();

		this.processors.forEach(p -> this.processorExecutor.execute(p));

		try {
			this.processorExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);

			this.done = true;

			this.savingExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
		} catch(InterruptedException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		}

		long end = System.currentTimeMillis();

		result.setDuration(end - start);

		this.benchmarkResultRepository.save(result);

		String duration = Util.durationToString(end - start);

		this.logger.info("Benchmark ended after " + duration);
	}

	private void endBenchmark() {
		this.logger.info("======================");
		this.logger.info("=  Benchmark  ended  =");
		this.logger.info("======================");
	}

	private void prepareBenchmark(BenchmarkResult result) {
		this.logger.info("Preparing benchmark");

		int size = this.config.getBenchmarkThreads();
		this.processorExecutor = Executors.newFixedThreadPool(size);

		int saveQueue = this.config.getSaveQueueSize();
		this.queue = new ArrayBlockingQueue<>(saveQueue);

		int saveThreads = this.config.getSaveThreads();
		this.savingExecutor = Executors.newFixedThreadPool(saveThreads + 1);

		this.savingExecutor.execute(this.fileLoader);

		IntStream.range(0, saveThreads)
				.forEach(i -> {
					PersistProcessor processor = new PersistProcessor(
							this,
							this.processResultRepository,
							this.queue
					);

					this.savingExecutor.execute(processor);
				});

		IntStream.range(0, size)
				.forEach(id -> {
					OCRLibrary library = this.libraryFactory.build(this.config.getLibrary());

					Processor processor = new Processor(
							id,
							this.config,
							this.fileLoader,
							this.processResultRepository,
							this.queue,
							result,
							library
					);

					this.processors.add(processor);
				});

		try {
			this.logger.info("Waiting 5000 ms for threads to spin up");

			Thread.sleep(5000);
		} catch(InterruptedException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		}
	}

	private void processBenchmarkResults(BenchmarkResult result) {
		ResultProcessor processor = new ResultProcessor(
				this.benchmarkResultRepository,
				this.processResultRepository,
				result
		);

		this.savingExecutor.execute(processor);

		try {
			this.savingExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
		} catch(InterruptedException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		}
	}

	private void startBenchmark() {
		this.logger.info("======================");
		this.logger.info("= Benchmark  started =");
		this.logger.info("======================");
	}
}
