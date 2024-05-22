package it.pagopa.pn.bff.mappers.inforecipient;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PgGroup;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PgGroupStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPgGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPgGroupStatus;
import it.pagopa.pn.bff.mocks.RecipientInfoMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GroupsMapperTest {
    private final RecipientInfoMock recipientInfoMock = new RecipientInfoMock();

    @Test
    void testGroupsMapper() {
        List<PgGroup> pgGroups = recipientInfoMock.getPgGroupsMock();

        List<BffPgGroup> bffPgGroups = pgGroups.stream()
                .map(GroupsMapper.modelMapper::mapGroups)
                .toList();
        assertNotNull(bffPgGroups);

        for (int i = 0; i < pgGroups.size(); i++) {
            BffPgGroup bffPgGroup = bffPgGroups.get(i);
            PgGroup pgGroup = pgGroups.get(i);
            assertEquals(bffPgGroup.getId(), pgGroup.getId());
            assertEquals(bffPgGroup.getName(), pgGroup.getName());
        }

        BffPgGroup pgGroupNull = GroupsMapper.modelMapper.mapGroups(null);
        assertNull(pgGroupNull);
    }

    @Test
    void testGroupStatusMapper() {
        BffPgGroupStatus bffPgGroupStatus = BffPgGroupStatus.ACTIVE;
        PgGroupStatus pgGroupStatus = GroupsMapper.modelMapper.mapGroupStatus(bffPgGroupStatus);

        assertEquals(bffPgGroupStatus.getValue(), pgGroupStatus.getValue());

        BffPgGroupStatus bffPgGroupStatusNull = null;
        PgGroupStatus pgGroupStatusNull = GroupsMapper.modelMapper.mapGroupStatus(null);
        assertNull(pgGroupStatusNull);
    }
}
