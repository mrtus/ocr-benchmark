package be.mrtus.ocrbenchmark.persistence;

import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;

public interface BenchmarkResultRepository {

	public void save(BenchmarkResult result);

}
