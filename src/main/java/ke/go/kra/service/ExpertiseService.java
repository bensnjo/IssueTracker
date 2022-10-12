package ke.go.kra.service;

import java.util.List;
import ke.go.kra.domain.Expertise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Expertise}.
 */
public interface ExpertiseService {
    /**
     * Save a expertise.
     *
     * @param expertise the entity to save.
     * @return the persisted entity.
     */
    Mono<Expertise> save(Expertise expertise);

    /**
     * Partially updates a expertise.
     *
     * @param expertise the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Expertise> partialUpdate(Expertise expertise);

    /**
     * Get all the expertise.
     *
     * @return the list of entities.
     */
    Flux<Expertise> findAll();

    /**
     * Returns the number of expertise available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" expertise.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Expertise> findOne(Long id);

    /**
     * Delete the "id" expertise.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
