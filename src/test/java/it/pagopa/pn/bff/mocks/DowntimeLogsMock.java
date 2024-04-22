package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnDowntimeEntry;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnFunctionality;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnFunctionalityStatus;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnStatusResponse;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class DowntimeLogsMock {

    private List<PnFunctionality> getFunctionalityMock() {
        List<PnFunctionality> functionalityList = new ArrayList<>();
        functionalityList.add(PnFunctionality.NOTIFICATION_VISUALIZATION);
        functionalityList.add(PnFunctionality.NOTIFICATION_CREATE);
        functionalityList.add(PnFunctionality.NOTIFICATION_WORKFLOW);
        return functionalityList;
    }

    private List<PnDowntimeEntry> getIncidentsMock() {
        List<PnDowntimeEntry> incidentList = new ArrayList<>();
        PnDowntimeEntry incident = new PnDowntimeEntry();
        incident.setStatus(PnFunctionalityStatus.KO);
        incident.setFunctionality(PnFunctionality.NOTIFICATION_CREATE);
        incident.setStartDate(OffsetDateTime.parse("2022-09-21T09:33:58.709695008Z"));
        incidentList.add(incident);
        return incidentList;
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
        statusResponse.setOpenIncidents(getIncidentsMock());
        return statusResponse;
    }
}