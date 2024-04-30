package it.pagopa.pn.bff.mappers.notification;

import it.pagopa.pn.bff.config.PnBffConfigs;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationsResponseV1;
import it.pagopa.pn.bff.mocks.NotificationSentMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = PnBffConfigs.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class NotificationSentMapperTest {
    private final NotificationSentMock notificationSentMock = new NotificationSentMock();

    @Test
    void testBffNotificationsResponseV1Mapper() {
        NotificationSearchResponse notificationSearchResponse = notificationSentMock.getNotificationSentPNMock();

        BffNotificationsResponseV1 bffNotificationsResponseV1 = NotificationSentMapper.modelMapper.toBffNotificationsResponseV1(notificationSearchResponse);
        assertNotNull(bffNotificationsResponseV1);

        assertEquals(bffNotificationsResponseV1.getResultsPage().get(0).getIun(), notificationSearchResponse.getResultsPage().get(0).getIun());
        assertEquals(bffNotificationsResponseV1.getResultsPage().get(0).getPaProtocolNumber(), notificationSearchResponse.getResultsPage().get(0).getPaProtocolNumber());
        assertEquals(bffNotificationsResponseV1.getResultsPage().get(0).getSender(), notificationSearchResponse.getResultsPage().get(0).getSender());
        assertEquals(bffNotificationsResponseV1.getResultsPage().get(0).getSentAt(), notificationSearchResponse.getResultsPage().get(0).getSentAt());
        assertEquals(bffNotificationsResponseV1.getResultsPage().get(0).getSubject(), notificationSearchResponse.getResultsPage().get(0).getSubject());
        assertEquals(bffNotificationsResponseV1.getResultsPage().get(0).getNotificationStatus().getValue(), notificationSearchResponse.getResultsPage().get(0).getNotificationStatus().getValue());
        assertEquals(bffNotificationsResponseV1.getResultsPage().get(0).getRecipients(), notificationSearchResponse.getResultsPage().get(0).getRecipients());
        assertEquals(bffNotificationsResponseV1.getResultsPage().get(0).getRequestAcceptedAt(), notificationSearchResponse.getResultsPage().get(0).getRequestAcceptedAt());
        assertEquals(bffNotificationsResponseV1.getResultsPage().get(0).getGroup(), notificationSearchResponse.getResultsPage().get(0).getGroup());

        assertEquals(bffNotificationsResponseV1.getMoreResult(), notificationSearchResponse.getMoreResult());
        assertEquals(bffNotificationsResponseV1.getNextPagesKey(), notificationSearchResponse.getNextPagesKey());

        BffNotificationsResponseV1 bffNotificationsResponseV1Null = NotificationSentMapper.modelMapper.toBffNotificationsResponseV1(null);
        assertNull(bffNotificationsResponseV1Null);
    }
}
