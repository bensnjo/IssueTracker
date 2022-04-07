package ke.go.kra.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import ke.go.kra.domain.Department;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Department}, with proper type conversions.
 */
@Service
public class DepartmentRowMapper implements BiFunction<Row, String, Department> {

    private final ColumnConverter converter;

    public DepartmentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Department} stored in the database.
     */
    @Override
    public Department apply(Row row, String prefix) {
        Department entity = new Department();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        return entity;
    }
}
