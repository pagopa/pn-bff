package it.pagopa.pn.bff.mappers.infopa;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup;
import it.pagopa.pn.bff.mocks.PaInfoMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GroupsMapperTest {
    private final PaInfoMock paInfoMock = new PaInfoMock();

    @Test
    void testGroupsMapper() {
        List<PaGroup> paGroups = paInfoMock.getPaGroupsMock();

        List<it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaGroup> bffPaGroups = paGroups.stream()
                .map(GroupsMapper.modelMapper::mapGroups)
                .toList();
        assertNotNull(bffPaGroups);

        for (int i = 0; i < paGroups.size(); i++) {
            it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaGroup bffPaGroup = bffPaGroups.get(i);
            PaGroup paGroup = paGroups.get(i);
            assertEquals(bffPaGroup.getId(), paGroup.getId());
            assertEquals(bffPaGroup.getName(), paGroup.getName());
            assertEquals(bffPaGroup.getDescription(), paGroup.getDescription());
            assertEquals(bffPaGroup.getStatus().getValue(), paGroup.getStatus().getValue());
        }

        List<it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaGroup> bffPaGroupsNull = paGroups.stream()
                .map(GroupsMapper.modelMapper::mapGroups)
                .toList();
        assertNotNull(bffPaGroupsNull);
    }
}