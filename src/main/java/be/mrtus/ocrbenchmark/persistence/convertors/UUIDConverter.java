package be.mrtus.ocrbenchmark.persistence.convertors;

import java.util.UUID;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class UUIDConverter implements AttributeConverter<UUID, String> {

	@Override
	public String convertToDatabaseColumn(UUID id) {
		return id.toString();
	}

	@Override
	public UUID convertToEntityAttribute(String string) {
		return UUID.fromString(string);
	}
}
