package it.pagopa.pn.bff.exceptions;

import it.pagopa.pn.commons.exceptions.PnRuntimeException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PnBffBadRequestException extends PnRuntimeException {
    public PnBffBadRequestException(String message, String description, String errorcode) {
        super(message, description, HttpStatus.BAD_REQUEST.value(), errorcode, null, null);
    }

    public PnBffBadRequestException(String message, String description, String errorcode, Exception cause) {
        super(message, description, HttpStatus.BAD_REQUEST.value(), errorcode, null, null, cause);
    }
}