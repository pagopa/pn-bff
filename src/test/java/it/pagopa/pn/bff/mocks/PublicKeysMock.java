package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PublicKeysMock {
    private ArrayList<String> getPublicKeyGroups(String... groups) { return new ArrayList<>(Arrays.asList(groups)); }

    private ArrayList<PublicKeyStatusHistory> getPublicKeyStatusHistory(PublicKeyStatusHistory... statusHistories) {
        return new ArrayList<>(Arrays.asList(statusHistories));
    }

    private PublicKeyRow getPublicKeyMock(String kid, String issuer, String name, String value, OffsetDateTime createdAt, PublicKeyStatus status, List<PublicKeyStatusHistory> statusHistory){
        PublicKeyRow publicKeyRow = new PublicKeyRow();
        publicKeyRow.setKid(kid);
        publicKeyRow.setIssuer(issuer);
        publicKeyRow.setName(name);
        publicKeyRow.setValue(value);
        publicKeyRow.setCreatedAt(createdAt);
        publicKeyRow.setStatus(status);
        publicKeyRow.setStatusHistory(statusHistory);

        return publicKeyRow;
    }

    public PublicKeysResponse getPublicKeysMock() {
        PublicKeysResponse publicKeysResponse = new PublicKeysResponse();
        publicKeysResponse.setTotal(3);
        publicKeysResponse.setCreatedAt("10/09/2024");
        publicKeysResponse.setLastKey("last-key");
        List<PublicKeyRow> publicKeyRows = new ArrayList<>();

        PublicKeyRow firstPublicKey = getPublicKeyMock(
                "9e522ef5-a024-4dbd-8a59-3e090c637659",
                "key-issuer",
                "Public Key 1",
                "key-value",
                OffsetDateTime.parse("2024-09-10T11:48:07.569784524Z"),
                PublicKeyStatus.CREATED,
                getPublicKeyStatusHistory(
                        new PublicKeyStatusHistory()
                                .status(PublicKeyStatus.CREATED)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi"),
                        new PublicKeyStatusHistory()
                                .status(PublicKeyStatus.BLOCKED)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi"),
                        new PublicKeyStatusHistory()
                                .status(PublicKeyStatus.ACTIVE)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi")
                )
        );

        PublicKeyRow secondPublicKey = getPublicKeyMock(
                "9e522ef5-a024-4dbd-8a59-3e090c637659",
                "key-issuer",
                "Public Key 2",
                "key-value",
                OffsetDateTime.parse("2024-09-10T11:48:07.569784524Z"),
                PublicKeyStatus.CREATED,
                getPublicKeyStatusHistory(
                        new PublicKeyStatusHistory()
                                .status(PublicKeyStatus.CREATED)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi"),
                        new PublicKeyStatusHistory()
                                .status(PublicKeyStatus.BLOCKED)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi"),
                        new PublicKeyStatusHistory()
                                .status(PublicKeyStatus.ACTIVE)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi")
                )
        );

        PublicKeyRow thirdPublicKey = getPublicKeyMock(
                "9e522ef5-a024-4dbd-8a59-3e090c637659",
                "key-issuer",
                "Public Key 3",
                "key-value",
                OffsetDateTime.parse("2024-09-10T11:48:07.569784524Z"),
                PublicKeyStatus.CREATED,
                getPublicKeyStatusHistory(
                        new PublicKeyStatusHistory()
                                .status(PublicKeyStatus.CREATED)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi"),
                        new PublicKeyStatusHistory()
                                .status(PublicKeyStatus.BLOCKED)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi"),
                        new PublicKeyStatusHistory()
                                .status(PublicKeyStatus.ACTIVE)
                                .date(OffsetDateTime.parse("2022-09-13T09:33:58.709695008Z"))
                                .changedByDenomination("Maria Rossi")
                )
        );

        publicKeyRows.add(firstPublicKey);
        publicKeyRows.add(secondPublicKey);
        publicKeyRows.add(thirdPublicKey);
        publicKeysResponse.setItems(publicKeyRows);

        return publicKeysResponse;
    }

    public PublicKeyRequest gePublicKeyRequestMock() {
        PublicKeyRequest publicKeyRequest = new PublicKeyRequest();

        publicKeyRequest.setName("mock-public-key-name");
        publicKeyRequest.setPublicKey("mock-public-key-value");
        publicKeyRequest.setAlgorithm(PublicKeyRequest.AlgorithmEnum.RS256);
        publicKeyRequest.setExponent("mock-public-key-exponent");

        return publicKeyRequest;
    }

    public PublicKeyResponse gePublicKeyResponseMock() {
        PublicKeyResponse publicKeyResponse = new PublicKeyResponse();

        publicKeyResponse.setIssuer("mock-public-key-issuer");
        publicKeyResponse.setKid("mock-public-key-kid");

        return publicKeyResponse;
    }
}
