package ke.go.kra.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import ke.go.kra.domain.Issue;
import ke.go.kra.domain.enumeration.Status;
import ke.go.kra.repository.rowmapper.CategoryRowMapper;
import ke.go.kra.repository.rowmapper.DepartmentRowMapper;
import ke.go.kra.repository.rowmapper.DeveloperRowMapper;
import ke.go.kra.repository.rowmapper.IssueRowMapper;
import ke.go.kra.repository.rowmapper.PriorityRowMapper;
import ke.go.kra.repository.rowmapper.ProductRowMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Issue entity.
 */
@SuppressWarnings("unused")
class IssueRepositoryInternalImpl extends SimpleR2dbcRepository<Issue, Long> implements IssueRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CategoryRowMapper categoryMapper;
    private final ProductRowMapper productMapper;
    private final DeveloperRowMapper developerMapper;
    private final PriorityRowMapper priorityMapper;
    private final DepartmentRowMapper departmentMapper;
    private final IssueRowMapper issueMapper;

    private static final Table entityTable = Table.aliased("issue", EntityManager.ENTITY_ALIAS);
    private static final Table categoryTable = Table.aliased("category", "category");
    private static final Table productTable = Table.aliased("product", "product");
    private static final Table assigneeTable = Table.aliased("developer", "assignee");
    private static final Table priorityTable = Table.aliased("priority", "priority");
    private static final Table departmentTable = Table.aliased("department", "department");

    public IssueRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CategoryRowMapper categoryMapper,
        ProductRowMapper productMapper,
        DeveloperRowMapper developerMapper,
        PriorityRowMapper priorityMapper,
        DepartmentRowMapper departmentMapper,
        IssueRowMapper issueMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Issue.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
        this.developerMapper = developerMapper;
        this.priorityMapper = priorityMapper;
        this.departmentMapper = departmentMapper;
        this.issueMapper = issueMapper;
    }

    @Override
    public Flux<Issue> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Issue> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Issue> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = IssueSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CategorySqlHelper.getColumns(categoryTable, "category"));
        columns.addAll(ProductSqlHelper.getColumns(productTable, "product"));
        columns.addAll(DeveloperSqlHelper.getColumns(assigneeTable, "assignee"));
        columns.addAll(PrioritySqlHelper.getColumns(priorityTable, "priority"));
        columns.addAll(DepartmentSqlHelper.getColumns(departmentTable, "department"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(categoryTable)
            .on(Column.create("category_id", entityTable))
            .equals(Column.create("id", categoryTable))
            .leftOuterJoin(productTable)
            .on(Column.create("product_id", entityTable))
            .equals(Column.create("id", productTable))
            .leftOuterJoin(assigneeTable)
            .on(Column.create("assignee_id", entityTable))
            .equals(Column.create("id", assigneeTable))
            .leftOuterJoin(priorityTable)
            .on(Column.create("priority_id", entityTable))
            .equals(Column.create("id", priorityTable))
            .leftOuterJoin(departmentTable)
            .on(Column.create("department_id", entityTable))
            .equals(Column.create("id", departmentTable));

        String select = entityManager.createSelect(selectFrom, Issue.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Issue> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Issue> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    private Issue process(Row row, RowMetadata metadata) {
        Issue entity = issueMapper.apply(row, "e");
        entity.setCategory(categoryMapper.apply(row, "category"));
        entity.setProduct(productMapper.apply(row, "product"));
        entity.setAssignee(developerMapper.apply(row, "assignee"));
        entity.setPriority(priorityMapper.apply(row, "priority"));
        entity.setDepartment(departmentMapper.apply(row, "department"));
        return entity;
    }

    @Override
    public <S extends Issue> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
