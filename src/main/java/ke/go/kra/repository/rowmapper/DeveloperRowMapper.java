package ke.go.kra.repository.rowmapper;

import java.util.Set;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import ke.go.kra.domain.Developer;
import ke.go.kra.domain.Expertise;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Developer}, with proper type conversions.
 */
@Service
public class DeveloperRowMapper implements BiFunction<Row, String, Developer> {

    private final ColumnConverter converter;

    public DeveloperRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Developer} stored in the database.
     */
    @Override
    public Developer apply(Row row, String prefix) {
        Developer entity = new Developer();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setStaffNo(converter.fromRow(row, prefix + "_staff_no", String.class));
        entity.setFullName(converter.fromRow(row, prefix + "_full_name", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setPhoneNumber(converter.fromRow(row, prefix + "_phone_number", String.class));
        //entity.setExpertise(converter.fromRow(row, prefix + "_expertise_id", Set());
        return entity;
    }
}
