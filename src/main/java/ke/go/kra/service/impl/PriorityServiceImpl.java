package ke.go.kra.service.impl;

import java.util.List;
import ke.go.kra.domain.Priority;
import ke.go.kra.repository.PriorityRepository;
import ke.go.kra.service.PriorityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Priority}.
 */
@Service
@Transactional
public class PriorityServiceImpl implements PriorityService {

    private final Logger log = LoggerFactory.getLogger(PriorityServiceImpl.class);

    private final PriorityRepository priorityRepository;

    public PriorityServiceImpl(PriorityRepository priorityRepository) {
        this.priorityRepository = priorityRepository;
    }

    @Override
    public Mono<Priority> save(Priority priority) {
        log.debug("Request to save Priority : {}", priority);
        return priorityRepository.save(priority);
    }

    @Override
    public Mono<Priority> partialUpdate(Priority priority) {
        log.debug("Request to partially update Priority : {}", priority);

        return priorityRepository
            .findById(priority.getId())
            .map(existingPriority -> {
                if (priority.getName() != null) {
                    existingPriority.setName(priority.getName());
                }
                if (priority.getDescription() != null) {
                    existingPriority.setDescription(priority.getDescription());
                }
                if (priority.getSla() != null) {
                    existingPriority.setSla(priority.getSla());
                }

                return existingPriority;
            })
            .flatMap(priorityRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Priority> findAll() {
        log.debug("Request to get all Priorities");
        return priorityRepository.findAll();
    }

    public Mono<Long> countAll() {
        return priorityRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Priority> findOne(Long id) {
        log.debug("Request to get Priority : {}", id);
        return priorityRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Priority : {}", id);
        return priorityRepository.deleteById(id);
    }
}
