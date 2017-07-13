package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import be.mrtus.ocrbenchmark.domain.entities.LoadedFile;
import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepository;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

public class Processor extends Thread {

	private final FileLoader fileLoader;
	private final int id;
	private final Logger logger = Logger.getLogger(Processor.class.getName());
	private final BenchmarkResult result;
	private final ProcessResultRepository resultRepository;

	public Processor(
			int id,
			FileLoader fileLoader,
			ProcessResultRepository resultRepository,
			BenchmarkResult result
	) {
		this.id = id;
		this.fileLoader = fileLoader;
		this.resultRepository = resultRepository;
		this.result = result;

		this.setDaemon(true);
		this.setName("Benchmark Processor " + this.id);
	}

	@Override
	public void run() {
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

	private void processFile(LoadedFile lf) {
		this.logger.info("Processing file " + lf.getPath().toString());

		long start = System.currentTimeMillis();

		String result = "";

		long end = System.currentTimeMillis();

		ProcessResult processResult = new ProcessResult();

		processResult.setBenchmarkResult(this.result);
		processResult.setPath(lf.getPath());
		processResult.setResult(result);
		processResult.setDuration(end - start);

		this.resultRepository.add(processResult);
	}
}