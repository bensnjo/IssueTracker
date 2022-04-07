package ke.go.kra.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import ke.go.kra.domain.Priority;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Priority}, with proper type conversions.
 */
@Service
public class PriorityRowMapper implements BiFunction<Row, String, Priority> {

    private final ColumnConverter converter;

    public PriorityRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Priority} stored in the database.
     */
    @Override
    public Priority apply(Row row, String prefix) {
        Priority entity = new Priority();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setSla(converter.fromRow(row, prefix + "_sla", Integer.class));
        return entity;
    }
}
