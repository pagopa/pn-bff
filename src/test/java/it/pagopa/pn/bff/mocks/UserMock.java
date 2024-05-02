package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroupStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserMock {

    public static final String PN_UID = "UID";
    public static final String PN_CX_ID = "CX_ID";
    public static final List<String> PN_CX_GROUPS = Collections.singletonList("group");
    public static final String RECIPIENT_ID = "RECIPIENT_ID";
    public static final String SENDER_ID = "SENDER_ID";
    public static final String MANDATE_ID = "MANDATE_ID";
    public static final String SUBJECT_REG_EXP = "SUBJECT";
    public static final String IUN_MATCH = "IUN";
    public static final NotificationStatus STATUS = NotificationStatus.ACCEPTED;
    public static final int SIZE = 10;
    public static final String START_DATE = "2014-04-30T00:00:00.000Z";
    public static final String END_DATE = "2024-04-30T00:00:00.000Z";
    public static final String GROUP = "GROUP";
    public static final String NEXT_PAGES_KEY = "NEXT_PAGES_KEY";

    private PaGroup getPaGroupMock(String id, String name, PaGroupStatus status) {
        PaGroup group = new PaGroup();
        group.setId(id);
        group.setName(name);
        group.setDescription("This is a group for test purpose");
        group.setStatus(status);
        return group;
    }

    public List<PaGroup> getPaGroupsMock() {
        List<PaGroup> paGroups = new ArrayList<>();
        // first group
        paGroups.add(getPaGroupMock("mock-id-1", "mock-group-1", PaGroupStatus.ACTIVE));
        // second group
        paGroups.add(getPaGroupMock("mock-id-2", "mock-group-2", PaGroupStatus.ACTIVE));
        // third group
        paGroups.add(getPaGroupMock("mock-id-3", "mock-group-3", PaGroupStatus.ACTIVE));
        // fourth group
        paGroups.add(getPaGroupMock("mock-id-4", "mock-group-4", PaGroupStatus.ACTIVE));
        return paGroups;
    }
}