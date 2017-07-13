package be.mrtus.ocrbenchmark.domain.entities;

import be.mrtus.ocrbenchmark.persistence.UUIDConverter;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.UUID;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ProcessResult implements Serializable {

	private static final long serialVersionUID = 1L;
	private long duration;
	@Id
	@Convert(converter = UUIDConverter.class)
	private UUID id;
	private Path path;
	private String result;

	public ProcessResult() {
		this.id = UUID.randomUUID();
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

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
