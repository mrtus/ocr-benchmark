package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.application.config.properties.BenchmarkConfig;
import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import be.mrtus.ocrbenchmark.persistence.BenchmarkResultRepository;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepository;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;

public class Benchmark extends Thread {

	@Autowired
	private BenchmarkResultRepository benchmarkResultRepository;
	@Autowired
	private BenchmarkConfig config;
	@Autowired
	private FileLoader fileLoader;
	private final Logger logger = Logger.getLogger(Benchmark.class.getName());
	@Autowired
	private ProcessResultRepository processResultRepository;
	private final List<Processor> processors = new ArrayList<>();

	@Override
	public void run() {
		this.startBenchmark();

		BenchmarkResult result = new BenchmarkResult();

		this.benchmarkResultRepository.save(result);

		this.prepareBenchmark(result);

		this.doBenchmark(result);

		this.processBenchmarkResults(result);

		this.endBenchmark();

		System.exit(0);
	}

	private void calculateAccuracy(BenchmarkResult result, List<ProcessResult> results) {
		results.forEach(r -> {
			double accuracy = 0.0;

			r.setAccuracy(accuracy);

			this.processResultRepository.save(r);
		});

		OptionalDouble optional = results.stream()
				.mapToDouble(r -> r.getAccuracy())
				.average();

		if(!optional.isPresent()) {
			this.logger.severe("Not able to calculate avg accuracy for benchmark '" + result.getId() + "'");

			return;
		}

		result.setAvgAccuracy(optional.orElse(0));

		this.benchmarkResultRepository.save(result);

	}

	private void doBenchmark(BenchmarkResult result) {
		this.logger.info("Starting benchmark");

		long start = System.currentTimeMillis();

		this.processors.forEach(p -> p.start());

		this.processors.forEach(p -> {
			try {
				p.join();
			} catch(InterruptedException ex) {
				this.logger.log(Level.SEVERE, null, ex);
			}
		});

		long end = System.currentTimeMillis();

		result.setDuration(end - start);

		this.benchmarkResultRepository.save(result);

		String duration = this.durationToString(end - start);

		this.logger.info("Benchmark ended after " + duration);
	}

	private String durationToString(long time) {
		Duration duration = Duration.ofMillis(time);

		long hours = duration.toHours();

		Duration minutesDuration = duration.minusHours(hours);
		long minutes = minutesDuration.toMinutes();

		Duration secondsDuration = minutesDuration.minusMinutes(minutes);
		long seconds = secondsDuration.getSeconds();

		Duration milliesDuratoin = secondsDuration.minusSeconds(seconds);
		long millies = milliesDuratoin.toMillis();

		String durationString = hours + "h "
								+ minutes + "m "
								+ seconds + "." + millies + "s ";

		return durationString;
	}

	private void endBenchmark() {
		this.logger.info("======================");
		this.logger.info("=  Benchmark  ended  =");
		this.logger.info("======================");
	}

	private void prepareBenchmark(BenchmarkResult result) {
		this.logger.info("Preparing benchmark");

		this.fileLoader.start();

		int size = this.config.getParallelBenchmarks();

		IntStream.range(0, size)
				.forEach(id -> {
					Processor processor = new Processor(
							id,
							this.config,
							this.fileLoader,
							this.processResultRepository,
							result
					);

					this.processors.add(processor);
				});

		try {
			this.logger.info("Sleeping 5000 ms");

			Thread.sleep(5000);
		} catch(InterruptedException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		}
	}

	private void processBenchmarkResults(BenchmarkResult result) {
		this.logger.info("Processing results");

		long start = System.currentTimeMillis();

		List<ProcessResult> results = this.processResultRepository.findAllByBenchmarkResultId(result.getId());

		this.calculateAccuracy(result, results);

		long end = System.currentTimeMillis();

		String duration = this.durationToString(end - start);

		this.logger.info("Processing results ended after " + duration);
	}

	private void startBenchmark() {
		this.logger.info("======================");
		this.logger.info("= Benchmark  started =");
		this.logger.info("======================");
	}
}
