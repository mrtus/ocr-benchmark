package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import be.mrtus.ocrbenchmark.domain.entities.LoadedFile;
import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import be.mrtus.ocrbenchmark.domain.libraries.OCRLibrary;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Processor extends Thread {

	private final AtomicInteger count;
	private final FileLoader fileLoader;
	private ArrayBlockingQueue<LoadedFile> fileQueue;
	private final int id;
	private final OCRLibrary library;
	private final Logger logger = Logger.getLogger(Processor.class.getName());
	private final ArrayBlockingQueue<ProcessResult> saveQueue;
	private final BenchmarkResult result;

	public Processor(
			int id,
			FileLoader fileLoader,
			ArrayBlockingQueue<ProcessResult> saveQueue,
			BenchmarkResult result,
			OCRLibrary library,
			AtomicInteger count
	) {
		this.id = id;
		this.fileLoader = fileLoader;
		this.saveQueue = saveQueue;
		this.result = result;
		this.library = library;
		this.count = count;

		this.setDaemon(true);
		this.setName("Benchmark Processor " + this.id);
	}

	@Override
	public void run() {
		this.fileQueue = this.fileLoader.getQueue();

		while(true) {
			while(this.fileQueue.peek() == null) {
				if(!this.fileLoader.isLoading()) {
					break;
				}
			}

			LoadedFile lf = this.fileQueue.poll();

			if(lf == null) {
				break;
			}

			this.doOcr(lf);
		}
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
		this.calculatePixels(processResult);

		int errors = Util.calculateLevenshteinDistance(processResult.getTarget(), processResult.getResult());

		processResult.setErrors(errors);

		try {
			this.saveQueue.put(processResult);
		} catch(InterruptedException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		}

		this.logger.info("OCR " + this.count.addAndGet(1)
						 + " took "
						 + (end - start)
						 + "ms "
						 + " File queue size: "
						 + this.fileQueue.size());
	}
}
