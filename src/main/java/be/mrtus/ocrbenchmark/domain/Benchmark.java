package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.application.config.properties.BenchmarkConfig;
import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import be.mrtus.ocrbenchmark.persistence.BenchmarkResultRepository;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepository;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;

public class Benchmark extends Thread {

	@Autowired
	private BenchmarkResultRepository benchmarkResultRepository;
	@Autowired
	private BenchmarkConfig config;
	private long endTime;
	@Autowired
	private FileLoader fileLoader;
	private final Logger logger = Logger.getLogger(Benchmark.class.getName());
	@Autowired
	private ProcessResultRepository processResultRepository;
	private final List<Processor> processors = new ArrayList<>();
	private BenchmarkResult result;
	private long startTime;

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

	@Override
	public void run() {
		this.prepareBenchmark();

		this.startBenchmark();

		this.doBenchmark();

		this.endBenchmark();

		System.exit(0);
	}

	private void doBenchmark() {
		this.processors.forEach(p -> p.start());

		this.processors.forEach(p -> {
			try {
				p.join();
			} catch(InterruptedException ex) {
				this.logger.log(Level.SEVERE, null, ex);
			}
		});
	}

	private void endBenchmark() {
		this.endTime = System.currentTimeMillis();

		String duration = this.durationToString(this.endTime - this.startTime);

		this.result.setDuration(this.endTime - this.startTime);

		this.benchmarkResultRepository.save(this.result);

		this.logger.info("Benchmark ended after " + duration);
		this.logger.info("====================");
	}

	private void prepareBenchmark() {
		this.logger.info("====================");
		this.logger.info("Preparing benchmark");

		this.fileLoader.start();

		int size = this.config.getParallelBenchmarks();

		this.result = new BenchmarkResult();

		this.benchmarkResultRepository.save(this.result);

		IntStream.range(0, size)
				.forEach(id -> {
					Processor processor = new Processor(
							id,
							this.fileLoader,
							this.processResultRepository,
							this.result
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

	private void startBenchmark() {
		this.logger.info("Starting benchmark");

		this.startTime = System.currentTimeMillis();
	}
}
