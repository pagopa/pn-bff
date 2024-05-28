package it.pagopa.pn.bff.exceptions;

import it.pagopa.pn.commons.exceptions.PnRuntimeException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PnBffNotFoundException extends PnRuntimeException {
    public PnBffNotFoundException(String message, String description, String errorcode) {
        super(message, description, HttpStatus.NOT_FOUND.value(), errorcode, null, null);
    }

    public PnBffNotFoundException(String message, String description, String errorcode, Exception cause) {
        super(message, description, HttpStatus.NOT_FOUND.value(), errorcode, null, null, cause);
    }
}
