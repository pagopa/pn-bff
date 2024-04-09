package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiKeysMock {

    private ArrayList<String> getApikeyGroups(String... groups) {
        return new ArrayList<>(Arrays.asList(groups));
    }

    private ArrayList<ApiKeyStatusHistory> getApikeyStatusHistory(ApiKeyStatusHistory... statusHistories) {
        return new ArrayList<>(Arrays.asList(statusHistories));
    }

    private ApiKeyRow getApikeyMock(String id, String value, String name, OffsetDateTime lastUpdate, ArrayList<String> groups, ApiKeyStatus status, ArrayList<ApiKeyStatusHistory> statusHistories) {
        ApiKeyRow apiKeyRow = new ApiKeyRow();
        apiKeyRow.setId(id);
        apiKeyRow.setValue(value);
        apiKeyRow.setName(name);
        apiKeyRow.setLastUpdate(lastUpdate);
        apiKeyRow.setGroups(groups);
        apiKeyRow.setStatus(status);
        apiKeyRow.setStatusHistory(statusHistories);
        return apiKeyRow;
    }

    public ApiKeysResponse getApiKeysMock() {
        ApiKeysResponse apiKeysResponse = new ApiKeysResponse();
        apiKeysResponse.setTotal(14);
        apiKeysResponse.setLastKey("9e522ef5-a024-4dbd-8a59-3e090c637653");
        apiKeysResponse.lastUpdate("22/09/2022");
        List<ApiKeyRow> apiKeyRows = new ArrayList<>();
        // first api key
        ApiKeyRow firstApiKey = getApikeyMock(
                "9e522ef5-a024-4dbd-8a59-3e090c637659",
                "9e522ef5-a024-4dbd-8a59-3e090c637650",
                "Rimborso e multe",
                OffsetDateTime.parse("2022-09-21T09:33:58.709695008Z"),
                getApikeyGroups("mock-group-1", "mock-group-2", "mock-group-3", "mock-group-4", "mock-group-5"),
                ApiKeyStatus.ENABLED,
                getApikeyStatusHistory(
                        new ApiKeyStatusHistory()
                                .status(ApiKeyStatus.CREATED)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi"),
                        new ApiKeyStatusHistory()
                                .status(ApiKeyStatus.BLOCKED)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi"),
                        new ApiKeyStatusHistory()
                                .status(ApiKeyStatus.ENABLED)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi")
                )
        );
        // second api key
        ApiKeyRow secondApiKey = getApikeyMock(
                "9e522ef5-a024-4dbd-8a59-3e090c637651",
                "9e522ef5-a024-4dbd-8a59-3e090c637652",
                "Cartelle esattoriali",
                OffsetDateTime.parse("2022-09-22T09:33:58.709695008Z"),
                getApikeyGroups("mock-group-2"),
                ApiKeyStatus.BLOCKED,
                getApikeyStatusHistory(
                        new ApiKeyStatusHistory()
                                .status(ApiKeyStatus.CREATED)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi"),
                        new ApiKeyStatusHistory()
                                .status(ApiKeyStatus.BLOCKED)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi"),
                        new ApiKeyStatusHistory()
                                .status(ApiKeyStatus.ENABLED)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi")
                )
        );
        // third api key
        ApiKeyRow thirdApiKey = getApikeyMock(
                "9e522ef5-a024-4dbd-8a59-3e090c637653",
                "9e522ef5-a024-4dbd-8a59-3e090c637654",
                "Rimborsi",
                OffsetDateTime.parse("2022-09-22T09:33:58.709695008Z"),
                getApikeyGroups("mock-group-3"),
                ApiKeyStatus.ROTATED,
                getApikeyStatusHistory(
                        new ApiKeyStatusHistory()
                                .status(ApiKeyStatus.CREATED)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi"),
                        new ApiKeyStatusHistory()
                                .status(ApiKeyStatus.BLOCKED)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi"),
                        new ApiKeyStatusHistory()
                                .status(ApiKeyStatus.ENABLED)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi")
                )
        );
        apiKeyRows.add(firstApiKey);
        apiKeyRows.add(secondApiKey);
        apiKeyRows.add(thirdApiKey);
        apiKeysResponse.setItems(apiKeyRows);
        return apiKeysResponse;
    }

    public RequestNewApiKey geRequestNewApiKeyMock() {
        RequestNewApiKey requestNewApiKey = new RequestNewApiKey();
        requestNewApiKey.setName("mock-api-key-name");
        List<String> groups = new ArrayList<>();
        groups.add("mock-id-1");
        groups.add("mock-id-3");
        requestNewApiKey.setGroups(groups);
        return requestNewApiKey;
    }

    public ResponseNewApiKey geResponseNewApiKeyMock() {
        ResponseNewApiKey responseNewApiKey = new ResponseNewApiKey();
        responseNewApiKey.setId("mock-api-key-id");
        responseNewApiKey.setApiKey("mock-api-key-value");
        return responseNewApiKey;
    }
}