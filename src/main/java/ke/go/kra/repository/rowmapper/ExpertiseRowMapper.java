package ke.go.kra.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import ke.go.kra.domain.Expertise;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Expertise}, with proper type conversions.
 */
@Service
public class ExpertiseRowMapper implements BiFunction<Row, String, Expertise> {

    private final ColumnConverter converter;

    public ExpertiseRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Expertise} stored in the database.
     */
    @Override
    public Expertise apply(Row row, String prefix) {
        Expertise entity = new Expertise();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
