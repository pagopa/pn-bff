package it.pagopa.pn.bff.mappers.infopa;

import lombok.Getter;

@Getter
public class ProductMapperContext {
    private final String institutionId;
    private final String lang;

    public ProductMapperContext(String institutionId, String lang) {
        this.institutionId = institutionId;
        this.lang = lang;
    }

}
