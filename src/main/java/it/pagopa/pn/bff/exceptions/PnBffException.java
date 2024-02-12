package it.pagopa.pn.bff.exceptions;

import it.pagopa.pn.commons.exceptions.PnRuntimeException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class PnBffException extends PnRuntimeException {
    public PnBffException(@Nullable String message,
                          @Nullable String code,
                          @Nullable String detail,
                          int statusCode,
                          @NonNull String statusText,
                          @Nullable Throwable cause) {
        super(statusText, message, statusCode, code, null, detail, cause);
    }

    public static PnBffException wrapException(WebClientResponseException e) {
        return new PnBffException(e.getMessage(), null, null, e.getStatusCode().value(), e.getStatusText(), e);
    }
}
