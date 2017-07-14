package be.mrtus.ocrbenchmark.persistence;

import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import java.util.List;
import java.util.UUID;

public interface ProcessResultRepository {

	public void save(ProcessResult processResult);

	public List<ProcessResult> findAllByBenchmarkResultId(UUID id);
}
