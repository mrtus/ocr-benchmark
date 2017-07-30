package be.mrtus.ocrbenchmark.persistence;

import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import java.util.UUID;

public interface BenchmarkResultRepository {

	public BenchmarkResult findById(UUID id);

	public void save(BenchmarkResult result);
}
