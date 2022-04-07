package ke.go.kra.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import ke.go.kra.domain.Issue;
import ke.go.kra.repository.IssueRepository;
import ke.go.kra.service.IssueService;
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
 * REST controller for managing {@link ke.go.kra.domain.Issue}.
 */
@RestController
@RequestMapping("/api")
public class IssueResource {

    private final Logger log = LoggerFactory.getLogger(IssueResource.class);

    private static final String ENTITY_NAME = "issue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IssueService issueService;

    private final IssueRepository issueRepository;

    public IssueResource(IssueService issueService, IssueRepository issueRepository) {
        this.issueService = issueService;
        this.issueRepository = issueRepository;
    }

    /**
     * {@code POST  /issues} : Create a new issue.
     *
     * @param issue the issue to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new issue, or with status {@code 400 (Bad Request)} if the issue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/issues")
    public Mono<ResponseEntity<Issue>> createIssue(@Valid @RequestBody Issue issue) throws URISyntaxException {
        log.debug("REST request to save Issue : {}", issue);
        if (issue.getId() != null) {
            throw new BadRequestAlertException("A new issue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return issueService
            .save(issue)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/issues/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /issues/:id} : Updates an existing issue.
     *
     * @param id the id of the issue to save.
     * @param issue the issue to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated issue,
     * or with status {@code 400 (Bad Request)} if the issue is not valid,
     * or with status {@code 500 (Internal Server Error)} if the issue couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/issues/{id}")
    public Mono<ResponseEntity<Issue>> updateIssue(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Issue issue
    ) throws URISyntaxException {
        log.debug("REST request to update Issue : {}, {}", id, issue);
        if (issue.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, issue.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return issueRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return issueService
                    .save(issue)
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
     * {@code PATCH  /issues/:id} : Partial updates given fields of an existing issue, field will ignore if it is null
     *
     * @param id the id of the issue to save.
     * @param issue the issue to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated issue,
     * or with status {@code 400 (Bad Request)} if the issue is not valid,
     * or with status {@code 404 (Not Found)} if the issue is not found,
     * or with status {@code 500 (Internal Server Error)} if the issue couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/issues/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Issue>> partialUpdateIssue(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Issue issue
    ) throws URISyntaxException {
        log.debug("REST request to partial update Issue partially : {}, {}", id, issue);
        if (issue.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, issue.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return issueRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Issue> result = issueService.partialUpdate(issue);

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
     * {@code GET  /issues} : get all the issues.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of issues in body.
     */
    @GetMapping("/issues")
    public Mono<ResponseEntity<List<Issue>>> getAllIssues(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Issues");
        return issueService
            .countAll()
            .zipWith(issueService.findAll(pageable).collectList())
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
     * {@code GET  /issues/:id} : get the "id" issue.
     *
     * @param id the id of the issue to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the issue, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/issues/{id}")
    public Mono<ResponseEntity<Issue>> getIssue(@PathVariable Long id) {
        log.debug("REST request to get Issue : {}", id);
        Mono<Issue> issue = issueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(issue);
    }

    /**
     * {@code DELETE  /issues/:id} : delete the "id" issue.
     *
     * @param id the id of the issue to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/issues/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteIssue(@PathVariable Long id) {
        log.debug("REST request to delete Issue : {}", id);
        return issueService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
