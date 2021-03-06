package be.mrtus.ocrbenchmark.domain.entities;

public class GroupPartition {

	private double avgDuration;
	private double avgErrorRate;
	private double avgPer1000Pixels;
	private int groupSize;
	private final int id;
	private double maxValue;
	private double minValue;

	public GroupPartition(int id, double minValue, double maxValue) {
		this.id = id;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public boolean between(double pixelCount) {
		return this.minValue < pixelCount && pixelCount <= this.maxValue;
	}

	public double getAvgDuration() {
		return this.avgDuration;
	}

	public double getAvgErrorRate() {
		return this.avgErrorRate;
	}

	public double getAvgPer1000Pixels() {
		return this.avgPer1000Pixels;
	}

	public int getGroupSize() {
		return this.groupSize;
	}

	public int getId() {
		return this.id;
	}

	public double getMaxValue() {
		return this.maxValue;
	}

	public double getMinValue() {
		return this.minValue;
	}

	public void setAvgDuration(double avgDuration) {
		this.avgDuration = avgDuration;
	}

	public void setAvgErrorRate(double avgErrorRate) {
		this.avgErrorRate = avgErrorRate;
	}

	public void setAvgPer1000Pixels(double avgPer1000Pixels) {
		this.avgPer1000Pixels = avgPer1000Pixels;
	}

	public void setGroupSize(int size) {
		this.groupSize = size;
	}
}
