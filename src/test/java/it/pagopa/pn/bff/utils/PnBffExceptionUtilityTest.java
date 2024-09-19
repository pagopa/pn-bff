package it.pagopa.pn.bff.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.common.rest.error.v1.dto.Problem;
import it.pagopa.pn.common.rest.error.v1.dto.ProblemError;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PnBffExceptionUtilityTest {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static PnBffExceptionUtility pnBffExceptionUtility;

    @BeforeAll
    public static void setup() {
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        pnBffExceptionUtility = new PnBffExceptionUtility(mapper);
    }

    @Test
    void wrapExceptionWithProblem() throws JsonProcessingException {
        List<ProblemError> problemErrors = new ArrayList<>();
        ProblemError problemError1 = new ProblemError();
        problemError1.setCode("PN_ERROR_1");
        problemError1.setDetail("This the detail of the error 1");
        ProblemError problemError2 = new ProblemError();
        problemError2.setCode("PN_ERROR_2");
        problemError2.setDetail("This the detail of the error 2");
        problemErrors.add(problemError1);
        problemErrors.add(problemError2);
        Problem problem = new Problem("GENERIC_ERROR", 401, "Test title", "Test detail", "test-trace-id", OffsetDateTime.now(), problemErrors);
        WebClientResponseException exception = new WebClientResponseException("Test message", 400, "This is a test text", null, mapper.writeValueAsBytes(problem), null);
        PnBffException bffException = pnBffExceptionUtility.wrapException(exception);
        assertEquals(bffException.getProblem().getTitle(), problem.getTitle());
        assertEquals(bffException.getProblem().getDetail(), exception.getMessage());
        assertEquals(bffException.getProblem().getType(), problem.getType());
        assertEquals(bffException.getProblem().getStatus(), problem.getStatus());
        assertEquals(bffException.getProblem().getErrors().size(), problem.getErrors().size());
        for (int i = 0; i < bffException.getProblem().getErrors().size(); i++) {
            assertEquals(bffException.getProblem().getErrors().get(i).getCode(), problem.getErrors().get(i).getCode());
            assertEquals(bffException.getProblem().getErrors().get(i).getDetail(), problem.getErrors().get(i).getDetail());
        }
    }

    @Test
    void wrapExceptionWithoutProblem() throws JsonProcessingException {
        Map<String, String> dummyObject = new HashMap<>();
        dummyObject.put("name", "Mario");
        dummyObject.put("surname", "Rossi");
        WebClientResponseException exception = new WebClientResponseException("Test message", 400, "This is a test text", null, mapper.writeValueAsBytes(dummyObject), null);
        PnBffException bffException = pnBffExceptionUtility.wrapException(exception);
        assertEquals(bffException.getProblem().getTitle(), exception.getMessage());
        assertEquals(bffException.getProblem().getDetail(), exception.getMessage());
        assertEquals("GENERIC_ERROR", bffException.getProblem().getType());
        assertEquals(bffException.getProblem().getStatus(), exception.getStatusCode().value());
        assertEquals(bffException.getProblem().getErrors().size(), 1);
        for (int i = 0; i < bffException.getProblem().getErrors().size(); i++) {
            assertEquals("PN_GENERIC_ERROR", bffException.getProblem().getErrors().get(i).getCode());
            assertEquals(bffException.getProblem().getErrors().get(i).getDetail(), exception.getStatusText());
        }
    }

    @Test
    void wrapExceptionWithNullResponseBody() {
        WebClientResponseException exception = new WebClientResponseException("Test message", 400, "This is a test text", null, null, null);
        PnBffException bffException = pnBffExceptionUtility.wrapException(exception);
        assertEquals(bffException.getProblem().getTitle(), exception.getMessage());
        assertEquals(bffException.getProblem().getDetail(), exception.getMessage());
        assertEquals("GENERIC_ERROR", bffException.getProblem().getType());
        assertEquals(bffException.getProblem().getStatus(), exception.getStatusCode().value());
        assertEquals(bffException.getProblem().getErrors().size(), 1);
        for (int i = 0; i < bffException.getProblem().getErrors().size(); i++) {
            assertEquals("PN_GENERIC_ERROR", bffException.getProblem().getErrors().get(i).getCode());
            assertEquals(bffException.getProblem().getErrors().get(i).getDetail(), exception.getStatusText());
        }
    }
}