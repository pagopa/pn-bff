package it.pagopa.pn.bff.utils.helpers;

public class MonoHelpers {

    public static <TC, FC> boolean monoComparator(TC argumentToCompare, FC argumentForComparison) {
        if (argumentToCompare == null || argumentForComparison == null) {
            return false;
        }
        if (!(argumentToCompare instanceof reactor.core.publisher.Mono) || !(argumentForComparison instanceof reactor.core.publisher.Mono)) {
            return false;
        }
        reactor.core.publisher.Mono<Boolean> result = ((reactor.core.publisher.Mono<?>) argumentToCompare)
                .zipWith((reactor.core.publisher.Mono<?>) argumentForComparison)
                .map((args) -> args.getT1().equals(args.getT2()));
        return Boolean.TRUE.equals(result.block());
    }
}
