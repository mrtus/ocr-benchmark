package be.mrtus.ocrbenchmark.persistence;

import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

public class ProcessResultRepositoryJPA implements ProcessResultRepository {

	@Autowired
	private EntityManager entityManager;

	@Override
	public void add(ProcessResult processResult) {
		this.entityManager.merge(processResult);
	}
}
