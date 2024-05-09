package it.pagopa.pn.bff.utils.helpers;

import org.mockito.ArgumentMatcher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FluxMatcher<T> implements ArgumentMatcher<T> {

    private T argumentForComparison;
    private Flux<?> cachedArgumentToCompare = null;

    public FluxMatcher(T argumentForComparison) {
        this.argumentForComparison = argumentForComparison;
    }

    @Override
    public boolean matches(T argumentToCompare) {
        if (argumentToCompare == null || argumentForComparison == null) {
            return false;
        }
        if (!(argumentToCompare instanceof Flux) || !(argumentForComparison instanceof Flux)) {
            return false;
        }

        if (cachedArgumentToCompare == null) {
            // the matcher is called twice to test that the code is idempotent
            // the first time, everything goes ok but the second time the argumentToCompare is completed
            // and every action on it causes an error
            // (the block() at the end, close and complete the Flux)
            // so we cache the last result emitted by the Flux, and we rerun the check
            cachedArgumentToCompare = ((Flux<?>) argumentToCompare).cache();
        }

        Mono<Boolean> result = cachedArgumentToCompare
                .zipWith((Flux<?>) argumentForComparison)
                .map((args) -> args.getT1().equals(args.getT2()))
                .reduce(true, (res1, res2) -> res1.equals(true) && res2.equals(true))
                .cache();

        return Boolean.TRUE.equals(result.block());
    }
}