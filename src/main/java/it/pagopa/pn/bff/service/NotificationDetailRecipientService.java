package it.pagopa.pn.bff.service;


import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mapper.CxTypeMapper;
import it.pagopa.pn.bff.mapper.NotificationDetailMapper;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientRecipientImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDetailRecipientService {

    private final PnDeliveryClientRecipientImpl pnDeliveryClient;

    public Mono<BffFullReceivedNotificationV23> getNotificationDetail(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                      String xPagopaPnCxId, List<String> xPagopaPnCxGroups,
                                                                      String iun, String mandateId) {
        log.info("Get notification detail for iun {} and mandateId: {}", iun, mandateId);
        Mono<FullReceivedNotificationV23> notificationDetail;
        notificationDetail = pnDeliveryClient.getReceivedNotification(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertRecipientCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups,
                mandateId
        );

        return notificationDetail.map(NotificationDetailMapper.modelMapper::mapNotificationDetail);
    }
}
