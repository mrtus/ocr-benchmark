package be.mrtus.ocrbenchmark.persistence;

import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

public class ProcessResultRepositoryJPA implements ProcessResultRepository {

	@Autowired
	private EntityManager entityManager;

	@Override
	public List<ProcessResult> findAllByBenchmarkResultId(UUID id, int offset, int size) {
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		CriteriaQuery<ProcessResult> criteria = builder.createQuery(ProcessResult.class);

		Root<ProcessResult> root = criteria.from(ProcessResult.class);

		Predicate where = builder.equal(root.get("benchmarkResult").get("id"), id);

		criteria.where(where);

		return this.entityManager.createQuery(criteria)
				.setFirstResult(offset)
				.setMaxResults(size)
				.getResultList();
	}

	@Override
	public List<ProcessResult> findFaultyFiles(UUID id, int maxWidth, int minTargetLength) {
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		CriteriaQuery<ProcessResult> criteria = builder.createQuery(ProcessResult.class);

		Root<ProcessResult> root = criteria.from(ProcessResult.class);

		Predicate where = builder.and(
				builder.equal(root.get("benchmarkResult").get("id"), id),
				builder.lt(root.get("width"), maxWidth),
				builder.gt(builder.length(root.get("target")), minTargetLength)
		);

		criteria.where(where);

		return this.entityManager.createQuery(criteria)
				.getResultList();
	}

	@Transactional
	@Override
	public void save(ProcessResult processResult) {
		this.entityManager.merge(processResult);
		this.entityManager.flush();
	}
}
