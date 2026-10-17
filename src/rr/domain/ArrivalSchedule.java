package rr.domain;

import java.util.Arrays;
import java.util.List;

/** Decides who arrives on which turn (turn numbers and the kind of customer for each arrival). */
public class ArrivalSchedule {
    private final List<Integer> arrivalTurns;
    private final List<CustomerKind> kinds;

    public ArrivalSchedule(List<Integer> arrivalTurns, List<CustomerKind> kinds) {
        this.arrivalTurns = arrivalTurns;
        this.kinds = kinds;
    }

    private static final List<Integer> BASE_TURNS = Arrays.asList(1, 4, 7, 10, 13, 16);

    /** Stage 1 schedule: six Regular customers. */
    public static ArrivalSchedule base() {
        return new ArrivalSchedule(BASE_TURNS, Arrays.asList(CustomerKind.REGULAR, CustomerKind.REGULAR,
                CustomerKind.REGULAR, CustomerKind.REGULAR, CustomerKind.REGULAR, CustomerKind.REGULAR));
    }

    /** Stage 2 schedule: same six turns, with a Critic (2nd arrival) and a VIP (3rd arrival). */
    public static ArrivalSchedule mixed() {
        return new ArrivalSchedule(BASE_TURNS, Arrays.asList(CustomerKind.REGULAR, CustomerKind.CRITIC,
                CustomerKind.VIP, CustomerKind.REGULAR, CustomerKind.REGULAR, CustomerKind.REGULAR));
    }

    public boolean hasArrival(int turn) { return arrivalTurns.contains(turn); }

    public Customer createCustomer(int turn, int customerId) {
        int index = arrivalTurns.indexOf(turn);
        return kinds.get(index).create(customerId, index + 1, turn);
    }

    public int totalArrivals() { return arrivalTurns.size(); }
}
