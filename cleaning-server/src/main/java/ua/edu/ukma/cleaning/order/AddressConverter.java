package ua.edu.ukma.cleaning.order;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ua.edu.ukma.cleaning.address.AddressDto;

@Converter
public class AddressConverter implements AttributeConverter<AddressDto, String> {
    private static final String SEPARATOR = ", ";

    @Override
    public String convertToDatabaseColumn(AddressDto attribute) {
        if (attribute == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (attribute.getCity() != null && !attribute.getCity().isEmpty()) {
            sb.append(attribute.getCity());
            sb.append(SEPARATOR);
        }
        if (attribute.getStreet() != null && !attribute.getStreet().isEmpty()) {
            sb.append(attribute.getStreet());
            sb.append(SEPARATOR);
        }
        if (attribute.getHouseNumber() != null && !attribute.getHouseNumber().isEmpty()) {
            sb.append(attribute.getHouseNumber());
            sb.append(SEPARATOR);
        }
        if (attribute.getFlatNumber() != null && !attribute.getFlatNumber().isEmpty()) {
            sb.append(attribute.getFlatNumber());
            sb.append(SEPARATOR);
        }
        if (attribute.getZip() != null && !attribute.getZip().isEmpty()) {
            sb.append(attribute.getZip());
            sb.append(SEPARATOR);
        }

        return sb.toString();
    }

    @Override
    public AddressDto convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String[] pieces = dbData.split(SEPARATOR);
        if (pieces == null || pieces.length == 0) {
            return null;
        }
        AddressDto addressDto = new AddressDto();
        addressDto.setCity(!pieces[0].isEmpty() ? pieces[0] : null);
        addressDto.setStreet(!pieces[1].isEmpty() ? pieces[1] : null);
        addressDto.setHouseNumber(!pieces[2].isEmpty() ? pieces[2] : null);
        addressDto.setFlatNumber(!pieces[3].isEmpty() ? pieces[3] : null);
        addressDto.setZip(!pieces[4].isEmpty() ? pieces[4] : null);
        return addressDto;
    }
}
