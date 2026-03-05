package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.notification_cost_service.model.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationCostDetailsMock {

    public NotificationCostRecipientResponse getNotificationCostRecipientResponseMock() {
        NotificationCostRecipientResponse response = new NotificationCostRecipientResponse();
        response.setTotalCost(getTotalCostMock());
        response.setPagoPaIntMode(PagoPaIntMode.ASYNC);
        response.setLastUpdate(OffsetDateTime.of(2026, 3, 2, 0, 0, 0, 0, java.time.ZoneOffset.UTC));
        return response;
    }

    private TotalCost getTotalCostMock() {
        TotalCost totalCost = new TotalCost();
        totalCost.setCostWithVat(1220);
        totalCost.setDetails(getTotalCostDetailsMock());
        return totalCost;
    }

    private TotalCostDetails getTotalCostDetailsMock() {
        TotalCostDetails details = new TotalCostDetails();
        details.setBaseCostDetail(getBaseCostDetailMock());
        details.setAnalogCostDetail(getAnalogCostDetailMock());
        details.setNotificationFeePolicy(NotificationFeePolicy.DELIVERY_MODE);
        return details;
    }

    private BaseCostDetail getBaseCostDetailMock() {
        BaseCostDetail baseCostDetail = new BaseCostDetail();
        baseCostDetail.setCost(100);

        List<BaseCostComponent> components = new ArrayList<>();
        components.add(getBaseCostComponentMock(50, BaseCostName.PA_FEE));
        components.add(getBaseCostComponentMock(50, BaseCostName.SEND_FEE));
        baseCostDetail.setBaseCostComponents(components);

        return baseCostDetail;
    }

    private BaseCostComponent getBaseCostComponentMock(int cost, BaseCostName costName) {
        BaseCostComponent component = new BaseCostComponent();
        component.setCost(cost);
        component.setCostName(costName);
        return component;
    }

    private AnalogCostDetail getAnalogCostDetailMock() {
        AnalogCostDetail analogCostDetail = new AnalogCostDetail();
        analogCostDetail.setCostWithVat(1000);
        analogCostDetail.setVat(22);

        List<AnalogCostComponent> components = new ArrayList<>();
        components.add(getAnalogCostComponentMock(400, AnalogCostName.FIRST_ATTEMPT, "AR"));
        components.add(getAnalogCostComponentMock(420, AnalogCostName.SECOND_ATTEMPT, "RS"));
        analogCostDetail.setAnalogCostComponents(components);

        return analogCostDetail;
    }

    private AnalogCostComponent getAnalogCostComponentMock(int cost, AnalogCostName analogCostName, String productType) {
        AnalogCostComponent component = new AnalogCostComponent();
        component.setCost(cost);
        component.setCostName(analogCostName);
        component.setProductType(productType);
        return component;
    }
}