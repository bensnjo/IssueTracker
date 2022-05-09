package ke.go.kra.service.impl;

import ke.go.kra.domain.Issue;
import ke.go.kra.domain.enumeration.Status;
import ke.go.kra.repository.IssueRepository;
import ke.go.kra.service.IssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Issue}.
 */
@Service
@Transactional
public class IssueServiceImpl implements IssueService {

    private final Logger log = LoggerFactory.getLogger(IssueServiceImpl.class);

    private final IssueRepository issueRepository;

    public IssueServiceImpl(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    @Override
    public Mono<Issue> save(Issue issue) {
        log.debug("Request to save Issue : {}", issue);
        return issueRepository.save(issue);
    }

    @Override
    public Mono<Issue> partialUpdate(Issue issue) {
        log.debug("Request to partially update Issue : {}", issue);

        return issueRepository
            .findById(issue.getId())
            .map(existingIssue -> {
                if (issue.getDefectNumber() != null) {
                    existingIssue.setDefectNumber(issue.getDefectNumber());
                }
                if (issue.getDescription() != null) {
                    existingIssue.setDescription(issue.getDescription());
                }
                if (issue.getVersion() != null) {
                    existingIssue.setVersion(issue.getVersion());
                }
                if (issue.getStatus() != null) {
                    existingIssue.setStatus(issue.getStatus());
                }
                if (issue.getDateIdentified() != null) {
                    existingIssue.setDateIdentified(issue.getDateIdentified());
                }
                if (issue.getDateClosed() != null) {
                    existingIssue.setDateClosed(issue.getDateClosed());
                }
                if (issue.getComments() != null) {
                    existingIssue.setComments(issue.getComments());
                }

                return existingIssue;
            })
            .flatMap(issueRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Issue> findAll(Pageable pageable) {
        log.debug("Request to get all Issues");
        return issueRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return issueRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Issue> findOne(Long id) {
        log.debug("Request to get Issue : {}", id);
        return issueRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Issue : {}", id);
        return issueRepository.deleteById(id);
    }

    @Override
    public Mono<Long> countAllClosed() {
        return issueRepository.findByStatus(Status.CLOSED).count();
    }

    @Override
    public Mono<Long> countAllOpen() {
        return issueRepository.findByStatus(Status.OPEN).count();
    }

    @Override
    public Mono<Long> countAllInProgress() {
        return issueRepository.findByStatus(Status.IN_PROGRESS).count();
    }

    @Override
    public Mono<Long> countAllRejected() {
        return issueRepository.findByStatus(Status.REJECTED).count();
    }
}
