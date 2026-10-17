package rr.domain;

import java.util.Arrays;
import java.util.List;

/** Decides who arrives on which turn. Stage 1: six Regular customers on turns 1,4,7,10,13,16. */
public class ArrivalSchedule {
    private final List<Integer> arrivalTurns;

    public ArrivalSchedule(List<Integer> arrivalTurns) { this.arrivalTurns = arrivalTurns; }

    public static ArrivalSchedule base() {
        return new ArrivalSchedule(Arrays.asList(1, 4, 7, 10, 13, 16));
    }

    public boolean hasArrival(int turn) { return arrivalTurns.contains(turn); }

    public Customer createCustomer(int turn, int customerId) {
        int arrivalNumber = arrivalTurns.indexOf(turn) + 1;
        return new RegularCustomer(customerId, arrivalNumber, turn);
    }

    public int totalArrivals() { return arrivalTurns.size(); }
}
