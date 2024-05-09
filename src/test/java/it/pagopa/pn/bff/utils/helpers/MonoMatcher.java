package it.pagopa.pn.bff.utils.helpers;

import org.mockito.ArgumentMatcher;
import reactor.core.publisher.Mono;

public class MonoMatcher<T> implements ArgumentMatcher<T> {

    private T argumentForComparison;

    public MonoMatcher(T argumentForComparison) {
        this.argumentForComparison = argumentForComparison;
    }

    @Override
    public boolean matches(T argumentToCompare) {
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