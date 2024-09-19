package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.RequestCheckAarMandateDto;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.ResponseCheckAarMandateDto;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCheckAarRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCheckAarResponse;
import it.pagopa.pn.bff.mocks.NotificationsReceivedMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationAarQrCodeMapperTest {
    private final NotificationsReceivedMock notificationsReceivedMock = new NotificationsReceivedMock();

    @Test
    void testRequestCheckAarMandateMapper() {
        BffCheckAarRequest bffCheckAarMandate = notificationsReceivedMock.getRequestCheckAarMandateDtoPNMock();

        RequestCheckAarMandateDto requestCheckAarMandateDtoV1 = NotificationAarQrCodeMapper.modelMapper.toRequestCheckAarMandateDto(bffCheckAarMandate);
        assertNotNull(requestCheckAarMandateDtoV1);

        assertEquals(requestCheckAarMandateDtoV1.getAarQrCodeValue(), bffCheckAarMandate.getAarQrCodeValue());

        RequestCheckAarMandateDto requestCheckAarMandateDtoV1Null = NotificationAarQrCodeMapper.modelMapper.toRequestCheckAarMandateDto(null);
        assertNull(requestCheckAarMandateDtoV1Null);
    }

    @Test
    void testResponseCheckAarMandateMapper() {
        ResponseCheckAarMandateDto responseCheckAarMandateDto = notificationsReceivedMock.getResponseCheckAarMandateDtoPNMock();

        BffCheckAarResponse bffResponseCheckAarMandateDto = NotificationAarQrCodeMapper.modelMapper.toBffResponseCheckAarMandateDto(responseCheckAarMandateDto);
        assertNotNull(bffResponseCheckAarMandateDto);

        assertEquals(bffResponseCheckAarMandateDto.getMandateId(), responseCheckAarMandateDto.getMandateId());
        assertEquals(bffResponseCheckAarMandateDto.getIun(), responseCheckAarMandateDto.getIun());

        BffCheckAarResponse bffResponseCheckAarMandateDtoV1Null = NotificationAarQrCodeMapper.modelMapper.toBffResponseCheckAarMandateDto(null);
        assertNull(bffResponseCheckAarMandateDtoV1Null);
    }
}