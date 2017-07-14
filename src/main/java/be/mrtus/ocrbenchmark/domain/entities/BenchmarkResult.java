package be.mrtus.ocrbenchmark.domain.entities;

import be.mrtus.ocrbenchmark.persistence.convertors.UUIDConverter;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BenchmarkResult implements Serializable {

	private static final long serialVersionUID = 1L;
	private double avgAccuracy;
	private long duration;
	@Id
	@Convert(converter = UUIDConverter.class)
	private UUID id;

	public BenchmarkResult() {
		this.id = UUID.randomUUID();
	}

	public double getAvgAccuracy() {
		return this.avgAccuracy;
	}

	public long getDuration() {
		return this.duration;
	}

	public UUID getId() {
		return this.id;
	}

	public void setAvgAccuracy(double avgAccuracy) {
		this.avgAccuracy = avgAccuracy;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
}
