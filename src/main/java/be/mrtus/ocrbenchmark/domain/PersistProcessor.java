package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepository;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersistProcessor extends Thread {

	private final Benchmark benchmark;
	private final ProcessResultRepository processResultRepository;
	private final ArrayBlockingQueue<ProcessResult> queue;
	private final Logger logger = Logger.getLogger(PersistProcessor.class.getName());

	public PersistProcessor(
			Benchmark benchmark,
			ProcessResultRepository processResultRepository,
			ArrayBlockingQueue<ProcessResult> queue
	) {
		this.benchmark = benchmark;
		this.processResultRepository = processResultRepository;
		this.queue = queue;
	}

	@Override
	public void run() {
		while(true) {
			while(this.queue.peek() == null) {
				if(this.benchmark.isDone()) {
					break;
				}
			}

			ProcessResult result = this.queue.poll();

			if(result == null) {
				break;
			}

			long start = System.currentTimeMillis();

			try {
				this.processResultRepository.save(result);
			} catch(Exception e) {
				this.logger.log(Level.SEVERE, null, e);
			}

			long end = System.currentTimeMillis();

			this.logger.info("Saving result took " + (end - start) + "ms, Save queue size: " + this.queue.size());
		}
	}
}
