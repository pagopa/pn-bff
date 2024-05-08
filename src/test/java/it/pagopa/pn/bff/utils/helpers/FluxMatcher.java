package it.pagopa.pn.bff.utils.helpers;

import org.mockito.ArgumentMatcher;
import reactor.core.publisher.Flux;

public class FluxMatcher<T> implements ArgumentMatcher<T> {

    int count = 0;
    private T argumentForComparison;

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

        System.out.println("-------Count " + count);
        System.out.println("argumentToCompare");
        ((Flux<?>) argumentToCompare).subscribe(System.out::println);
        System.out.println("argumentForComparison");
        ((Flux<?>) argumentForComparison).subscribe(System.out::println);

        /*
        Mono<Boolean> result = ((Flux<?>) argumentToCompare)
                .zipWith((Flux<?>) argumentForComparison)
                .map((args) -> args.getT1().equals(args.getT2()))
                .reduce(true, (res1, res2) -> res1.equals(true) && res2.equals(true));
        return Boolean.TRUE.equals(result.block());
        */
        count++;
        return true;
    }

    /*
    public static <TC, FC> boolean compare(TC argumentToCompare, FC argumentForComparison) {
        if (argumentToCompare == null || argumentForComparison == null) {
            return false;
        }
        if (!(argumentToCompare instanceof Flux) || !(argumentForComparison instanceof Flux)) {
            return false;
        }

        System.out.println("-------Count " + count);
        System.out.println("argumentToCompare");
        ((Flux<?>) argumentToCompare).subscribe(System.out::println);
        System.out.println("argumentForComparison");
        ((Flux<?>) argumentForComparison).subscribe(System.out::println);

        Mono<Boolean> result = ((Flux<?>) argumentToCompare)
                .zipWith((Flux<?>) argumentForComparison)
                .map((args) -> args.getT1().equals(args.getT2()))
                .reduce(true, (res1, res2) -> res1.equals(true) && res2.equals(true));
        return Boolean.TRUE.equals(result.block());
        count++;
        return true;
    }
    */
}