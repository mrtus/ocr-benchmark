package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import be.mrtus.ocrbenchmark.persistence.BenchmarkResultRepository;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepository;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

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
			results = this.processResultRepository.findAllByBenchmarkResultId(this.result.getId());

			results.stream()
					.parallel()
					.forEach(r -> {
						String target = "";

						int errors = this.calculateLevenshteinDistance(target, r.getResult());

						r.setErrors(errors);

						this.processResultRepository.save(r);
					});
		} while(results.size() > 0);

//		double avgAccuracy = this.processResultRepository.findAvgAccuracyForBenchmarkResultId(this.result.getId());
//		this.result.setAvgAccuracy(avgAccuracy);
//		this.benchmarkResultRepository.save(this.result);
		long end = System.currentTimeMillis();

		String duration = Util.durationToString(end - start);

		this.logger.info("Processing results ended after " + duration);
	}

	private int calculateLevenshteinDistance(String target, String result) {
		int resultLength = result.length();
		int targetLenth = target.length();

		if(resultLength == 0) {
			return targetLenth;
		}

		if(targetLenth == 0) {
			return resultLength;
		}

		int[][] d = new int[resultLength + 1][targetLenth + 1];

		IntStream.range(0, resultLength)
				.forEach(i -> d[i][0] = i);

		IntStream.range(0, targetLenth)
				.forEach(j -> d[0][j] = j);

		IntStream.range(0, resultLength)
				.forEach(i -> {
					char resultChar = result.charAt(i);

					IntStream.range(0, targetLenth)
					.forEach(j -> {
						char targetChar = target.charAt(j);
						int cost;

						if(resultChar == targetChar) {
							cost = 0;
						} else {
							cost = 1;
						}

						d[i + 1][j + 1] = minimum(d[i][j + 1] + 1, d[i + 1][j] + 1, d[i][j] + cost);
					});
				});

		return d[resultLength][targetLenth];
	}

	private int minimum(int a, int b, int c) {
		int min = a;

		if(b < min) {
			min = b;
		}

		if(c < min) {
			min = c;
		}

		return min;
	}
}
