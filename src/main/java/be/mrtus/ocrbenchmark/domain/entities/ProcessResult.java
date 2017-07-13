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
	@Id
	@Convert(converter = UUIDConverter.class)
	private UUID id;
	@Convert(converter = PathConverter.class)
	private Path path;
	private String result;

	public ProcessResult() {
		this.id = UUID.randomUUID();
	}

	public BenchmarkResult getBenchmarkResult() {
		return this.benchmarkResult;
	}

	public long getDuration() {
		return this.duration;
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

	public void setBenchmarkResult(BenchmarkResult result) {
		this.benchmarkResult = result;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
