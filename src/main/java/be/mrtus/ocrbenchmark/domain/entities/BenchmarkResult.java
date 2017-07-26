package be.mrtus.ocrbenchmark.domain.entities;

import be.mrtus.ocrbenchmark.persistence.convertors.UUIDConverter;
import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;
import javax.persistence.*;

@Entity
public class BenchmarkResult implements Serializable {

	private static final long serialVersionUID = 1L;
	private double avgAccuracy;
	@Temporal(value = TemporalType.TIMESTAMP)
	private Calendar createOn;
	private long duration;
	@Id
	@Convert(converter = UUIDConverter.class)
	private UUID id;

	public BenchmarkResult() {
		this.id = UUID.randomUUID();
		this.createOn = Calendar.getInstance();
	}

	public double getAvgAccuracy() {
		return this.avgAccuracy;
	}

	public Calendar getCreateOn() {
		return this.createOn;
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
