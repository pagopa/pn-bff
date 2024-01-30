package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.StatusTemplateApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.StatusSchema;
import it.pagopa.pn.bff.service.StatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class StatusController implements StatusTemplateApi {

    private final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @Override
    public Mono<ResponseEntity<StatusSchema>> getStatus(final ServerWebExchange exchange) {
        return statusService.getStatus();
    }
}