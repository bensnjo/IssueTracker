package ke.go.kra.service;

import java.util.List;
import ke.go.kra.domain.Priority;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Priority}.
 */
public interface PriorityService {
    /**
     * Save a priority.
     *
     * @param priority the entity to save.
     * @return the persisted entity.
     */
    Mono<Priority> save(Priority priority);

    /**
     * Partially updates a priority.
     *
     * @param priority the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Priority> partialUpdate(Priority priority);

    /**
     * Get all the priorities.
     *
     * @return the list of entities.
     */
    Flux<Priority> findAll();

    /**
     * Returns the number of priorities available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" priority.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Priority> findOne(Long id);

    /**
     * Delete the "id" priority.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
