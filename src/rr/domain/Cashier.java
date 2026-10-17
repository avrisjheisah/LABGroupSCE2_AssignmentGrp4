package rr.domain;

/** The Cashier automatically takes one payment per turn (the oldest READY_TO_PAY customer). */
public class Cashier extends Staff {
    public Cashier() { super("Cashier"); }

    /** Collects payment and returns the satisfaction value that was recorded. */
    public int collect(Customer c, Scoreboard scoreboard, int turn) {
        ensureCanAct();
        if (c.getState() != CustomerState.READY_TO_PAY || c.getReadyToPayTurn() >= turn) {
            throw new InvalidActionException("Customer C" + c.getId() + " cannot pay yet.");
        }
        int recorded = c.pay();
        Order o = c.getOrder();
        o.markPaid();
        c.getTable().release();
        scoreboard.recordPayment(o.getBillCents(), recorded);
        markActed();
        return recorded;
    }
}
