package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffAcceptRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNewMandateRequest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MandateMock {

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public MandateCountsDto getCountMock() {
        MandateCountsDto count = new MandateCountsDto();
        count.setValue(10);
        return count;
    }

    private OrganizationIdDto getOrganizationMock(int index) {
        OrganizationIdDto organization = new OrganizationIdDto();
        organization.setName("Organization " + index);
        organization.setUniqueIdentifier("organization-" + index);
        return organization;
    }

    public MandateDto getNewMandateRequestMock() {
        MandateDto newMandateRequest = new MandateDto();
        OffsetDateTime today = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime tomorrow = today.plusDays(1);
        newMandateRequest.setDateto(tomorrow.format(fmt));
        UserDto delegate = new UserDto();
        delegate.setFirstName("Mario");
        delegate.setLastName("Rossi");
        delegate.setPerson(true);
        delegate.setDisplayName(delegate.getFirstName() + " " + delegate.getLastName());
        delegate.setFiscalCode("RSSMRA80A01H501U");
        newMandateRequest.setDelegate(delegate);
        List<OrganizationIdDto> visibilityIds = new ArrayList<>();
        visibilityIds.add(getOrganizationMock(1));
        visibilityIds.add(getOrganizationMock(2));
        visibilityIds.add(getOrganizationMock(3));
        newMandateRequest.setVisibilityIds(visibilityIds);
        newMandateRequest.setVerificationCode("12345");
        return newMandateRequest;
    }

    private it.pagopa.pn.bff.generated.openapi.server.v1.dto.OrganizationIdDto getBffOrganizationMock(int index) {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.OrganizationIdDto organization = new it.pagopa.pn.bff.generated.openapi.server.v1.dto.OrganizationIdDto();
        organization.setName("Organization " + index);
        organization.setUniqueIdentifier("organization-" + index);
        return organization;
    }

    public BffNewMandateRequest getBffNewMandateRequestMock() {
        BffNewMandateRequest newMandateRequest = new BffNewMandateRequest();
        OffsetDateTime today = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime tomorrow = today.plusDays(1);
        newMandateRequest.setDateto(tomorrow.format(fmt));
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.UserDto delegate = new it.pagopa.pn.bff.generated.openapi.server.v1.dto.UserDto();
        delegate.setFirstName("Mario");
        delegate.setLastName("Rossi");
        delegate.setPerson(true);
        delegate.setDisplayName(delegate.getFirstName() + " " + delegate.getLastName());
        delegate.setFiscalCode("RSSMRA80A01H501U");
        newMandateRequest.setDelegate(delegate);
        List<it.pagopa.pn.bff.generated.openapi.server.v1.dto.OrganizationIdDto> visibilityIds = new ArrayList<>();
        visibilityIds.add(getBffOrganizationMock(1));
        visibilityIds.add(getBffOrganizationMock(2));
        visibilityIds.add(getBffOrganizationMock(3));
        newMandateRequest.setVisibilityIds(visibilityIds);
        newMandateRequest.setVerificationCode("12345");
        return newMandateRequest;
    }

    private GroupDto getGroupMock(int index) {
        GroupDto group = new GroupDto();
        group.setName("Group " + index);
        group.setId("group-" + index);
        return group;
    }

    public MandateDto getNewMandateResponseMock() {
        MandateDto newMandateResponse = getNewMandateRequestMock();
        newMandateResponse.setMandateId("mandate-id");
        List<GroupDto> groups = new ArrayList<>();
        groups.add(getGroupMock(1));
        groups.add(getGroupMock(2));
        newMandateResponse.setGroups(groups);
        newMandateResponse.setStatus(MandateDto.StatusEnum.PENDING);
        return newMandateResponse;
    }

    public AcceptRequestDto getAcceptRequestMock() {
        AcceptRequestDto acceptRequest = new AcceptRequestDto();
        List<String> groups = new ArrayList<>();
        groups.add("group-1");
        groups.add("group-2");
        acceptRequest.setGroups(groups);
        acceptRequest.setVerificationCode("12345");
        return acceptRequest;
    }

    public BffAcceptRequest getBffAcceptRequestMock() {
        BffAcceptRequest acceptRequest = new BffAcceptRequest();
        List<String> groups = new ArrayList<>();
        groups.add("group-1");
        groups.add("group-2");
        acceptRequest.setGroups(groups);
        acceptRequest.setVerificationCode("12345");
        return acceptRequest;
    }
}