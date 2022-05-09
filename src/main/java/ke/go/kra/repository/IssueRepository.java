package ke.go.kra.repository;

import ke.go.kra.domain.Issue;
import ke.go.kra.domain.enumeration.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Issue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IssueRepository extends ReactiveCrudRepository<Issue, Long>, IssueRepositoryInternal {
    Flux<Issue> findAllBy(Pageable pageable);

    @Query("SELECT * FROM issue entity WHERE entity.category_id = :id")
    Flux<Issue> findByCategory(Long id);

    @Query("SELECT * FROM issue entity WHERE entity.status =:status")
    Flux<Issue> findByStatus(Status status);

    @Query("SELECT * FROM issue entity WHERE entity.category_id IS NULL")
    Flux<Issue> findAllWhereCategoryIsNull();

    @Query("SELECT * FROM issue entity WHERE entity.product_id = :id")
    Flux<Issue> findByProduct(Long id);

    @Query("SELECT * FROM issue entity WHERE entity.product_id IS NULL")
    Flux<Issue> findAllWhereProductIsNull();

    @Query("SELECT * FROM issue entity WHERE entity.assignee_id = :id")
    Flux<Issue> findByAssignee(Long id);

    @Query("SELECT * FROM issue entity WHERE entity.assignee_id IS NULL")
    Flux<Issue> findAllWhereAssigneeIsNull();

    @Query("SELECT * FROM issue entity WHERE entity.priority_id = :id")
    Flux<Issue> findByPriority(Long id);

    @Query("SELECT * FROM issue entity WHERE entity.priority_id IS NULL")
    Flux<Issue> findAllWherePriorityIsNull();

    @Query("SELECT * FROM issue entity WHERE entity.department_id = :id")
    Flux<Issue> findByDepartment(Long id);

    @Query("SELECT * FROM issue entity WHERE entity.department_id IS NULL")
    Flux<Issue> findAllWhereDepartmentIsNull();

    @Override
    <S extends Issue> Mono<S> save(S entity);

    @Override
    Flux<Issue> findAll();

    @Override
    Mono<Issue> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface IssueRepositoryInternal {
    <S extends Issue> Mono<S> save(S entity);

    Flux<Issue> findAllBy(Pageable pageable);

    Flux<Issue> findAll();

    Mono<Issue> findById(Long id);

    Flux<Issue> findAllBy(Pageable pageable, Criteria criteria);
}
