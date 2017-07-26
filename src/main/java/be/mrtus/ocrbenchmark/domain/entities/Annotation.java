package be.mrtus.ocrbenchmark.domain.entities;

import be.mrtus.ocrbenchmark.persistence.convertors.UUIDConverter;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Annotation implements Serializable {

	private static final long serialVersionUID = 1L;
	private String filename;
	@Id
	@Convert(converter = UUIDConverter.class)
	private UUID id;
	private String word;

	public Annotation() {
		this.id = UUID.randomUUID();
	}

	public Annotation(String filename, String word) {
		this();
		this.filename = filename;
		this.word = word;
	}

	public String getFilename() {
		return this.filename;
	}

	public String getWord() {
		return this.word;
	}
}
