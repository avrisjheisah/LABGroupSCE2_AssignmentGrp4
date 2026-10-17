package rr.domain;

/** The Host seats one queued customer per turn, in addition to the Waiter. */
public class Host extends Staff {
    public Host() { super("Host"); }

    public Table seat(Customer c, Restaurant restaurant) {
        ensureCanAct();
        Table t = restaurant.seat(c);
        markActed();
        return t;
    }
}
