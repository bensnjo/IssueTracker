package ke.go.kra.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import ke.go.kra.domain.Expertise;
import ke.go.kra.repository.ExpertiseRepository;
import ke.go.kra.service.ExpertiseService;
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
 * REST controller for managing {@link ke.go.kra.domain.Expertise}.
 */
@RestController
@RequestMapping("/api")
public class ExpertiseResource {

    private final Logger log = LoggerFactory.getLogger(ExpertiseResource.class);

    private static final String ENTITY_NAME = "expertise";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExpertiseService expertiseService;

    private final ExpertiseRepository expertiseRepository;

    public ExpertiseResource(ExpertiseService expertiseService, ExpertiseRepository expertiseRepository) {
        this.expertiseService = expertiseService;
        this.expertiseRepository = expertiseRepository;
    }

    /**
     * {@code POST  /expertise} : Create a new expertise.
     *
     * @param expertise the expertise to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new expertise, or with status {@code 400 (Bad Request)} if the expertise has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/expertise")
    public Mono<ResponseEntity<Expertise>> createExpertise(@Valid @RequestBody Expertise expertise) throws URISyntaxException {
        log.debug("REST request to save Expertise : {}", expertise);
        if (expertise.getId() != null) {
            throw new BadRequestAlertException("A new expertise cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return expertiseService
            .save(expertise)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/expertise/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /expertise/:id} : Updates an existing expertise.
     *
     * @param id the id of the expertise to save.
     * @param expertise the expertise to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated expertise,
     * or with status {@code 400 (Bad Request)} if the expertise is not valid,
     * or with status {@code 500 (Internal Server Error)} if the expertise couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/expertise/{id}")
    public Mono<ResponseEntity<Expertise>> updateExpertise(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Expertise expertise
    ) throws URISyntaxException {
        log.debug("REST request to update Expertise : {}, {}", id, expertise);
        if (expertise.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, expertise.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return expertiseRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return expertiseService
                    .save(expertise)
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
     * {@code PATCH  /expertise/:id} : Partial updates given fields of an existing expertise, field will ignore if it is null
     *
     * @param id the id of the expertise to save.
     * @param expertise the expertise to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated expertise,
     * or with status {@code 400 (Bad Request)} if the expertise is not valid,
     * or with status {@code 404 (Not Found)} if the expertise is not found,
     * or with status {@code 500 (Internal Server Error)} if the expertise couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/expertise/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Expertise>> partialUpdateExpertise(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Expertise expertise
    ) throws URISyntaxException {
        log.debug("REST request to partial update Expertise partially : {}, {}", id, expertise);
        if (expertise.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, expertise.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return expertiseRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Expertise> result = expertiseService.partialUpdate(expertise);

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
     * {@code GET  /expertise} : get all the expertise.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of expertise in body.
     */
    @GetMapping("/expertise")
    public Mono<List<Expertise>> getAllExpertise() {
        log.debug("REST request to get all Expertise");
        return expertiseService.findAll().collectList();
    }

    /**
     * {@code GET  /expertise} : get all the expertise as a stream.
     * @return the {@link Flux} of expertise.
     */
    @GetMapping(value = "/expertise", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Expertise> getAllExpertiseAsStream() {
        log.debug("REST request to get all Expertise as a stream");
        return expertiseService.findAll();
    }

    /**
     * {@code GET  /expertise/:id} : get the "id" expertise.
     *
     * @param id the id of the expertise to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the expertise, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/expertise/{id}")
    public Mono<ResponseEntity<Expertise>> getExpertise(@PathVariable Long id) {
        log.debug("REST request to get Expertise : {}", id);
        Mono<Expertise> expertise = expertiseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(expertise);
    }

    /**
     * {@code DELETE  /expertise/:id} : delete the "id" expertise.
     *
     * @param id the id of the expertise to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/expertise/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteExpertise(@PathVariable Long id) {
        log.debug("REST request to delete Expertise : {}", id);
        return expertiseService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
