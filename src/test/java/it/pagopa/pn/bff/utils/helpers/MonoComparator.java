package it.pagopa.pn.bff.utils.helpers;

import reactor.core.publisher.Mono;

public class MonoComparator {

    public static <TC, FC> boolean compare(TC argumentToCompare, FC argumentForComparison) {
        if (argumentToCompare == null || argumentForComparison == null) {
            return false;
        }
        if (!(argumentToCompare instanceof Mono) || !(argumentForComparison instanceof Mono)) {
            return false;
        }
        Mono<Boolean> result = ((Mono<?>) argumentToCompare)
                .zipWith((Mono<?>) argumentForComparison)
                .map((args) -> args.getT1().equals(args.getT2()));
        return Boolean.TRUE.equals(result.block());
    }
}