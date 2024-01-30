package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.StatusSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class StatusService {
    public Mono<ResponseEntity<StatusSchema>> getStatus() {
        return Mono.just(ResponseEntity.ok(StatusSchema.builder().status("OK").build()));
    }
}
