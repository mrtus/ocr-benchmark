package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.application.config.properties.BenchmarkConfig;
import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import be.mrtus.ocrbenchmark.domain.entities.LoadedFile;
import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import be.mrtus.ocrbenchmark.domain.libraries.OCRLibrary;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepository;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

public class Processor extends Thread {

	private final BenchmarkConfig config;
	private final FileLoader fileLoader;
	private final int id;
	private final OCRLibrary library;
	private final Logger logger = Logger.getLogger(Processor.class.getName());
	private final BenchmarkResult result;
	private final ProcessResultRepository resultRepository;

	public Processor(
			int id,
			BenchmarkConfig config,
			FileLoader fileLoader,
			ProcessResultRepository resultRepository,
			BenchmarkResult result,
			OCRLibrary library
	) {
		this.id = id;
		this.config = config;
		this.fileLoader = fileLoader;
		this.resultRepository = resultRepository;
		this.result = result;
		this.library = library;

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

			this.doOcr(lf);
		}
	}

	private void doOcr(LoadedFile lf) {
		long start = System.currentTimeMillis();

		String result = this.library.doOCR(lf);

		long end = System.currentTimeMillis();

		ProcessResult processResult = new ProcessResult();

		processResult.setBenchmarkResult(this.result);
		processResult.setPath(lf.getPath());
		processResult.setResult(result);
		processResult.setTarget(lf.getTarget());
		processResult.setDuration(end - start);

		this.resultRepository.save(processResult);

		this.logger.info("Result for " + lf.getPath() + ": " + result);
	}
}
