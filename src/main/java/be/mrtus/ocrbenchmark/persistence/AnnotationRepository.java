package be.mrtus.ocrbenchmark.persistence;

import be.mrtus.ocrbenchmark.domain.entities.Annotation;

public interface AnnotationRepository {

	public Annotation findByFilename(String filename);

	public void save(Annotation annotation);
}
