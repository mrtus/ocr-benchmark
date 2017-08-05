package be.mrtus.ocrbenchmark.persistence;

import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import java.util.List;
import java.util.UUID;

public interface ProcessResultRepository {

	public List<ProcessResult> findAllByBenchmarkResultId(UUID id, int offset, int size);

	public List<ProcessResult> findFaultyFiles(UUID id, int maxWidth, int minTargetLength);

	public void save(ProcessResult processResult);
}
