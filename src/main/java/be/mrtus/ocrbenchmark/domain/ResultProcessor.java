package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import be.mrtus.ocrbenchmark.persistence.BenchmarkResultRepository;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepository;
import java.util.List;
import java.util.OptionalDouble;
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
			results = this.processResultRepository.findAllByBenchmarkResultId(this.result.getId());

			results.stream()
					.parallel()
					.forEach(r -> {
						double accuracy = 0.0;

						r.setAccuracy(accuracy);

						this.processResultRepository.save(r);
					});

			OptionalDouble optional = results.stream()
					.mapToDouble(r -> r.getAccuracy())
					.average();

			if(!optional.isPresent()) {
				this.logger.severe("Not able to calculate avg accuracy for benchmark '" + this.result.getId() + "'");

				return;
			}

			this.result.setAvgAccuracy(optional.orElse(0));

			this.benchmarkResultRepository.save(this.result);
		} while(results.size() > 0);

		long end = System.currentTimeMillis();

		String duration = Util.durationToString(end - start);

		this.logger.info("Processing results ended after " + duration);
	}
}
