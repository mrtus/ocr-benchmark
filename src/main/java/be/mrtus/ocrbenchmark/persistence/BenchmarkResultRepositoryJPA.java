package be.mrtus.ocrbenchmark.persistence;

import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

public class BenchmarkResultRepositoryJPA implements BenchmarkResultRepository {

	@Autowired
	private EntityManager entityManager;

	@Override
	public void save(BenchmarkResult result) {
		this.entityManager.merge(result);
	}
}
