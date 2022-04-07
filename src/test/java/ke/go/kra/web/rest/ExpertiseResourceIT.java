package ke.go.kra.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import ke.go.kra.IntegrationTest;
import ke.go.kra.domain.Expertise;
import ke.go.kra.repository.EntityManager;
import ke.go.kra.repository.ExpertiseRepository;
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
 * Integration tests for the {@link ExpertiseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ExpertiseResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/expertise";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExpertiseRepository expertiseRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Expertise expertise;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Expertise createEntity(EntityManager em) {
        Expertise expertise = new Expertise().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return expertise;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Expertise createUpdatedEntity(EntityManager em) {
        Expertise expertise = new Expertise().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return expertise;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Expertise.class).block();
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
        expertise = createEntity(em);
    }

    @Test
    void createExpertise() throws Exception {
        int databaseSizeBeforeCreate = expertiseRepository.findAll().collectList().block().size();
        // Create the Expertise
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expertise))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Expertise in the database
        List<Expertise> expertiseList = expertiseRepository.findAll().collectList().block();
        assertThat(expertiseList).hasSize(databaseSizeBeforeCreate + 1);
        Expertise testExpertise = expertiseList.get(expertiseList.size() - 1);
        assertThat(testExpertise.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testExpertise.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createExpertiseWithExistingId() throws Exception {
        // Create the Expertise with an existing ID
        expertise.setId(1L);

        int databaseSizeBeforeCreate = expertiseRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expertise))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Expertise in the database
        List<Expertise> expertiseList = expertiseRepository.findAll().collectList().block();
        assertThat(expertiseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllExpertiseAsStream() {
        // Initialize the database
        expertiseRepository.save(expertise).block();

        List<Expertise> expertiseList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Expertise.class)
            .getResponseBody()
            .filter(expertise::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(expertiseList).isNotNull();
        assertThat(expertiseList).hasSize(1);
        Expertise testExpertise = expertiseList.get(0);
        assertThat(testExpertise.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testExpertise.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllExpertise() {
        // Initialize the database
        expertiseRepository.save(expertise).block();

        // Get all the expertiseList
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
            .value(hasItem(expertise.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getExpertise() {
        // Initialize the database
        expertiseRepository.save(expertise).block();

        // Get the expertise
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, expertise.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(expertise.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingExpertise() {
        // Get the expertise
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewExpertise() throws Exception {
        // Initialize the database
        expertiseRepository.save(expertise).block();

        int databaseSizeBeforeUpdate = expertiseRepository.findAll().collectList().block().size();

        // Update the expertise
        Expertise updatedExpertise = expertiseRepository.findById(expertise.getId()).block();
        updatedExpertise.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedExpertise.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedExpertise))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Expertise in the database
        List<Expertise> expertiseList = expertiseRepository.findAll().collectList().block();
        assertThat(expertiseList).hasSize(databaseSizeBeforeUpdate);
        Expertise testExpertise = expertiseList.get(expertiseList.size() - 1);
        assertThat(testExpertise.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testExpertise.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void putNonExistingExpertise() throws Exception {
        int databaseSizeBeforeUpdate = expertiseRepository.findAll().collectList().block().size();
        expertise.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, expertise.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expertise))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Expertise in the database
        List<Expertise> expertiseList = expertiseRepository.findAll().collectList().block();
        assertThat(expertiseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchExpertise() throws Exception {
        int databaseSizeBeforeUpdate = expertiseRepository.findAll().collectList().block().size();
        expertise.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expertise))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Expertise in the database
        List<Expertise> expertiseList = expertiseRepository.findAll().collectList().block();
        assertThat(expertiseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamExpertise() throws Exception {
        int databaseSizeBeforeUpdate = expertiseRepository.findAll().collectList().block().size();
        expertise.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expertise))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Expertise in the database
        List<Expertise> expertiseList = expertiseRepository.findAll().collectList().block();
        assertThat(expertiseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateExpertiseWithPatch() throws Exception {
        // Initialize the database
        expertiseRepository.save(expertise).block();

        int databaseSizeBeforeUpdate = expertiseRepository.findAll().collectList().block().size();

        // Update the expertise using partial update
        Expertise partialUpdatedExpertise = new Expertise();
        partialUpdatedExpertise.setId(expertise.getId());

        partialUpdatedExpertise.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedExpertise.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedExpertise))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Expertise in the database
        List<Expertise> expertiseList = expertiseRepository.findAll().collectList().block();
        assertThat(expertiseList).hasSize(databaseSizeBeforeUpdate);
        Expertise testExpertise = expertiseList.get(expertiseList.size() - 1);
        assertThat(testExpertise.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testExpertise.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void fullUpdateExpertiseWithPatch() throws Exception {
        // Initialize the database
        expertiseRepository.save(expertise).block();

        int databaseSizeBeforeUpdate = expertiseRepository.findAll().collectList().block().size();

        // Update the expertise using partial update
        Expertise partialUpdatedExpertise = new Expertise();
        partialUpdatedExpertise.setId(expertise.getId());

        partialUpdatedExpertise.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedExpertise.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedExpertise))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Expertise in the database
        List<Expertise> expertiseList = expertiseRepository.findAll().collectList().block();
        assertThat(expertiseList).hasSize(databaseSizeBeforeUpdate);
        Expertise testExpertise = expertiseList.get(expertiseList.size() - 1);
        assertThat(testExpertise.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testExpertise.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingExpertise() throws Exception {
        int databaseSizeBeforeUpdate = expertiseRepository.findAll().collectList().block().size();
        expertise.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, expertise.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(expertise))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Expertise in the database
        List<Expertise> expertiseList = expertiseRepository.findAll().collectList().block();
        assertThat(expertiseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchExpertise() throws Exception {
        int databaseSizeBeforeUpdate = expertiseRepository.findAll().collectList().block().size();
        expertise.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(expertise))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Expertise in the database
        List<Expertise> expertiseList = expertiseRepository.findAll().collectList().block();
        assertThat(expertiseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamExpertise() throws Exception {
        int databaseSizeBeforeUpdate = expertiseRepository.findAll().collectList().block().size();
        expertise.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(expertise))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Expertise in the database
        List<Expertise> expertiseList = expertiseRepository.findAll().collectList().block();
        assertThat(expertiseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteExpertise() {
        // Initialize the database
        expertiseRepository.save(expertise).block();

        int databaseSizeBeforeDelete = expertiseRepository.findAll().collectList().block().size();

        // Delete the expertise
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, expertise.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Expertise> expertiseList = expertiseRepository.findAll().collectList().block();
        assertThat(expertiseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
