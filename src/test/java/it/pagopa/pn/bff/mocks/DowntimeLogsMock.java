package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class DowntimeLogsMock {

    public List<PnFunctionality> getFunctionalityMock() {
        List<PnFunctionality> functionalityList = new ArrayList<>();
        functionalityList.add(PnFunctionality.NOTIFICATION_VISUALIZATION);
        functionalityList.add(PnFunctionality.NOTIFICATION_CREATE);
        functionalityList.add(PnFunctionality.NOTIFICATION_WORKFLOW);
        return functionalityList;
    }

    private PnDowntimeEntry getIncidentMock(PnFunctionality functionality, OffsetDateTime endDate, String legalFactId) {
        PnDowntimeEntry incident = new PnDowntimeEntry();
        incident.setStatus(PnFunctionalityStatus.KO);
        incident.setFunctionality(functionality);
        incident.setStartDate(OffsetDateTime.parse("2022-09-21T09:33:58.709695008Z"));
        if (endDate != null) {
            incident.setEndDate(endDate);
        }
        if (legalFactId != null) {
            incident.setFileAvailable(true);
            incident.setLegalFactId(legalFactId);
        }
        return incident;
    }

    public PnStatusResponse getStatusMockOK() {
        PnStatusResponse statusResponse = new PnStatusResponse();
        statusResponse.setStatus(200);
        statusResponse.setTitle("OK");
        statusResponse.setDetail("OK");
        statusResponse.setFunctionalities(getFunctionalityMock());
        return statusResponse;
    }

    public PnStatusResponse getStatusMockKO() {
        PnStatusResponse statusResponse = new PnStatusResponse();
        statusResponse.setStatus(500);
        statusResponse.setTitle("KO");
        statusResponse.setDetail("KO");
        statusResponse.setFunctionalities(getFunctionalityMock());
        List<PnDowntimeEntry> downtimeEntries = new ArrayList<>();
        downtimeEntries.add(getIncidentMock(PnFunctionality.NOTIFICATION_CREATE, null, null));
        statusResponse.setOpenIncidents(downtimeEntries);
        return statusResponse;
    }

    public PnDowntimeHistoryResponse getDowntimeHistoryMock() {
        PnDowntimeHistoryResponse downtimeHistoryResponse = new PnDowntimeHistoryResponse();
        downtimeHistoryResponse.setNextPage("1");
        List<PnDowntimeEntry> downtimeEntries = new ArrayList<>();
        downtimeEntries.add(getIncidentMock(PnFunctionality.NOTIFICATION_CREATE, null, null));
        downtimeEntries.add(getIncidentMock(PnFunctionality.NOTIFICATION_VISUALIZATION, OffsetDateTime.parse("2022-07-19T09:33:58.709695008Z"), "LEGAL_FACT_1"));
        downtimeEntries.add(getIncidentMock(PnFunctionality.NOTIFICATION_VISUALIZATION, OffsetDateTime.parse("2022-07-01T09:33:58.709695008Z"), null));
        downtimeEntries.add(getIncidentMock(PnFunctionality.NOTIFICATION_WORKFLOW, OffsetDateTime.parse("2022-07-19T09:33:58.709695008Z"), "LEGAL_FACT_2"));
        downtimeHistoryResponse.setResult(downtimeEntries);
        return downtimeHistoryResponse;
    }
}