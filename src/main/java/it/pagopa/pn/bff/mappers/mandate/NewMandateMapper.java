package it.pagopa.pn.bff.mappers.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.MandateDto;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNewMandateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Mapstruct mapper interface, used to map the BffNewMandateRequest to the MandateDto
 */
@Mapper
public interface NewMandateMapper {
    NewMandateMapper modelMapper = Mappers.getMapper(NewMandateMapper.class);

    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Maps a BffNewMandateRequest to a MandateDto
     *
     * @param request the BffNewMandateRequest to map
     * @return the mapped MandateDto
     */
    @Mapping(target = "datefrom", expression = "java( calcFromDate() )")
    MandateDto mapRequest(BffNewMandateRequest request);

    /**
     * Calc the from date. It is the date when the request is sent in format 'yyyy-MM-dd'
     *
     * @return the calculated from date
     */
    default String calcFromDate() {
        return OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS).format(fmt);
    }
}