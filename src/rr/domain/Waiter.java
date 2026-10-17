package rr.domain;

/** The Waiter seats customers, takes orders and serves READY dishes - one task per turn. */
public class Waiter extends Staff {
    public Waiter() { super("Waiter"); }

    public Table seat(Customer c, Restaurant restaurant) {
        ensureCanAct();
        Table t = restaurant.seat(c);
        markActed();
        return t;
    }

    public Order takeOrder(Customer c, Restaurant restaurant) {
        ensureCanAct();
        Order o = restaurant.takeOrderFrom(c);
        markActed();
        return o;
    }

    public void serve(Order o, int turn, Scoreboard scoreboard) {
        ensureCanAct();
        o.markServed(turn);
        o.getCustomer().markServed(turn);
        scoreboard.recordServed();
        markActed();
    }
}
