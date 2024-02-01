package it.pagopa.pn.bff.service;


import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.FullReceivedNotificationV21;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class NotificationDetailService {

    private final PnDeliveryClientImpl pnDeliveryClient;

    public NotificationDetailService(PnDeliveryClientImpl pnDeliveryClient) {
        this.pnDeliveryClient = pnDeliveryClient;
    }

    public Mono<FullReceivedNotificationV21> getNotificationDetail(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                   String xPagopaPnCxId, List<String> xPagopaPnCxGroups,
                                                                   String iun, String mandateId) {
        return pnDeliveryClient.retrieveNotification(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups,
                mandateId
        );
    }
}
