package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.application.config.properties.BenchmarkConfig;
import java.time.Duration;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class Benchmark extends Thread {

	@Autowired
	private BenchmarkConfig config;
	private long endTime;
	private final Logger logger = Logger.getLogger(Benchmark.class.getName());
	private long startTime;
	@Autowired
	private FileLoader fileLoader;

	public String durationToString(long time) {
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

		this.endBenchmark();
	}

	private void endBenchmark() {
		this.endTime = System.currentTimeMillis();

		String duration = this.durationToString(this.endTime - this.startTime);

		this.logger.info("Benchmark ended after " + duration);
		this.logger.info("====================");
	}

	private void prepareBenchmark() {
		this.fileLoader.start();
	}

	private void startBenchmark() {
		this.logger.info("====================");
		this.logger.info("Benchmark started");
		this.startTime = System.currentTimeMillis();
	}
}
