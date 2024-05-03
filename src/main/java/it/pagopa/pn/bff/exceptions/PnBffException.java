package it.pagopa.pn.bff.exceptions;

import it.pagopa.pn.commons.exceptions.PnRuntimeException;
import it.pagopa.pn.commons.exceptions.dto.ProblemError;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PnBffException extends PnRuntimeException {
    public PnBffException(String message, String description, int status, @NotNull String errorCode, String detail, Throwable cause) {
        super(message, description, status, errorCode, null, detail, cause);
    }

    public PnBffException(@NotNull String message, @NotNull String description, int status, @NotNull String errorCode) {
        super(message, description, status, errorCode, null, null, null);
    }

    public PnBffException(@NotNull String message, @NotNull String description, int status, @NotNull List<ProblemError> problemErrorList, Throwable cause) {
        super(message, description, status, problemErrorList, cause);
    }
}