package be.mrtus.ocrbenchmark.domain.entities;

import be.mrtus.ocrbenchmark.persistence.convertors.PathConverter;
import be.mrtus.ocrbenchmark.persistence.convertors.UUIDConverter;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.UUID;
import javax.persistence.*;

@Entity
public class ProcessResult implements Serializable {

	private static final long serialVersionUID = 1L;
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = false)
	private BenchmarkResult benchmarkResult;
	private long duration;
	private int errors;
	@Id
	@Convert(converter = UUIDConverter.class)
	private UUID id;
	@Convert(converter = PathConverter.class)
	private Path path;
	private String result;
	private String target;

	public ProcessResult() {
		this.id = UUID.randomUUID();
	}

	public double getErrorRate() {
		return this.errors / this.target.length();
	}

	public BenchmarkResult getBenchmarkResult() {
		return this.benchmarkResult;
	}

	public long getDuration() {
		return this.duration;
	}

	public int getErrors() {
		return this.errors;
	}

	public UUID getId() {
		return this.id;
	}

	public Path getPath() {
		return this.path;
	}

	public String getResult() {
		return this.result;
	}

	public String getTarget() {
		return this.target;
	}

	public void setBenchmarkResult(BenchmarkResult result) {
		this.benchmarkResult = result;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
