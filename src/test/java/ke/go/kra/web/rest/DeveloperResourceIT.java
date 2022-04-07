package ke.go.kra.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import ke.go.kra.IntegrationTest;
import ke.go.kra.domain.Developer;
import ke.go.kra.repository.DeveloperRepository;
import ke.go.kra.repository.EntityManager;
import ke.go.kra.service.DeveloperService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link DeveloperResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DeveloperResourceIT {

    private static final String DEFAULT_STAFF_NO = "AAAAAAAAAA";
    private static final String UPDATED_STAFF_NO = "BBBBBBBBBB";

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/developers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DeveloperRepository developerRepository;

    @Mock
    private DeveloperRepository developerRepositoryMock;

    @Mock
    private DeveloperService developerServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Developer developer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Developer createEntity(EntityManager em) {
        Developer developer = new Developer()
            .staffNo(DEFAULT_STAFF_NO)
            .fullName(DEFAULT_FULL_NAME)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER);
        return developer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Developer createUpdatedEntity(EntityManager em) {
        Developer developer = new Developer()
            .staffNo(UPDATED_STAFF_NO)
            .fullName(UPDATED_FULL_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER);
        return developer;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_developer__expertise").block();
            em.deleteAll(Developer.class).block();
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
        developer = createEntity(em);
    }

    @Test
    void createDeveloper() throws Exception {
        int databaseSizeBeforeCreate = developerRepository.findAll().collectList().block().size();
        // Create the Developer
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(developer))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Developer in the database
        List<Developer> developerList = developerRepository.findAll().collectList().block();
        assertThat(developerList).hasSize(databaseSizeBeforeCreate + 1);
        Developer testDeveloper = developerList.get(developerList.size() - 1);
        assertThat(testDeveloper.getStaffNo()).isEqualTo(DEFAULT_STAFF_NO);
        assertThat(testDeveloper.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testDeveloper.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testDeveloper.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
    }

    @Test
    void createDeveloperWithExistingId() throws Exception {
        // Create the Developer with an existing ID
        developer.setId(1L);

        int databaseSizeBeforeCreate = developerRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(developer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Developer in the database
        List<Developer> developerList = developerRepository.findAll().collectList().block();
        assertThat(developerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkStaffNoIsRequired() throws Exception {
        int databaseSizeBeforeTest = developerRepository.findAll().collectList().block().size();
        // set the field null
        developer.setStaffNo(null);

        // Create the Developer, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(developer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Developer> developerList = developerRepository.findAll().collectList().block();
        assertThat(developerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkFullNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = developerRepository.findAll().collectList().block().size();
        // set the field null
        developer.setFullName(null);

        // Create the Developer, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(developer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Developer> developerList = developerRepository.findAll().collectList().block();
        assertThat(developerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = developerRepository.findAll().collectList().block().size();
        // set the field null
        developer.setEmail(null);

        // Create the Developer, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(developer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Developer> developerList = developerRepository.findAll().collectList().block();
        assertThat(developerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPhoneNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = developerRepository.findAll().collectList().block().size();
        // set the field null
        developer.setPhoneNumber(null);

        // Create the Developer, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(developer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Developer> developerList = developerRepository.findAll().collectList().block();
        assertThat(developerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllDevelopers() {
        // Initialize the database
        developerRepository.save(developer).block();

        // Get all the developerList
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
            .value(hasItem(developer.getId().intValue()))
            .jsonPath("$.[*].staffNo")
            .value(hasItem(DEFAULT_STAFF_NO))
            .jsonPath("$.[*].fullName")
            .value(hasItem(DEFAULT_FULL_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phoneNumber")
            .value(hasItem(DEFAULT_PHONE_NUMBER));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDevelopersWithEagerRelationshipsIsEnabled() {
        when(developerServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(developerServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDevelopersWithEagerRelationshipsIsNotEnabled() {
        when(developerServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(developerServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getDeveloper() {
        // Initialize the database
        developerRepository.save(developer).block();

        // Get the developer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, developer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(developer.getId().intValue()))
            .jsonPath("$.staffNo")
            .value(is(DEFAULT_STAFF_NO))
            .jsonPath("$.fullName")
            .value(is(DEFAULT_FULL_NAME))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.phoneNumber")
            .value(is(DEFAULT_PHONE_NUMBER));
    }

    @Test
    void getNonExistingDeveloper() {
        // Get the developer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewDeveloper() throws Exception {
        // Initialize the database
        developerRepository.save(developer).block();

        int databaseSizeBeforeUpdate = developerRepository.findAll().collectList().block().size();

        // Update the developer
        Developer updatedDeveloper = developerRepository.findById(developer.getId()).block();
        updatedDeveloper.staffNo(UPDATED_STAFF_NO).fullName(UPDATED_FULL_NAME).email(UPDATED_EMAIL).phoneNumber(UPDATED_PHONE_NUMBER);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedDeveloper.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedDeveloper))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Developer in the database
        List<Developer> developerList = developerRepository.findAll().collectList().block();
        assertThat(developerList).hasSize(databaseSizeBeforeUpdate);
        Developer testDeveloper = developerList.get(developerList.size() - 1);
        assertThat(testDeveloper.getStaffNo()).isEqualTo(UPDATED_STAFF_NO);
        assertThat(testDeveloper.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testDeveloper.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testDeveloper.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
    }

    @Test
    void putNonExistingDeveloper() throws Exception {
        int databaseSizeBeforeUpdate = developerRepository.findAll().collectList().block().size();
        developer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, developer.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(developer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Developer in the database
        List<Developer> developerList = developerRepository.findAll().collectList().block();
        assertThat(developerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDeveloper() throws Exception {
        int databaseSizeBeforeUpdate = developerRepository.findAll().collectList().block().size();
        developer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(developer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Developer in the database
        List<Developer> developerList = developerRepository.findAll().collectList().block();
        assertThat(developerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDeveloper() throws Exception {
        int databaseSizeBeforeUpdate = developerRepository.findAll().collectList().block().size();
        developer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(developer))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Developer in the database
        List<Developer> developerList = developerRepository.findAll().collectList().block();
        assertThat(developerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDeveloperWithPatch() throws Exception {
        // Initialize the database
        developerRepository.save(developer).block();

        int databaseSizeBeforeUpdate = developerRepository.findAll().collectList().block().size();

        // Update the developer using partial update
        Developer partialUpdatedDeveloper = new Developer();
        partialUpdatedDeveloper.setId(developer.getId());

        partialUpdatedDeveloper.fullName(UPDATED_FULL_NAME).email(UPDATED_EMAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDeveloper.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDeveloper))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Developer in the database
        List<Developer> developerList = developerRepository.findAll().collectList().block();
        assertThat(developerList).hasSize(databaseSizeBeforeUpdate);
        Developer testDeveloper = developerList.get(developerList.size() - 1);
        assertThat(testDeveloper.getStaffNo()).isEqualTo(DEFAULT_STAFF_NO);
        assertThat(testDeveloper.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testDeveloper.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testDeveloper.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
    }

    @Test
    void fullUpdateDeveloperWithPatch() throws Exception {
        // Initialize the database
        developerRepository.save(developer).block();

        int databaseSizeBeforeUpdate = developerRepository.findAll().collectList().block().size();

        // Update the developer using partial update
        Developer partialUpdatedDeveloper = new Developer();
        partialUpdatedDeveloper.setId(developer.getId());

        partialUpdatedDeveloper
            .staffNo(UPDATED_STAFF_NO)
            .fullName(UPDATED_FULL_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDeveloper.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDeveloper))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Developer in the database
        List<Developer> developerList = developerRepository.findAll().collectList().block();
        assertThat(developerList).hasSize(databaseSizeBeforeUpdate);
        Developer testDeveloper = developerList.get(developerList.size() - 1);
        assertThat(testDeveloper.getStaffNo()).isEqualTo(UPDATED_STAFF_NO);
        assertThat(testDeveloper.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testDeveloper.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testDeveloper.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
    }

    @Test
    void patchNonExistingDeveloper() throws Exception {
        int databaseSizeBeforeUpdate = developerRepository.findAll().collectList().block().size();
        developer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, developer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(developer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Developer in the database
        List<Developer> developerList = developerRepository.findAll().collectList().block();
        assertThat(developerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDeveloper() throws Exception {
        int databaseSizeBeforeUpdate = developerRepository.findAll().collectList().block().size();
        developer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(developer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Developer in the database
        List<Developer> developerList = developerRepository.findAll().collectList().block();
        assertThat(developerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDeveloper() throws Exception {
        int databaseSizeBeforeUpdate = developerRepository.findAll().collectList().block().size();
        developer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(developer))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Developer in the database
        List<Developer> developerList = developerRepository.findAll().collectList().block();
        assertThat(developerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDeveloper() {
        // Initialize the database
        developerRepository.save(developer).block();

        int databaseSizeBeforeDelete = developerRepository.findAll().collectList().block().size();

        // Delete the developer
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, developer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Developer> developerList = developerRepository.findAll().collectList().block();
        assertThat(developerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
