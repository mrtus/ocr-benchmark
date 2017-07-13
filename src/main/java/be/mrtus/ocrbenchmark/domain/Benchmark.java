package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.application.config.properties.BenchmarkConfig;
import be.mrtus.ocrbenchmark.domain.entities.LoadedFile;
import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class Benchmark extends Thread {

	@Autowired
	private BenchmarkConfig config;
	private long endTime;
	@Autowired
	private FileLoader fileLoader;
	private final Logger logger = Logger.getLogger(Benchmark.class.getName());
	private long startTime;

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

		try {
			this.doBenchmark();
		} catch(InterruptedException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		}

		this.endBenchmark();

		System.exit(0);
	}

	private void doBenchmark() throws InterruptedException {
		ArrayBlockingQueue<LoadedFile> queue = this.fileLoader.getQueue();

		while(true) {
			while(queue.peek() == null) {
				if(!this.fileLoader.isLoading()) {
					break;
				}
			}

			LoadedFile lf = queue.poll();

			if(lf == null) {
				break;
			}

			this.processFile(lf);
		}
	}

	private void endBenchmark() {
		this.endTime = System.currentTimeMillis();

		String duration = this.durationToString(this.endTime - this.startTime);

		this.logger.info("Benchmark ended after " + duration);
		this.logger.info("====================");
	}

	private void prepareBenchmark() {
		this.logger.info("====================");
		this.logger.info("Preparing benchmark");

		this.fileLoader.start();

		try {
			this.logger.info("Sleeping 5000 ms");
			Thread.sleep(5000);
		} catch(InterruptedException ex) {
			Logger.getLogger(Benchmark.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void processFile(LoadedFile lf) {
		this.logger.info("Processing file " + lf.getPath().toString());
		
	}

	private void startBenchmark() {
		this.logger.info("Starting benchmark");

		this.startTime = System.currentTimeMillis();
	}
}
