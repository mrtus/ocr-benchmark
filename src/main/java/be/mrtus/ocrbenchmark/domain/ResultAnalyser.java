package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.application.config.properties.ResultAnalyserConfig;
import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import be.mrtus.ocrbenchmark.persistence.BenchmarkResultRepository;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ResultAnalyser extends Thread {

	@Autowired
	private BenchmarkResultRepository benchmarkResultRepository;
	@Autowired
	private ResultAnalyserConfig config;
	private final Logger logger = Logger.getLogger(ResultAnalyser.class.getName());
	@Autowired
	private ProcessResultRepository processResultRepository;

	@Override
	public void run() {
		this.logger.info("Processing results");

		long start = System.currentTimeMillis();

		BenchmarkResult result = this.benchmarkResultRepository.findById(this.config.getBenchmarkResultId());

		List<ProcessResult> results = new ArrayList<>();

		int offset = 0;
		int size = 100_000;
		List<ProcessResult> partialResults;
		do {
			this.logger.info("Retreiving for offset " + offset);

			partialResults = this.processResultRepository.findAllByBenchmarkResultId(
					result.getId(),
					offset * size,
					size
			);

			results.addAll(partialResults);

			offset++;
		} while(partialResults.size() > 0);

		OptionalDouble avgDuration = results.stream()
				.parallel()
				.mapToDouble(r -> r.getDuration())
				.average();

		OptionalDouble avgErrorRate = results.stream()
				.parallel()
				.mapToDouble(r -> r.getErrorRate())
				.average();

		long end = System.currentTimeMillis();

		System.out.println("avg duration " + avgDuration.getAsDouble());
		System.out.println("avg accuracy: " + (1 - avgErrorRate.getAsDouble()));

		this.logger.info("Processing results ended after " + Util.durationToString(end - start));
	}
}
