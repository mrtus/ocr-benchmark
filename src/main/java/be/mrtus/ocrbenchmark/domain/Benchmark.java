package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.application.config.properties.BenchmarkConfig;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class Benchmark extends Thread {

	@Autowired
	private BenchmarkConfig config;
	@Autowired
	private final Logger logger = Logger.getLogger(Benchmark.class.getName());

	@Override
	public void run() {

	}
}
