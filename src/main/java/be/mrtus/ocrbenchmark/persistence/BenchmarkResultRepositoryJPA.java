package be.mrtus.ocrbenchmark.persistence;

import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

public class BenchmarkResultRepositoryJPA implements BenchmarkResultRepository {

	@Autowired
	private EntityManager entityManager;

	@Transactional
	@Override
	public BenchmarkResult findById(UUID id) {
		return null;
	}

	@Transactional
	@Override
	public void save(BenchmarkResult result) {
		this.entityManager.merge(result);
	}
}
