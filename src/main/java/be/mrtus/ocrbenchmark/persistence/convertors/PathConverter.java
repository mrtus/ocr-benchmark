package be.mrtus.ocrbenchmark.persistence.convertors;

import java.io.File;
import java.nio.file.Path;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PathConverter implements AttributeConverter<Path, String> {

	@Override
	public String convertToDatabaseColumn(Path path) {
		return path.toString();
	}

	@Override
	public Path convertToEntityAttribute(String path) {
		return new File(path).toPath();
	}
}
