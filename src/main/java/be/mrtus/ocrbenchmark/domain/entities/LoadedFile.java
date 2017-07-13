package be.mrtus.ocrbenchmark.domain.entities;

import java.nio.file.Path;

public class LoadedFile {

	private String fileContents;
	private Path path;

	public LoadedFile(Path path) {
		this.path = path;
	}

	public void setFileContents(String fileContents) {
		this.fileContents = fileContents;
	}

	public String getFileContents() {
		return this.fileContents;
	}

	public Path getPath() {
		return this.path;
	}
}
