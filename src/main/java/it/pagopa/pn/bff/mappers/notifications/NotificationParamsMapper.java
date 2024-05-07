package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.LegalFactCategory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the parameters (from bff version to ms version and viceversa)
 * used to call the notification api
 */
@Mapper()
public interface NotificationParamsMapper {
    // Instance of the mapper
    NotificationParamsMapper modelMapper = Mappers.getMapper(NotificationParamsMapper.class);

    /**
     * Maps the bff LegalFactCategory to the delivery push LegalFactCategory
     *
     * @param legalFactCategory the LegalFactCategory to map
     * @return the mapped LegalFactCategory
     */
    it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactCategory mapLegalFactCategory(LegalFactCategory legalFactCategory);
}