package it.pagopa.pn.bff.mappers;

import it.pagopa.pn.common.rest.error.v1.dto.ProblemError;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapstruct mapper interface, used to map the bff List<ProblemError>
 * to the microservice List<ProblemError>
 */
@Mapper
public interface ProblemErrorMapper {
    ProblemErrorMapper modelMapper = Mappers.getMapper(ProblemErrorMapper.class);

    /**
     * Map bff List<ProblemError> to the pn-commons List<ProblemError>
     *
     * @param problems bff List<ProblemError>
     * @return the mapped List<ProblemError>
     */
    List<it.pagopa.pn.commons.exceptions.dto.ProblemError> convertPnCommonsProblemError(List<ProblemError> problems);

}