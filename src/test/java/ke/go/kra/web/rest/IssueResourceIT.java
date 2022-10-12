package ke.go.kra.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import ke.go.kra.IntegrationTest;
import ke.go.kra.domain.Issue;
import ke.go.kra.domain.enumeration.Status;
import ke.go.kra.repository.EntityManager;
import ke.go.kra.repository.IssueRepository;
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
 * Integration tests for the {@link IssueResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class IssueResourceIT {

    private static final String DEFAULT_DEFECT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_DEFECT_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.OPEN;
    private static final Status UPDATED_STATUS = Status.IN_PROGRESS;

    private static final LocalDate DEFAULT_DATE_IDENTIFIED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_IDENTIFIED = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATE_CLOSED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_CLOSED = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/issues";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Issue issue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Issue createEntity(EntityManager em) {
        Issue issue = new Issue()
            .defectNumber(DEFAULT_DEFECT_NUMBER)
            .description(DEFAULT_DESCRIPTION)
            .version(DEFAULT_VERSION)
            .status(DEFAULT_STATUS)
            .dateIdentified(DEFAULT_DATE_IDENTIFIED)
            .dateClosed(DEFAULT_DATE_CLOSED)
            .comments(DEFAULT_COMMENTS);
        return issue;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Issue createUpdatedEntity(EntityManager em) {
        Issue issue = new Issue()
            .defectNumber(UPDATED_DEFECT_NUMBER)
            .description(UPDATED_DESCRIPTION)
            .version(UPDATED_VERSION)
            .status(UPDATED_STATUS)
            .dateIdentified(UPDATED_DATE_IDENTIFIED)
            .dateClosed(UPDATED_DATE_CLOSED)
            .comments(UPDATED_COMMENTS);
        return issue;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Issue.class).block();
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
        issue = createEntity(em);
    }

    @Test
    void createIssue() throws Exception {
        int databaseSizeBeforeCreate = issueRepository.findAll().collectList().block().size();
        // Create the Issue
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(issue))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeCreate + 1);
        Issue testIssue = issueList.get(issueList.size() - 1);
        assertThat(testIssue.getDefectNumber()).isEqualTo(DEFAULT_DEFECT_NUMBER);
        assertThat(testIssue.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testIssue.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testIssue.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testIssue.getDateIdentified()).isEqualTo(DEFAULT_DATE_IDENTIFIED);
        assertThat(testIssue.getDateClosed()).isEqualTo(DEFAULT_DATE_CLOSED);
        assertThat(testIssue.getComments()).isEqualTo(DEFAULT_COMMENTS);
    }

    @Test
    void createIssueWithExistingId() throws Exception {
        // Create the Issue with an existing ID
        issue.setId(1L);

        int databaseSizeBeforeCreate = issueRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(issue))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkDefectNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = issueRepository.findAll().collectList().block().size();
        // set the field null
        issue.setDefectNumber(null);

        // Create the Issue, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(issue))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = issueRepository.findAll().collectList().block().size();
        // set the field null
        issue.setStatus(null);

        // Create the Issue, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(issue))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDateIdentifiedIsRequired() throws Exception {
        int databaseSizeBeforeTest = issueRepository.findAll().collectList().block().size();
        // set the field null
        issue.setDateIdentified(null);

        // Create the Issue, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(issue))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllIssues() {
        // Initialize the database
        issueRepository.save(issue).block();

        // Get all the issueList
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
            .value(hasItem(issue.getId().intValue()))
            .jsonPath("$.[*].defectNumber")
            .value(hasItem(DEFAULT_DEFECT_NUMBER))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].version")
            .value(hasItem(DEFAULT_VERSION))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].dateIdentified")
            .value(hasItem(DEFAULT_DATE_IDENTIFIED.toString()))
            .jsonPath("$.[*].dateClosed")
            .value(hasItem(DEFAULT_DATE_CLOSED.toString()))
            .jsonPath("$.[*].comments")
            .value(hasItem(DEFAULT_COMMENTS));
    }

    @Test
    void getIssue() {
        // Initialize the database
        issueRepository.save(issue).block();

        // Get the issue
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, issue.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(issue.getId().intValue()))
            .jsonPath("$.defectNumber")
            .value(is(DEFAULT_DEFECT_NUMBER))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.version")
            .value(is(DEFAULT_VERSION))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.dateIdentified")
            .value(is(DEFAULT_DATE_IDENTIFIED.toString()))
            .jsonPath("$.dateClosed")
            .value(is(DEFAULT_DATE_CLOSED.toString()))
            .jsonPath("$.comments")
            .value(is(DEFAULT_COMMENTS));
    }

    @Test
    void getNonExistingIssue() {
        // Get the issue
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewIssue() throws Exception {
        // Initialize the database
        issueRepository.save(issue).block();

        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();

        // Update the issue
        Issue updatedIssue = issueRepository.findById(issue.getId()).block();
        updatedIssue
            .defectNumber(UPDATED_DEFECT_NUMBER)
            .description(UPDATED_DESCRIPTION)
            .version(UPDATED_VERSION)
            .status(UPDATED_STATUS)
            .dateIdentified(UPDATED_DATE_IDENTIFIED)
            .dateClosed(UPDATED_DATE_CLOSED)
            .comments(UPDATED_COMMENTS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedIssue.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedIssue))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
        Issue testIssue = issueList.get(issueList.size() - 1);
        assertThat(testIssue.getDefectNumber()).isEqualTo(UPDATED_DEFECT_NUMBER);
        assertThat(testIssue.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testIssue.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testIssue.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testIssue.getDateIdentified()).isEqualTo(UPDATED_DATE_IDENTIFIED);
        assertThat(testIssue.getDateClosed()).isEqualTo(UPDATED_DATE_CLOSED);
        assertThat(testIssue.getComments()).isEqualTo(UPDATED_COMMENTS);
    }

    @Test
    void putNonExistingIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();
        issue.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, issue.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(issue))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();
        issue.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(issue))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();
        issue.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(issue))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateIssueWithPatch() throws Exception {
        // Initialize the database
        issueRepository.save(issue).block();

        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();

        // Update the issue using partial update
        Issue partialUpdatedIssue = new Issue();
        partialUpdatedIssue.setId(issue.getId());

        partialUpdatedIssue
            .description(UPDATED_DESCRIPTION)
            .version(UPDATED_VERSION)
            .dateIdentified(UPDATED_DATE_IDENTIFIED)
            .dateClosed(UPDATED_DATE_CLOSED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedIssue.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedIssue))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
        Issue testIssue = issueList.get(issueList.size() - 1);
        assertThat(testIssue.getDefectNumber()).isEqualTo(DEFAULT_DEFECT_NUMBER);
        assertThat(testIssue.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testIssue.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testIssue.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testIssue.getDateIdentified()).isEqualTo(UPDATED_DATE_IDENTIFIED);
        assertThat(testIssue.getDateClosed()).isEqualTo(UPDATED_DATE_CLOSED);
        assertThat(testIssue.getComments()).isEqualTo(DEFAULT_COMMENTS);
    }

    @Test
    void fullUpdateIssueWithPatch() throws Exception {
        // Initialize the database
        issueRepository.save(issue).block();

        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();

        // Update the issue using partial update
        Issue partialUpdatedIssue = new Issue();
        partialUpdatedIssue.setId(issue.getId());

        partialUpdatedIssue
            .defectNumber(UPDATED_DEFECT_NUMBER)
            .description(UPDATED_DESCRIPTION)
            .version(UPDATED_VERSION)
            .status(UPDATED_STATUS)
            .dateIdentified(UPDATED_DATE_IDENTIFIED)
            .dateClosed(UPDATED_DATE_CLOSED)
            .comments(UPDATED_COMMENTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedIssue.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedIssue))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
        Issue testIssue = issueList.get(issueList.size() - 1);
        assertThat(testIssue.getDefectNumber()).isEqualTo(UPDATED_DEFECT_NUMBER);
        assertThat(testIssue.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testIssue.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testIssue.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testIssue.getDateIdentified()).isEqualTo(UPDATED_DATE_IDENTIFIED);
        assertThat(testIssue.getDateClosed()).isEqualTo(UPDATED_DATE_CLOSED);
        assertThat(testIssue.getComments()).isEqualTo(UPDATED_COMMENTS);
    }

    @Test
    void patchNonExistingIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();
        issue.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, issue.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(issue))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();
        issue.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(issue))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();
        issue.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(issue))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteIssue() {
        // Initialize the database
        issueRepository.save(issue).block();

        int databaseSizeBeforeDelete = issueRepository.findAll().collectList().block().size();

        // Delete the issue
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, issue.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
