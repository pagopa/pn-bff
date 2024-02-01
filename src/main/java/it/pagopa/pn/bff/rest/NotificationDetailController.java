package it.pagopa.pn.bff.rest;


import it.pagopa.pn.bff.generated.openapi.server.v1.api.NotificationReceivedApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.service.NotificationDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
public class NotificationDetailController implements NotificationReceivedApi {

    private final NotificationDetailService notificationDetailService;

    public NotificationDetailController(NotificationDetailService notificationDetailService) {
        this.notificationDetailService = notificationDetailService;
    }

    @Override
    public Mono<ResponseEntity<FullReceivedNotificationV21>> getReceivedNotification(String xPagopaPnUid,
                                                                                      CxTypeAuthFleet xPagopaPnCxType,
                                                                                      String xPagopaPnCxId,
                                                                                      String iun,
                                                                                      List<String> xPagopaPnCxGroups,
                                                                                      String mandateId,
                                                                                      final ServerWebExchange exchange) {

        return notificationDetailService.getNotificationDetail(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId,xPagopaPnCxGroups, iun, mandateId
        ).map(ResponseEntity::ok);
    }

}
