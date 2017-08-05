package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.application.config.properties.DeleterConfig;
import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class Deleter extends Thread {

	@Autowired
	private DeleterConfig config;
	@Autowired
	private ProcessResultRepository repository;
	private final Logger logger = Logger.getLogger(Deleter.class.getName());

	@Override
	public void run() {
		List<ProcessResult> results = this.repository.findFaultyFiles(
				this.config.getBenchmarkResultId(),
				this.config.getMaxWidth(),
				this.config.getMinTargetLength()
		);

		this.logger.info("Deleting " + results.size() + " items");

		results.forEach(r -> {
			try {
				this.logger.info("Deleting " + r.getPath());
				Files.deleteIfExists(r.getPath());
			} catch(IOException ex) {
				this.logger.log(Level.SEVERE, null, ex);
			}
		});
	}
}
