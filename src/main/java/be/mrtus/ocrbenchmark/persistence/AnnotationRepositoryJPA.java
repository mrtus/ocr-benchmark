package be.mrtus.ocrbenchmark.persistence;

import be.mrtus.ocrbenchmark.domain.entities.Annotation;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

public class AnnotationRepositoryJPA implements AnnotationRepository {

	@Autowired
	private EntityManager entityManager;

	@Transactional
	@Override
	public Annotation findByFilename(String filename) {
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		CriteriaQuery<Annotation> criteria = builder.createQuery(Annotation.class);

		Root<Annotation> root = criteria.from(Annotation.class);

		Predicate where = builder.equal(root.get("filename"), filename);

		criteria.where(where);

		return this.entityManager.createQuery(criteria)
				.getSingleResult();
	}

	@Transactional
	@Override
	public void save(Annotation annotation) {
		this.entityManager.merge(annotation);
	}
}
