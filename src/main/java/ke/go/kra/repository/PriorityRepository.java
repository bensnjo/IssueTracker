package ke.go.kra.repository;

import ke.go.kra.domain.Priority;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Priority entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PriorityRepository extends ReactiveCrudRepository<Priority, Long>, PriorityRepositoryInternal {
    @Override
    <S extends Priority> Mono<S> save(S entity);

    @Override
    Flux<Priority> findAll();

    @Override
    Mono<Priority> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PriorityRepositoryInternal {
    <S extends Priority> Mono<S> save(S entity);

    Flux<Priority> findAllBy(Pageable pageable);

    Flux<Priority> findAll();

    Mono<Priority> findById(Long id);

    Flux<Priority> findAllBy(Pageable pageable, Criteria criteria);
}
