package it.pagopa.pn.bff.service.senderdashboard.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public final class IndexObject {

    @JsonProperty
    private String bucketName;

    @JsonProperty
    private String bucketRegion;

    @JsonProperty
    private String overviewObjectKey;

    @JsonProperty
    private String focusObjectKey;

    @JsonProperty
    private String overviewObjectVersionId;

    @JsonProperty
    private String focusObjectVersionId;

    @JsonProperty
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime genTimestamp;

    @JsonProperty
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @JsonProperty
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate lastDate;

    @JsonProperty
    private long overviewObjectProcessTimeMs;

    @JsonProperty
    private long focusObjectProcessTimeMs;

    @JsonProperty
    private long overviewObjectSizeByte;

    @JsonProperty
    private long focusObjectSizeByte;

    @JsonProperty
    private long overviewObjectLines;

    @JsonProperty
    private long focusObjectLines;

    @JsonProperty
    private Map<String, SenderInfo> overviewSendersId = new HashMap<>();

    @JsonProperty
    private Map<String, SenderInfo> focusSendersId = new HashMap<>();

    @Data
    public static class SenderInfo {
        @JsonProperty
        private int start;

        @JsonProperty
        private int end;

        @JsonProperty
        private int numRows;
    }
}
