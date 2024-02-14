package it.pagopa.pn.bff.mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class DateMapper {
    public static OffsetDateTime mapOffset(Instant value) {
        if (value == null) {
            return null;
        }
        return OffsetDateTime.ofInstant(value, ZoneOffset.UTC);
    }
}
