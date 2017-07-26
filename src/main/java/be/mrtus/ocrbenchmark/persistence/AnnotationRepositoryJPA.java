package be.mrtus.ocrbenchmark.persistence;

import be.mrtus.ocrbenchmark.domain.entities.Annotation;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

public class AnnotationRepositoryJPA implements AnnotationRepository {

	@Autowired
	private EntityManager entityManager;

	@Transactional
	@Override
	public void save(Annotation annotation) {
		this.entityManager.merge(annotation);
	}
}
