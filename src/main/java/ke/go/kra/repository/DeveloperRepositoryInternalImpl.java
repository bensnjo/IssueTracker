package ke.go.kra.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import ke.go.kra.domain.Developer;
import ke.go.kra.domain.Expertise;
import ke.go.kra.repository.rowmapper.DeveloperRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Developer entity.
 */
@SuppressWarnings("unused")
class DeveloperRepositoryInternalImpl extends SimpleR2dbcRepository<Developer, Long> implements DeveloperRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final DeveloperRowMapper developerMapper;

    private static final Table entityTable = Table.aliased("developer", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable expertiseLink = new EntityManager.LinkTable(
        "rel_developer__expertise",
        "developer_id",
        "expertise_id"
    );

    public DeveloperRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        DeveloperRowMapper developerMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Developer.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.developerMapper = developerMapper;
    }

    @Override
    public Flux<Developer> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Developer> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Developer> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = DeveloperSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, Developer.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Developer> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Developer> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    @Override
    public Mono<Developer> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Developer> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Developer> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Developer process(Row row, RowMetadata metadata) {
        Developer entity = developerMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Developer> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Developer> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(expertiseLink, entity.getId(), entity.getExpertise().stream().map(Expertise::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(expertiseLink, entityId);
    }
}
