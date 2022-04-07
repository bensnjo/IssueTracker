package ke.go.kra.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class IssueSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("defect_number", table, columnPrefix + "_defect_number"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("version", table, columnPrefix + "_version"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("date_identified", table, columnPrefix + "_date_identified"));
        columns.add(Column.aliased("date_closed", table, columnPrefix + "_date_closed"));
        columns.add(Column.aliased("comments", table, columnPrefix + "_comments"));

        columns.add(Column.aliased("category_id", table, columnPrefix + "_category_id"));
        columns.add(Column.aliased("product_id", table, columnPrefix + "_product_id"));
        columns.add(Column.aliased("assignee_id", table, columnPrefix + "_assignee_id"));
        columns.add(Column.aliased("priority_id", table, columnPrefix + "_priority_id"));
        columns.add(Column.aliased("department_id", table, columnPrefix + "_department_id"));
        return columns;
    }
}
