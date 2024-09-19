package it.pagopa.pn.bff.mappers.infopa;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroupStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.BffPaGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.BffPaGroupStatus;
import it.pagopa.pn.bff.mocks.PaInfoMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GroupsMapperTest {
    private final PaInfoMock paInfoMock = new PaInfoMock();

    @Test
    void testGroupsMapper() {
        List<PaGroup> paGroups = paInfoMock.getPaGroupsMock();

        List<BffPaGroup> bffPaGroups = paGroups.stream()
                .map(GroupsMapper.modelMapper::mapGroups)
                .toList();
        assertNotNull(bffPaGroups);

        for (int i = 0; i < paGroups.size(); i++) {
            BffPaGroup bffPaGroup = bffPaGroups.get(i);
            PaGroup paGroup = paGroups.get(i);
            assertEquals(bffPaGroup.getId(), paGroup.getId());
            assertEquals(bffPaGroup.getName(), paGroup.getName());
        }

        BffPaGroup paGroupNull = GroupsMapper.modelMapper.mapGroups(null);
        assertNull(paGroupNull);
    }

    @Test
    void testGroupStatusMapper() {
        BffPaGroupStatus bffPaGroupStatus = BffPaGroupStatus.ACTIVE;
        PaGroupStatus paGroupStatus = GroupsMapper.modelMapper.mapGroupStatus(bffPaGroupStatus);

        assertEquals(bffPaGroupStatus.getValue(), paGroupStatus.getValue());

        BffPaGroupStatus bffPaGroupStatusNull = null;
        PaGroupStatus paGroupStatusNull = GroupsMapper.modelMapper.mapGroupStatus(null);
        assertNull(paGroupStatusNull);
    }
}