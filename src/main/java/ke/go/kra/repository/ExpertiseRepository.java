package ke.go.kra.repository;

import ke.go.kra.domain.Expertise;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Expertise entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExpertiseRepository extends ReactiveCrudRepository<Expertise, Long>, ExpertiseRepositoryInternal {
    @Override
    <S extends Expertise> Mono<S> save(S entity);

    @Override
    Flux<Expertise> findAll();

    @Override
    Mono<Expertise> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ExpertiseRepositoryInternal {
    <S extends Expertise> Mono<S> save(S entity);

    Flux<Expertise> findAllBy(Pageable pageable);

    Flux<Expertise> findAll();

    Mono<Expertise> findById(Long id);

    Flux<Expertise> findAllBy(Pageable pageable, Criteria criteria);
}
