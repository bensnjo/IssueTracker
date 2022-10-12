package ke.go.kra.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import ke.go.kra.domain.Issue;
import ke.go.kra.domain.enumeration.Status;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Issue}, with proper type conversions.
 */
@Service
public class IssueRowMapper implements BiFunction<Row, String, Issue> {

    private final ColumnConverter converter;

    public IssueRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Issue} stored in the database.
     */
    @Override
    public Issue apply(Row row, String prefix) {
        Issue entity = new Issue();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDefectNumber(converter.fromRow(row, prefix + "_defect_number", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setVersion(converter.fromRow(row, prefix + "_version", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", Status.class));
        entity.setDateIdentified(converter.fromRow(row, prefix + "_date_identified", LocalDate.class));
        entity.setDateClosed(converter.fromRow(row, prefix + "_date_closed", LocalDate.class));
        entity.setComments(converter.fromRow(row, prefix + "_comments", String.class));
        entity.setCategoryId(converter.fromRow(row, prefix + "_category_id", Long.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        entity.setAssigneeId(converter.fromRow(row, prefix + "_assignee_id", Long.class));
        entity.setPriorityId(converter.fromRow(row, prefix + "_priority_id", Long.class));
        entity.setDepartmentId(converter.fromRow(row, prefix + "_department_id", Long.class));
        return entity;
    }
}
