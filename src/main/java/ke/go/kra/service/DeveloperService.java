package ke.go.kra.service;

import ke.go.kra.domain.Developer;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Developer}.
 */
public interface DeveloperService {
    /**
     * Save a developer.
     *
     * @param developer the entity to save.
     * @return the persisted entity.
     */
    Mono<Developer> save(Developer developer);

    /**
     * Partially updates a developer.
     *
     * @param developer the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Developer> partialUpdate(Developer developer);

    /**
     * Get all the developers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Developer> findAll(Pageable pageable);

    /**
     * Get all the developers with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Developer> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of developers available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" developer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Developer> findOne(Long id);

    /**
     * Delete the "id" developer.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
