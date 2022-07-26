package org.sugarcubes.cloner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Copy policy based on predicates.
 *
 * @see Predicates
 *
 * @author Maxim Butov
 */
public class PredicatePolicy<I> extends AbstractMappedPolicy<I, Predicate<I>> {

    /**
     * Collector for the stream of copy actions.
     */
    private static final Collector<CopyAction, ?, CopyAction> ACTION_COLLECTOR = Collector.of(ActionState::new,
        ActionState::accumulate, ActionState::combine, state -> state.action, Collector.Characteristics.UNORDERED);

    /**
     * Creates predicate based policy.
     *
     * @param map map (predicate, action)
     */
    public PredicatePolicy(Map<Predicate<I>, CopyAction> map) {
        super(map);
    }

    /**
     * Creates predicate based policy.
     *
     * @param map map (predicate, action)
     * @param mapCopyConstructor copy constructor of map as method reference
     */
    public PredicatePolicy(Map<Predicate<I>, CopyAction> map,
        Function<Map<Predicate<I>, CopyAction>, Map<Predicate<I>, CopyAction>> mapCopyConstructor) {
        super(map, mapCopyConstructor);
    }

    @Override
    public CopyAction getAction(I input) {
        Stream<CopyAction> actions = entrySet().stream()
            .filter(e -> e.getKey().test(input))
            .map(Map.Entry::getValue);
        try {
            return actions.collect(ACTION_COLLECTOR);
        }
        catch (MultipleActionException e) {
            throw new ClonerException(String.format("Multiple actions found for %s: %s.", input, e.actions));
        }
    }

    static class MultipleActionException extends IllegalStateException {

        final List<CopyAction> actions;

        MultipleActionException(CopyAction... actions) {
            this.actions = Arrays.asList(actions);
        }

    }

    static class ActionState {

        CopyAction action = CopyAction.DEFAULT;

        void accumulate(CopyAction next) {
            if (next != CopyAction.DEFAULT) {
                if (action != CopyAction.DEFAULT) {
                    throw new MultipleActionException(action, next);
                }
                action = next;
            }
        }

        ActionState combine(ActionState other) {
            accumulate(other.action);
            return this;
        }

    }

}
