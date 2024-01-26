package it.pagopa.pn.bff.rest;

import io.swagger.annotations.ApiOperation;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.StatusTemplateApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.TemplateSampleApi;
import it.pagopa.pn.bff.service.StatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class StatusController implements StatusTemplateApi {

    private final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    public Mono<ResponseEntity<String>> getStatus() {
        return statusService.getStatus().map(ResponseEntity::ok);
    }
}