package rr.domain;

/** A single order. The bill is locked in at the moment the order is taken. */
public class Order {
    private final int id;
    private final Customer customer;
    private final MenuItem item;
    private final int billCents;
    private int unitsDone = 0;
    private int readyTurn = -1;
    private OrderStatus status = OrderStatus.PLACED;

    public Order(int id, Customer customer, MenuItem item, int billCents) {
        this.id = id;
        this.customer = customer;
        this.item = item;
        this.billCents = billCents;
    }

    public int getId() { return id; }
    public Customer getCustomer() { return customer; }
    public MenuItem getItem() { return item; }
    public int getBillCents() { return billCents; }
    public int getUnitsDone() { return unitsDone; }
    public int getReadyTurn() { return readyTurn; }
    public OrderStatus getStatus() { return status; }

    public boolean isCookable() {
        return status == OrderStatus.PLACED || status == OrderStatus.PREPARING;
    }

    /** The Chef spends one prep unit on this order. */
    public void advance(int turn) {
        if (!isCookable()) {
            throw new InvalidActionException("Order #" + id + " is " + status + " - there is nothing left to cook.");
        }
        unitsDone++;
        if (unitsDone >= item.getPrepUnits()) {
            status = OrderStatus.READY;
            readyTurn = turn;
        } else {
            status = OrderStatus.PREPARING;
        }
    }

    public void markServed(int turn) {
        if (status != OrderStatus.READY) {
            throw new InvalidActionException("Order #" + id + " is " + status + ", not READY, so it cannot be served.");
        }
        if (readyTurn >= turn) {
            throw new InvalidActionException("Order #" + id + " only became READY this turn; it can be served from next turn.");
        }
        status = OrderStatus.SERVED;
    }

    public void markPaid() { status = OrderStatus.PAID; }

    public void cancel() { status = OrderStatus.CANCELLED; }
}
