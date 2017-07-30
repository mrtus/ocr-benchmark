package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import be.mrtus.ocrbenchmark.persistence.BenchmarkResultRepository;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepository;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ResultAnalyser extends Thread {

	@Autowired
	private BenchmarkResultRepository benchmarkResultRepository;

	private final Logger logger = Logger.getLogger(ResultAnalyser.class.getName());
	@Autowired
	private ProcessResultRepository processResultRepository;

	@Override
	public void run() {
		this.logger.info("Processing results");

		long start = System.currentTimeMillis();

		BenchmarkResult result = this.benchmarkResultRepository.findById();

		int offset = 0;
		int size = 100;

		List<ProcessResult> results;
		do {
			results = this.processResultRepository.findAllByBenchmarkResultId(
					result.getId(),
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

		long end = System.currentTimeMillis();

		this.logger.info("Processing results ended after " + Util.durationToString(end - start));
	}
}
