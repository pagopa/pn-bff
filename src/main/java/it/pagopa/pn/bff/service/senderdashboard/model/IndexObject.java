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
    private Long overviewObjectProcessTimeMs;

    @JsonProperty
    private Long focusObjectProcessTimeMs;

    @JsonProperty
    private Long overviewObjectSizeByte;

    @JsonProperty
    private Long focusObjectSizeByte;

    @JsonProperty
    private Long overviewObjectLines;

    @JsonProperty
    private Long focusObjectLines;

    @JsonProperty
    private Map<String, SenderInfo> overviewSendersId = new HashMap<>();

    @JsonProperty
    private Map<String, SenderInfo> focusSendersId = new HashMap<>();

    @Data
    public static class SenderInfo {
        @JsonProperty
        private Integer start;

        @JsonProperty
        private Integer end;

        @JsonProperty
        private Integer numRows;
    }
}
