package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import be.mrtus.ocrbenchmark.persistence.BenchmarkResultRepository;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepository;
import java.util.List;
import java.util.logging.Logger;

public class ResultProcessor extends Thread {

	private final BenchmarkResultRepository benchmarkResultRepository;
	private final Logger logger = Logger.getLogger(ResultProcessor.class.getName());
	private final ProcessResultRepository processResultRepository;
	private final BenchmarkResult result;

	public ResultProcessor(
			BenchmarkResultRepository benchmarkResultRepository,
			ProcessResultRepository processResultRepository,
			BenchmarkResult result
	) {
		this.benchmarkResultRepository = benchmarkResultRepository;
		this.processResultRepository = processResultRepository;
		this.result = result;
	}

	@Override
	public void run() {
		this.logger.info("Processing results");

		long start = System.currentTimeMillis();

		int offset = 0;
		int size = 100;

		List<ProcessResult> results;
		do {
			results = this.processResultRepository.findAllByBenchmarkResultId(
					this.result.getId(),
					offset * size,
					size
			);

			results.stream()
					.parallel()
					.forEach(r -> {
						//int errors = Util.calculateLevenshteinDistance(r.getTarget(), r.getResult());

						//r.setErrors(errors);
						//this.processResultRepository.save(r);
					});

			offset++;
		} while(results.size() > 0);

//		double avgAccuracy = this.processResultRepository.findAvgAccuracyForBenchmarkResultId(this.result.getId());
//		this.result.setAvgAccuracy(avgAccuracy);
//		this.benchmarkResultRepository.save(this.result);
		long end = System.currentTimeMillis();

		String duration = Util.durationToString(end - start);

		this.logger.info("Processing results ended after " + duration);
	}
}
