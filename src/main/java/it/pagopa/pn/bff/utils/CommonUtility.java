package it.pagopa.pn.bff.utils;

import java.util.List;

public class CommonUtility {

    public enum SourceChannel {
        TPP,
        WEB,
        B2B,
    }

    public static <T> List<T> safeList(List<T> values) {
        return values != null ? values : List.of();
    }
}
