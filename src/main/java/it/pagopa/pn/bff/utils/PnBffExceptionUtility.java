package it.pagopa.pn.bff.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.Problem;
import it.pagopa.pn.bff.mappers.ProblemErrorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Objects;

import static it.pagopa.pn.commons.exceptions.PnExceptionsCodes.ERROR_CODE_PN_GENERIC_ERROR;

@Component
@RequiredArgsConstructor
public class PnBffExceptionUtility {
    private final ObjectMapper mapper;

    public PnBffException wrapException(WebClientResponseException e) {
        String responseBody = e.getResponseBodyAsString();
        try {
            Problem problem = mapper.readValue(responseBody, Problem.class);
            return new PnBffException(problem.getTitle(), Objects.requireNonNull(e.getMessage()), problem.getStatus(), ProblemErrorMapper.modelMapper.convertPnCommonsProblemError(problem.getErrors()), e);
        } catch (JsonProcessingException ex) {
            return new PnBffException(e.getMessage(), e.getMessage(), e.getStatusCode().value(), ERROR_CODE_PN_GENERIC_ERROR, e.getStatusText(), e);
        }
    }
}