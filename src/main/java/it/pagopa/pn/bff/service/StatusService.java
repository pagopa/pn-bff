package it.pagopa.pn.bff.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class StatusService {
    public Mono<String> getStatus() {
        return Mono.just("I'm alive!");
    }
}
