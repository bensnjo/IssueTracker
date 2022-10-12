package ke.go.kra.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import ke.go.kra.domain.Developer;
import ke.go.kra.repository.DeveloperRepository;
import ke.go.kra.service.DeveloperService;
import ke.go.kra.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link ke.go.kra.domain.Developer}.
 */
@RestController
@RequestMapping("/api")
public class DeveloperResource {

    private final Logger log = LoggerFactory.getLogger(DeveloperResource.class);

    private static final String ENTITY_NAME = "developer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DeveloperService developerService;

    private final DeveloperRepository developerRepository;

    public DeveloperResource(DeveloperService developerService, DeveloperRepository developerRepository) {
        this.developerService = developerService;
        this.developerRepository = developerRepository;
    }

    /**
     * {@code POST  /developers} : Create a new developer.
     *
     * @param developer the developer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new developer, or with status {@code 400 (Bad Request)} if the developer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/developers")
    public Mono<ResponseEntity<Developer>> createDeveloper(@Valid @RequestBody Developer developer) throws URISyntaxException {
        log.debug("REST request to save Developer : {}", developer);
        if (developer.getId() != null) {
            throw new BadRequestAlertException("A new developer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return developerService
            .save(developer)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/developers/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /developers/:id} : Updates an existing developer.
     *
     * @param id the id of the developer to save.
     * @param developer the developer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated developer,
     * or with status {@code 400 (Bad Request)} if the developer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the developer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/developers/{id}")
    public Mono<ResponseEntity<Developer>> updateDeveloper(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Developer developer
    ) throws URISyntaxException {
        log.debug("REST request to update Developer : {}, {}", id, developer);
        if (developer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, developer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return developerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return developerService
                    .save(developer)
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
     * {@code PATCH  /developers/:id} : Partial updates given fields of an existing developer, field will ignore if it is null
     *
     * @param id the id of the developer to save.
     * @param developer the developer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated developer,
     * or with status {@code 400 (Bad Request)} if the developer is not valid,
     * or with status {@code 404 (Not Found)} if the developer is not found,
     * or with status {@code 500 (Internal Server Error)} if the developer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/developers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Developer>> partialUpdateDeveloper(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Developer developer
    ) throws URISyntaxException {
        log.debug("REST request to partial update Developer partially : {}, {}", id, developer);
        if (developer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, developer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return developerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Developer> result = developerService.partialUpdate(developer);

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
     * {@code GET  /developers} : get all the developers.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of developers in body.
     */
    @GetMapping("/developers")
    public Mono<ResponseEntity<List<Developer>>> getAllDevelopers(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Developers");
        return developerService
            .countAll()
            .zipWith(developerService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /developers/:id} : get the "id" developer.
     *
     * @param id the id of the developer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the developer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/developers/{id}")
    public Mono<ResponseEntity<Developer>> getDeveloper(@PathVariable Long id) {
        log.debug("REST request to get Developer : {}", id);
        Mono<Developer> developer = developerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(developer);
    }

    /**
     * {@code DELETE  /developers/:id} : delete the "id" developer.
     *
     * @param id the id of the developer to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/developers/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteDeveloper(@PathVariable Long id) {
        log.debug("REST request to delete Developer : {}", id);
        return developerService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
