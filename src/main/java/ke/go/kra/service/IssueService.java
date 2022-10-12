package ke.go.kra.service;

import ke.go.kra.domain.Issue;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Issue}.
 */
public interface IssueService {
    /**
     * Save a issue.
     *
     * @param issue the entity to save.
     * @return the persisted entity.
     */
    Mono<Issue> save(Issue issue);

    /**
     * Partially updates a issue.
     *
     * @param issue the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Issue> partialUpdate(Issue issue);

    /**
     * Get all the issues.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Issue> findAll(Pageable pageable);

    /**
     * Returns the number of issues available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" issue.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Issue> findOne(Long id);

    /**
     * Delete the "id" issue.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Returns the number of Closed issues available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAllClosed();
    /**
     * Returns the number of Open issues available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAllOpen();
    /**
     * Returns the number of issues in-progress available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAllInProgress();
    /**
     * Returns the number of Closed issues available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAllRejected();
}
