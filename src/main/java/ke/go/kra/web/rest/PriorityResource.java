package ke.go.kra.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import ke.go.kra.domain.Priority;
import ke.go.kra.repository.PriorityRepository;
import ke.go.kra.service.PriorityService;
import ke.go.kra.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link ke.go.kra.domain.Priority}.
 */
@RestController
@RequestMapping("/api")
public class PriorityResource {

    private final Logger log = LoggerFactory.getLogger(PriorityResource.class);

    private static final String ENTITY_NAME = "priority";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PriorityService priorityService;

    private final PriorityRepository priorityRepository;

    public PriorityResource(PriorityService priorityService, PriorityRepository priorityRepository) {
        this.priorityService = priorityService;
        this.priorityRepository = priorityRepository;
    }

    /**
     * {@code POST  /priorities} : Create a new priority.
     *
     * @param priority the priority to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new priority, or with status {@code 400 (Bad Request)} if the priority has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/priorities")
    public Mono<ResponseEntity<Priority>> createPriority(@Valid @RequestBody Priority priority) throws URISyntaxException {
        log.debug("REST request to save Priority : {}", priority);
        if (priority.getId() != null) {
            throw new BadRequestAlertException("A new priority cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return priorityService
            .save(priority)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/priorities/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /priorities/:id} : Updates an existing priority.
     *
     * @param id the id of the priority to save.
     * @param priority the priority to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated priority,
     * or with status {@code 400 (Bad Request)} if the priority is not valid,
     * or with status {@code 500 (Internal Server Error)} if the priority couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/priorities/{id}")
    public Mono<ResponseEntity<Priority>> updatePriority(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Priority priority
    ) throws URISyntaxException {
        log.debug("REST request to update Priority : {}, {}", id, priority);
        if (priority.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, priority.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return priorityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return priorityService
                    .save(priority)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /priorities/:id} : Partial updates given fields of an existing priority, field will ignore if it is null
     *
     * @param id the id of the priority to save.
     * @param priority the priority to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated priority,
     * or with status {@code 400 (Bad Request)} if the priority is not valid,
     * or with status {@code 404 (Not Found)} if the priority is not found,
     * or with status {@code 500 (Internal Server Error)} if the priority couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/priorities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Priority>> partialUpdatePriority(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Priority priority
    ) throws URISyntaxException {
        log.debug("REST request to partial update Priority partially : {}, {}", id, priority);
        if (priority.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, priority.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return priorityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Priority> result = priorityService.partialUpdate(priority);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /priorities} : get all the priorities.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of priorities in body.
     */
    @GetMapping("/priorities")
    public Mono<List<Priority>> getAllPriorities() {
        log.debug("REST request to get all Priorities");
        return priorityService.findAll().collectList();
    }

    /**
     * {@code GET  /priorities} : get all the priorities as a stream.
     * @return the {@link Flux} of priorities.
     */
    @GetMapping(value = "/priorities", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Priority> getAllPrioritiesAsStream() {
        log.debug("REST request to get all Priorities as a stream");
        return priorityService.findAll();
    }

    /**
     * {@code GET  /priorities/:id} : get the "id" priority.
     *
     * @param id the id of the priority to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the priority, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/priorities/{id}")
    public Mono<ResponseEntity<Priority>> getPriority(@PathVariable Long id) {
        log.debug("REST request to get Priority : {}", id);
        Mono<Priority> priority = priorityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(priority);
    }

    /**
     * {@code DELETE  /priorities/:id} : delete the "id" priority.
     *
     * @param id the id of the priority to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/priorities/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePriority(@PathVariable Long id) {
        log.debug("REST request to delete Priority : {}", id);
        return priorityService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
