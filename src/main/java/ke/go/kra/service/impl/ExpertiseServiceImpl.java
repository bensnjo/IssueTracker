package ke.go.kra.service.impl;

import java.util.List;
import ke.go.kra.domain.Expertise;
import ke.go.kra.repository.ExpertiseRepository;
import ke.go.kra.service.ExpertiseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Expertise}.
 */
@Service
@Transactional
public class ExpertiseServiceImpl implements ExpertiseService {

    private final Logger log = LoggerFactory.getLogger(ExpertiseServiceImpl.class);

    private final ExpertiseRepository expertiseRepository;

    public ExpertiseServiceImpl(ExpertiseRepository expertiseRepository) {
        this.expertiseRepository = expertiseRepository;
    }

    @Override
    public Mono<Expertise> save(Expertise expertise) {
        log.debug("Request to save Expertise : {}", expertise);
        return expertiseRepository.save(expertise);
    }

    @Override
    public Mono<Expertise> partialUpdate(Expertise expertise) {
        log.debug("Request to partially update Expertise : {}", expertise);

        return expertiseRepository
            .findById(expertise.getId())
            .map(existingExpertise -> {
                if (expertise.getName() != null) {
                    existingExpertise.setName(expertise.getName());
                }
                if (expertise.getDescription() != null) {
                    existingExpertise.setDescription(expertise.getDescription());
                }

                return existingExpertise;
            })
            .flatMap(expertiseRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Expertise> findAll() {
        log.debug("Request to get all Expertise");
        return expertiseRepository.findAll();
    }

    public Mono<Long> countAll() {
        return expertiseRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Expertise> findOne(Long id) {
        log.debug("Request to get Expertise : {}", id);
        return expertiseRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Expertise : {}", id);
        return expertiseRepository.deleteById(id);
    }
}
