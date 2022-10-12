package ke.go.kra.service.impl;

import ke.go.kra.domain.Developer;
import ke.go.kra.repository.DeveloperRepository;
import ke.go.kra.service.DeveloperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Developer}.
 */
@Service
@Transactional
public class DeveloperServiceImpl implements DeveloperService {

    private final Logger log = LoggerFactory.getLogger(DeveloperServiceImpl.class);

    private final DeveloperRepository developerRepository;

    public DeveloperServiceImpl(DeveloperRepository developerRepository) {
        this.developerRepository = developerRepository;
    }

    @Override
    public Mono<Developer> save(Developer developer) {
        log.debug("Request to save Developer : {}", developer);
        return developerRepository.save(developer);
    }

    @Override
    public Mono<Developer> partialUpdate(Developer developer) {
        log.debug("Request to partially update Developer : {}", developer);

        return developerRepository
            .findById(developer.getId())
            .map(existingDeveloper -> {
                if (developer.getStaffNo() != null) {
                    existingDeveloper.setStaffNo(developer.getStaffNo());
                }
                if (developer.getFullName() != null) {
                    existingDeveloper.setFullName(developer.getFullName());
                }
                if (developer.getEmail() != null) {
                    existingDeveloper.setEmail(developer.getEmail());
                }
                if (developer.getPhoneNumber() != null) {
                    existingDeveloper.setPhoneNumber(developer.getPhoneNumber());
                }

                return existingDeveloper;
            })
            .flatMap(developerRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Developer> findAll(Pageable pageable) {
        log.debug("Request to get all Developers");
        return developerRepository.findAllBy(pageable);
    }

    public Flux<Developer> findAllWithEagerRelationships(Pageable pageable) {
        return developerRepository.findAllWithEagerRelationships(pageable);
    }

    public Mono<Long> countAll() {
        return developerRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Developer> findOne(Long id) {
        log.debug("Request to get Developer : {}", id);
        return developerRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Developer : {}", id);
        return developerRepository.deleteById(id);
    }
}
