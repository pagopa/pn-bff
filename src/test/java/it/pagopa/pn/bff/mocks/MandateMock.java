package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffAcceptRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNewMandateRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSearchMandateRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffUpdateRequest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private UserDto getUserMock(String firstName, String lastName, String fiscalCode) {
        UserDto user = new UserDto();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPerson(true);
        user.setDisplayName(user.getFirstName() + " " + user.getLastName());
        user.setFiscalCode(fiscalCode);
        return user;
    }

    public MandateDto getNewMandateRequestMock() {
        MandateDto newMandateRequest = new MandateDto();
        OffsetDateTime today = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime tomorrow = today.plusDays(1);
        newMandateRequest.setDateto(tomorrow.format(fmt));
        newMandateRequest.setDelegate(getUserMock("Mario", "Rossi", "RSSMRA80A01H501U"));
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

    private it.pagopa.pn.bff.generated.openapi.server.v1.dto.UserDto getBffUserMock(String firstName, String lastName, String fiscalCode) {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.UserDto user = new it.pagopa.pn.bff.generated.openapi.server.v1.dto.UserDto();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPerson(true);
        user.setDisplayName(user.getFirstName() + " " + user.getLastName());
        user.setFiscalCode(fiscalCode);
        return user;
    }

    public BffNewMandateRequest getBffNewMandateRequestMock() {
        BffNewMandateRequest newMandateRequest = new BffNewMandateRequest();
        OffsetDateTime today = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime tomorrow = today.plusDays(1);
        newMandateRequest.setDateto(tomorrow.format(fmt));
        newMandateRequest.setDelegate(getBffUserMock("Mario", "Rossi", "RSSMRA80A01H501U"));
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

    public UpdateRequestDto getUpdateRequestMock() {
        UpdateRequestDto updateRequestDto = new UpdateRequestDto();
        List<String> groups = new ArrayList<>();
        groups.add("group-1");
        groups.add("group-2");
        updateRequestDto.setGroups(groups);
        return updateRequestDto;
    }

    public BffUpdateRequest getBffUpdateRequestMock() {
        BffUpdateRequest updateRequest = new BffUpdateRequest();
        List<String> groups = new ArrayList<>();
        groups.add("group-1");
        groups.add("group-2");
        updateRequest.setGroups(groups);
        return updateRequest;
    }

    private MandateDto getMandateMock(int index, MandateDto.StatusEnum status, UserDto user, String userType) {
        OffsetDateTime today = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime tomorrow = today.plusDays(1);
        MandateDto mandate = new MandateDto();
        mandate.setMandateId(String.valueOf(index));
        mandate.setStatus(status);
        mandate.setVerificationCode("12345");
        mandate.setDatefrom(today.format(fmt));
        mandate.setDateto(tomorrow.format(fmt));
        List<OrganizationIdDto> visibilityIds = new ArrayList<>();
        visibilityIds.add(getOrganizationMock(index));
        visibilityIds.add(getOrganizationMock(index + 1));
        mandate.setVisibilityIds(visibilityIds);
        if (Objects.equals(userType, "delegate")) {
            mandate.setDelegate(user);
        } else {
            mandate.setDelegator(user);
        }
        return mandate;
    }

    public List<MandateDto> getMandatesByDelegateMock() {
        List<MandateDto> mandates = new ArrayList<>();
        mandates.add(getMandateMock(1, MandateDto.StatusEnum.PENDING, getUserMock("Mario", "Rossi", "RSSMRA80A01H501U"), "delegate"));
        mandates.add(getMandateMock(2, MandateDto.StatusEnum.ACTIVE, getUserMock("Davide", "Legato", "DVDLGT83C12H501C"), "delegate"));
        return mandates;
    }

    public SearchMandateRequestDto getSearchMandatesByDelegateRequestMock() {
        SearchMandateRequestDto searchRequest = new SearchMandateRequestDto();
        List<String> groups = new ArrayList<>();
        groups.add("group-1");
        groups.add("group-2");
        searchRequest.setGroups(groups);
        List<String> status = new ArrayList<>();
        status.add("pending");
        status.add("active");
        searchRequest.setStatus(status);
        searchRequest.setTaxId("RSSMRA80A01H501U");
        return searchRequest;
    }

    public BffSearchMandateRequest getBffSearchMandatesByDelegateRequestMock() {
        BffSearchMandateRequest searchRequest = new BffSearchMandateRequest();
        List<String> groups = new ArrayList<>();
        groups.add("group-1");
        groups.add("group-2");
        searchRequest.setGroups(groups);
        List<String> status = new ArrayList<>();
        status.add("pending");
        status.add("active");
        searchRequest.setStatus(status);
        searchRequest.setTaxId("RSSMRA80A01H501U");
        return searchRequest;
    }

    public SearchMandateResponseDto getSearchMandatesByDelegateResponseMock() {
        SearchMandateResponseDto searchResponse = new SearchMandateResponseDto();
        searchResponse.setMoreResult(false);
        List<String> nextPagesKey = new ArrayList<>();
        nextPagesKey.add("page-1");
        nextPagesKey.add("page-2");
        searchResponse.setNextPagesKey(nextPagesKey);
        List<MandateDto> mandates = new ArrayList<>();
        mandates.add(getMandateMock(1, MandateDto.StatusEnum.PENDING, getUserMock("Mario", "Rossi", "RSSMRA80A01H501U"), "delegate"));
        mandates.add(getMandateMock(2, MandateDto.StatusEnum.ACTIVE, getUserMock("Davide", "Legato", "DVDLGT83C12H501C"), "delegate"));
        searchResponse.setResultsPage(mandates);
        return searchResponse;
    }

    public List<MandateDto> getMandatesByDelegatorMock() {
        List<MandateDto> mandates = new ArrayList<>();
        mandates.add(getMandateMock(1, MandateDto.StatusEnum.PENDING, getUserMock("Sara", "Bianchi", "BNCSRA96L53H501D"), "delegator"));
        mandates.add(getMandateMock(2, MandateDto.StatusEnum.ACTIVE, getUserMock("Maria", "Verdi", "VRDMRA07P69H501I"), "delegator"));
        return mandates;
    }
}