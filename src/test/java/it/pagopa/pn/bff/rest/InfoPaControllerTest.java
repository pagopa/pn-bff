package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.config.PnBffConfigs;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.*;
import it.pagopa.pn.bff.mappers.infopa.GroupsMapper;
import it.pagopa.pn.bff.mappers.infopa.InstitutionMapper;
import it.pagopa.pn.bff.mappers.infopa.LanguageMapper;
import it.pagopa.pn.bff.mappers.infopa.ProductMapper;
import it.pagopa.pn.bff.mocks.PaInfoMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.InfoPaService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import it.pagopa.pn.bff.utils.helpers.MonoMatcher;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;

@Slf4j
@WebFluxTest(InfoPaController.class)
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = PnBffConfigs.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class InfoPaControllerTest {

    private final PaInfoMock paInfoMock = new PaInfoMock();
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    private PnBffConfigs pnBffConfigs;
    @MockBean
    private InfoPaService infoPaService;

    @Test
    void getInstitutionsV1() {
        List<BffInstitution> bffInstitutions = paInfoMock.getInstitutionResourcePNMock()
                .stream()
                .map(institution -> InstitutionMapper.modelMapper.toBffInstitution(institution, pnBffConfigs))
                .toList();
        Mockito
                .when(infoPaService.getInstitutions(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.nullable(List.class)))
                .thenReturn(Flux.fromIterable(bffInstitutions));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.INSTITUTIONS_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BffInstitution.class)
                .isEqualTo(bffInstitutions);
    }

    @Test
    void getInstitutionsV1Error() {
        Mockito
                .doThrow(new PnBffException("Error Message", "Error Description", HttpStatus.NOT_FOUND.value(), "Error Code"))
                .when(infoPaService).getInstitutions(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.nullable(List.class));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.INSTITUTIONS_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.toString())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getInstitutionProductV1() {
        List<BffInstitutionProduct> bffInstitutionProducts = paInfoMock.getProductResourcePNMock()
                .stream()
                .map(product -> ProductMapper.modelMapper.toBffInstitutionProduct(product, pnBffConfigs, UserMock.PN_CX_ID))
                .toList();
        Mockito
                .when(infoPaService.getInstitutionProducts(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.nullable(List.class)))
                .thenReturn(Flux.fromIterable(bffInstitutionProducts));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.INSTITUTIONS_PATH + "/products").build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.toString())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BffInstitutionProduct.class)
                .isEqualTo(bffInstitutionProducts);
    }

    @Test
    void getInstitutionProductErrorV1Error() {
        Mockito
                .doThrow(new PnBffException("Error Message", "Error Description", HttpStatus.NOT_FOUND.value(), "Error Code"))
                .when(infoPaService).getInstitutionProducts(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.nullable(List.class));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.INSTITUTIONS_PATH + "/products").build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.toString())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getGroupsV1() {
        List<BffPaGroup> paGroups = paInfoMock.getPaGroupsMock()
                .stream()
                .map(GroupsMapper.modelMapper::mapGroups)
                .toList();
        Mockito
                .when(infoPaService.getGroups(
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.nullable(BffPaGroupStatus.class)))
                .thenReturn(Flux.fromIterable(paGroups));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.GROUPS_PA_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BffPaGroup.class)
                .isEqualTo(paGroups);
    }

    @Test
    void getGroupsV1Error() {
        Mockito
                .when(infoPaService.getGroups(
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.nullable(BffPaGroupStatus.class)
                ))
                .thenReturn(Flux.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.GROUPS_PA_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getAdditionalLanguages() {
        BffAdditionalLanguages response = LanguageMapper.modelMapper.toBffAdditionalLanguages(paInfoMock.getAdditionalLanguagesMock());
        Mockito.when(infoPaService.getAdditionalLanguages(Mockito.anyString())).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.ADDITIONAL_LANGUAGES_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffAdditionalLanguages.class)
                .isEqualTo(response);

        Mockito.verify(infoPaService).getAdditionalLanguages(UserMock.PN_CX_ID);
    }

    @Test
    void getAdditionalLanguagesError() {
        Mockito.when(infoPaService.getAdditionalLanguages(Mockito.anyString()))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.ADDITIONAL_LANGUAGES_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(infoPaService).getAdditionalLanguages(UserMock.PN_CX_ID);
    }

    @Test
    void changeAdditionalLanguages() {
        BffAdditionalLanguages bffAdditionalLanguages = new BffAdditionalLanguages();
        bffAdditionalLanguages.setAdditionalLanguages(List.of("it", "en"));

        Mockito.when(infoPaService.changeAdditionalLanguages(Mockito.anyString(), Mockito.any())).thenReturn(Mono.just(bffAdditionalLanguages));

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.ADDITIONAL_LANGUAGES_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .bodyValue(bffAdditionalLanguages)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffAdditionalLanguages.class)
                .isEqualTo(bffAdditionalLanguages);

        Mockito.verify(infoPaService).changeAdditionalLanguages(Mockito.eq(UserMock.PN_CX_ID), argThat(new MonoMatcher<>(Mono.just(bffAdditionalLanguages))));
    }

    @Test
    void changeAdditionalLanguagesError() {
        BffAdditionalLanguages bffAdditionalLanguages = new BffAdditionalLanguages();
        bffAdditionalLanguages.setAdditionalLanguages(List.of("it", "en"));

        Mockito.when(infoPaService.changeAdditionalLanguages(Mockito.anyString(), Mockito.any()))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.put()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.ADDITIONAL_LANGUAGES_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .bodyValue(bffAdditionalLanguages)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(infoPaService)
                .changeAdditionalLanguages(Mockito.eq(UserMock.PN_CX_ID), argThat(new MonoMatcher<>(Mono.just(bffAdditionalLanguages))));

    }
}