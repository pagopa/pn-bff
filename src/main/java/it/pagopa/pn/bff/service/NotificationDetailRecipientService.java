package it.pagopa.pn.bff.service;


import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.notificationdetail.NotificationDetailMapper;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientRecipientImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDetailRecipientService {

    private final PnDeliveryClientRecipientImpl pnDeliveryClient;

    public Mono<BffFullNotificationV1> getNotificationDetail(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
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
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        return notificationDetail.map(NotificationDetailMapper.modelMapper::mapReceivedNotificationDetail);
    }
}