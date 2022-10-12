package ke.go.kra.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import ke.go.kra.domain.Expertise;
import ke.go.kra.repository.rowmapper.ExpertiseRowMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Expertise entity.
 */
@SuppressWarnings("unused")
class ExpertiseRepositoryInternalImpl extends SimpleR2dbcRepository<Expertise, Long> implements ExpertiseRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ExpertiseRowMapper expertiseMapper;

    private static final Table entityTable = Table.aliased("expertise", EntityManager.ENTITY_ALIAS);

    public ExpertiseRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ExpertiseRowMapper expertiseMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Expertise.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.expertiseMapper = expertiseMapper;
    }

    @Override
    public Flux<Expertise> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Expertise> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Expertise> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = ExpertiseSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, Expertise.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Expertise> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Expertise> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    private Expertise process(Row row, RowMetadata metadata) {
        Expertise entity = expertiseMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Expertise> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
