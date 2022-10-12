package ke.go.kra.repository;

import ke.go.kra.domain.Developer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Developer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DeveloperRepository extends ReactiveCrudRepository<Developer, Long>, DeveloperRepositoryInternal {
    Flux<Developer> findAllBy(Pageable pageable);

    @Override
    Mono<Developer> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Developer> findAllWithEagerRelationships();

    @Override
    Flux<Developer> findAllWithEagerRelationships(Pageable page);

    @Query(
        "SELECT entity.* FROM developer entity JOIN rel_developer__expertise joinTable ON entity.id = joinTable.developer_id WHERE joinTable.expertise_id = :id"
    )
    Flux<Developer> findByExpertise(Long id);

    @Override
    <S extends Developer> Mono<S> save(S entity);

    @Override
    Flux<Developer> findAll();

    @Override
    Mono<Developer> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DeveloperRepositoryInternal {
    <S extends Developer> Mono<S> save(S entity);

    Flux<Developer> findAllBy(Pageable pageable);

    Flux<Developer> findAll();

    Mono<Developer> findById(Long id);

    Flux<Developer> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Developer> findOneWithEagerRelationships(Long id);

    Flux<Developer> findAllWithEagerRelationships();

    Flux<Developer> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
