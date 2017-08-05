package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.application.config.properties.ResultAnalyserConfig;
import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import be.mrtus.ocrbenchmark.domain.entities.GroupPartition;
import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import be.mrtus.ocrbenchmark.persistence.BenchmarkResultRepository;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
			this.logger.info("Retrieving for offset " + offset);

			partialResults = this.processResultRepository.findAllByBenchmarkResultId(
					result.getId(),
					offset * size,
					size
			);

			results.addAll(partialResults);

			offset++;
		} while(partialResults.size() > 0);

		this.logger.info("========== Results ==========");

		OptionalDouble avgDuration = this.calculateAvgDuration(results);
		this.logger.info("avg duration " + avgDuration.getAsDouble());

		OptionalDouble avgErrorRate = this.calculateAvgErrorRate(results);
		this.logger.info("avg accuracy: " + (1 - avgErrorRate.getAsDouble()));

		OptionalDouble minPixels = this.calculateMinimumPixels(results);
		this.logger.info("min pixels: " + minPixels.getAsDouble());

		OptionalDouble maxPixels = this.calculateMaximumPixels(results);
		this.logger.info("max pixels: " + maxPixels.getAsDouble());

		Map<GroupPartition, List<ProcessResult>> partitionedMap = this.calculatePartitions(results, minPixels, maxPixels);
		List<GroupPartition> partitionGroups = this.calculatePartitionDetails(partitionedMap);
		this.logger.info("Group partition results");
		partitionGroups.stream()
				.sorted((g1, g2) -> Double.compare(g1.getMinValue(), g2.getMinValue()))
				.forEach(g -> {
					this.logger.info("group " + g.getId() + ": range: " + g.getMinValue() + " - " + g.getMaxValue());
					this.logger.info("\t group size: " + g.getGroupSize());
					this.logger.info("\t avg duration: " + g.getAvgDuration());
					this.logger.info("\t avg error rate: " + g.getAvgErrorRate());
				});

		this.logger.info("Group partition results summary sorted on avg duration and group size > 1000");
		partitionGroups.stream()
				.filter(g -> g.getGroupSize() > 1000)
				.sorted((g1, g2) -> Double.compare(g1.getAvgDuration(), g2.getAvgDuration()))
				.forEach(g -> this.logger.info(
								"group " + g.getId() + ":"
								+ " group size: " + g.getGroupSize()
								+ " avg duration: " + g.getAvgDuration()
						)
				);

		long end = System.currentTimeMillis();

		this.logger.info("Processing results ended after " + Util.durationToString(end - start));
		this.logger.info("======== End Results ========");
	}

	private OptionalDouble calculateAvgDuration(List<ProcessResult> results) {
		return results.stream()
				.parallel()
				.mapToDouble(r -> r.getDuration())
				.average();
	}

	private OptionalDouble calculateAvgErrorRate(
			List<ProcessResult> results) {
		return results.stream()
				.parallel()
				.mapToDouble(r -> r.getErrorRate())
				.average();
	}

	private OptionalDouble calculateMaximumPixels(List<ProcessResult> results) {
		return results.stream()
				.parallel()
				.mapToDouble(r -> r.getPixelCount())
				.max();
	}

	private OptionalDouble calculateMinimumPixels(List<ProcessResult> results) {
		return results.stream()
				.parallel()
				.mapToDouble(r -> r.getPixelCount())
				.min();
	}

	private List<GroupPartition> calculatePartitionDetails(Map<GroupPartition, List<ProcessResult>> partitionedMap) {
		return partitionedMap.entrySet()
				.stream()
				.map(e -> {
					GroupPartition group = e.getKey();
					List<ProcessResult> results = e.getValue();

					OptionalDouble avgDuration = this.calculateAvgDuration(results);
					group.setAvgDuration(avgDuration.getAsDouble());

					OptionalDouble avgErrorRate = this.calculateAvgErrorRate(results);
					group.setAvgErrorRate(avgErrorRate.getAsDouble());

					group.setGroupSize(results.size());

					return group;
				})
				.collect(Collectors.toList());
	}

	private Map<GroupPartition, List<ProcessResult>> calculatePartitions(List<ProcessResult> results, OptionalDouble minPixels, OptionalDouble maxPixels) {
		double pixelDiff = maxPixels.getAsDouble() - minPixels.getAsDouble();

		int numPartitions = this.config.getPartitionSize();
		double range = Math.ceil(pixelDiff / numPartitions);

		List<GroupPartition> partitions = new ArrayList<>();
		GroupPartition startGroup = new GroupPartition(1, minPixels.getAsDouble() - 1, minPixels.getAsDouble() + range);
		partitions.add(startGroup);
		GroupPartition previousGroup = startGroup;
		for(int i = 1; i <= numPartitions; i++) {
			GroupPartition group = new GroupPartition(i + 1, previousGroup.getMaxValue(), previousGroup.getMaxValue() + range);

			partitions.add(group);

			previousGroup = group;
		}

		return results.stream()
				.sorted((r1, r2) -> Double.compare(r1.getPixelCount(), r2.getPixelCount()))
				.collect(Collectors.groupingBy(r -> this.findGroupPartition(partitions, r.getPixelCount())));
	}

	private GroupPartition findGroupPartition(List<GroupPartition> partitions, double pixelCount) {
		return partitions.stream()
				.filter(g -> g.between(pixelCount))
				.findFirst().orElseGet(() -> {
					this.logger.warning("Cannot find GroupPartition for pixelCount: " + pixelCount);
					return null;
				});
	}
}
