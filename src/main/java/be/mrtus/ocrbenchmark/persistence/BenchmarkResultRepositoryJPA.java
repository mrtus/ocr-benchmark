package be.mrtus.ocrbenchmark.persistence;

import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

public class BenchmarkResultRepositoryJPA implements BenchmarkResultRepository {

	@Autowired
	private EntityManager entityManager;

	@Transactional
	@Override
	public BenchmarkResult findById(UUID id) {
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		CriteriaQuery<BenchmarkResult> criteria = builder.createQuery(BenchmarkResult.class);

		Root<BenchmarkResult> root = criteria.from(BenchmarkResult.class);

		Predicate where = builder.equal(root.get("id"), id);

		criteria.where(where);

		return this.entityManager.createQuery(criteria)
				.getSingleResult();
	}

	@Transactional
	@Override
	public void save(BenchmarkResult result) {
		this.entityManager.merge(result);
	}
}
