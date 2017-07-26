package be.mrtus.ocrbenchmark.domain.entities;

import java.nio.file.Path;

public class LoadedFile {

	private Path path;
	private String target;

	public LoadedFile(Path path, String target) {
		this.path = path;
		this.target = target;
	}

	public Path getPath() {
		return this.path;
	}

	public String getTarget() {
		return this.target;
	}
}
