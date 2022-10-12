package ke.go.kra.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import ke.go.kra.IntegrationTest;
import ke.go.kra.domain.Priority;
import ke.go.kra.repository.EntityManager;
import ke.go.kra.repository.PriorityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link PriorityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PriorityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_SLA = 1;
    private static final Integer UPDATED_SLA = 2;

    private static final String ENTITY_API_URL = "/api/priorities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PriorityRepository priorityRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Priority priority;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Priority createEntity(EntityManager em) {
        Priority priority = new Priority().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).sla(DEFAULT_SLA);
        return priority;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Priority createUpdatedEntity(EntityManager em) {
        Priority priority = new Priority().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).sla(UPDATED_SLA);
        return priority;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Priority.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        priority = createEntity(em);
    }

    @Test
    void createPriority() throws Exception {
        int databaseSizeBeforeCreate = priorityRepository.findAll().collectList().block().size();
        // Create the Priority
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(priority))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Priority in the database
        List<Priority> priorityList = priorityRepository.findAll().collectList().block();
        assertThat(priorityList).hasSize(databaseSizeBeforeCreate + 1);
        Priority testPriority = priorityList.get(priorityList.size() - 1);
        assertThat(testPriority.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPriority.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPriority.getSla()).isEqualTo(DEFAULT_SLA);
    }

    @Test
    void createPriorityWithExistingId() throws Exception {
        // Create the Priority with an existing ID
        priority.setId(1L);

        int databaseSizeBeforeCreate = priorityRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(priority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Priority in the database
        List<Priority> priorityList = priorityRepository.findAll().collectList().block();
        assertThat(priorityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = priorityRepository.findAll().collectList().block().size();
        // set the field null
        priority.setName(null);

        // Create the Priority, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(priority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Priority> priorityList = priorityRepository.findAll().collectList().block();
        assertThat(priorityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkSlaIsRequired() throws Exception {
        int databaseSizeBeforeTest = priorityRepository.findAll().collectList().block().size();
        // set the field null
        priority.setSla(null);

        // Create the Priority, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(priority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Priority> priorityList = priorityRepository.findAll().collectList().block();
        assertThat(priorityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllPrioritiesAsStream() {
        // Initialize the database
        priorityRepository.save(priority).block();

        List<Priority> priorityList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Priority.class)
            .getResponseBody()
            .filter(priority::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(priorityList).isNotNull();
        assertThat(priorityList).hasSize(1);
        Priority testPriority = priorityList.get(0);
        assertThat(testPriority.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPriority.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPriority.getSla()).isEqualTo(DEFAULT_SLA);
    }

    @Test
    void getAllPriorities() {
        // Initialize the database
        priorityRepository.save(priority).block();

        // Get all the priorityList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(priority.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].sla")
            .value(hasItem(DEFAULT_SLA));
    }

    @Test
    void getPriority() {
        // Initialize the database
        priorityRepository.save(priority).block();

        // Get the priority
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, priority.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(priority.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.sla")
            .value(is(DEFAULT_SLA));
    }

    @Test
    void getNonExistingPriority() {
        // Get the priority
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPriority() throws Exception {
        // Initialize the database
        priorityRepository.save(priority).block();

        int databaseSizeBeforeUpdate = priorityRepository.findAll().collectList().block().size();

        // Update the priority
        Priority updatedPriority = priorityRepository.findById(priority.getId()).block();
        updatedPriority.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).sla(UPDATED_SLA);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPriority.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPriority))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Priority in the database
        List<Priority> priorityList = priorityRepository.findAll().collectList().block();
        assertThat(priorityList).hasSize(databaseSizeBeforeUpdate);
        Priority testPriority = priorityList.get(priorityList.size() - 1);
        assertThat(testPriority.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPriority.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPriority.getSla()).isEqualTo(UPDATED_SLA);
    }

    @Test
    void putNonExistingPriority() throws Exception {
        int databaseSizeBeforeUpdate = priorityRepository.findAll().collectList().block().size();
        priority.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, priority.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(priority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Priority in the database
        List<Priority> priorityList = priorityRepository.findAll().collectList().block();
        assertThat(priorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPriority() throws Exception {
        int databaseSizeBeforeUpdate = priorityRepository.findAll().collectList().block().size();
        priority.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(priority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Priority in the database
        List<Priority> priorityList = priorityRepository.findAll().collectList().block();
        assertThat(priorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPriority() throws Exception {
        int databaseSizeBeforeUpdate = priorityRepository.findAll().collectList().block().size();
        priority.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(priority))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Priority in the database
        List<Priority> priorityList = priorityRepository.findAll().collectList().block();
        assertThat(priorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePriorityWithPatch() throws Exception {
        // Initialize the database
        priorityRepository.save(priority).block();

        int databaseSizeBeforeUpdate = priorityRepository.findAll().collectList().block().size();

        // Update the priority using partial update
        Priority partialUpdatedPriority = new Priority();
        partialUpdatedPriority.setId(priority.getId());

        partialUpdatedPriority.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).sla(UPDATED_SLA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPriority.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPriority))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Priority in the database
        List<Priority> priorityList = priorityRepository.findAll().collectList().block();
        assertThat(priorityList).hasSize(databaseSizeBeforeUpdate);
        Priority testPriority = priorityList.get(priorityList.size() - 1);
        assertThat(testPriority.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPriority.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPriority.getSla()).isEqualTo(UPDATED_SLA);
    }

    @Test
    void fullUpdatePriorityWithPatch() throws Exception {
        // Initialize the database
        priorityRepository.save(priority).block();

        int databaseSizeBeforeUpdate = priorityRepository.findAll().collectList().block().size();

        // Update the priority using partial update
        Priority partialUpdatedPriority = new Priority();
        partialUpdatedPriority.setId(priority.getId());

        partialUpdatedPriority.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).sla(UPDATED_SLA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPriority.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPriority))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Priority in the database
        List<Priority> priorityList = priorityRepository.findAll().collectList().block();
        assertThat(priorityList).hasSize(databaseSizeBeforeUpdate);
        Priority testPriority = priorityList.get(priorityList.size() - 1);
        assertThat(testPriority.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPriority.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPriority.getSla()).isEqualTo(UPDATED_SLA);
    }

    @Test
    void patchNonExistingPriority() throws Exception {
        int databaseSizeBeforeUpdate = priorityRepository.findAll().collectList().block().size();
        priority.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, priority.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(priority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Priority in the database
        List<Priority> priorityList = priorityRepository.findAll().collectList().block();
        assertThat(priorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPriority() throws Exception {
        int databaseSizeBeforeUpdate = priorityRepository.findAll().collectList().block().size();
        priority.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(priority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Priority in the database
        List<Priority> priorityList = priorityRepository.findAll().collectList().block();
        assertThat(priorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPriority() throws Exception {
        int databaseSizeBeforeUpdate = priorityRepository.findAll().collectList().block().size();
        priority.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(priority))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Priority in the database
        List<Priority> priorityList = priorityRepository.findAll().collectList().block();
        assertThat(priorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePriority() {
        // Initialize the database
        priorityRepository.save(priority).block();

        int databaseSizeBeforeDelete = priorityRepository.findAll().collectList().block().size();

        // Delete the priority
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, priority.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Priority> priorityList = priorityRepository.findAll().collectList().block();
        assertThat(priorityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
