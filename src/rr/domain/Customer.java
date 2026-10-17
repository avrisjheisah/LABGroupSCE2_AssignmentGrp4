package rr.domain;

/**
 * A customer. The points that differ between kinds of customer (satisfaction loss per turn,
 * discount, review) are hooks that subclasses override, so the rest of the game never
 * needs to ask "what kind of customer is this?".
 */
public abstract class Customer {
    public static final int MAX_SATISFACTION = 100;

    private final int id;
    private final int arrivalNumber;   // 1 = first customer to arrive, 2 = second, ...
    private final int arrivalTurn;
    private int satisfaction = MAX_SATISFACTION;
    private CustomerState state = CustomerState.WAITING_FOR_TABLE;
    private Table table;
    private Order order;
    private int servedTurn = -1;
    private int readyToPayTurn = -1;

    protected Customer(int id, int arrivalNumber, int arrivalTurn) {
        this.id = id;
        this.arrivalNumber = arrivalNumber;
        this.arrivalTurn = arrivalTurn;
    }

    // ---- behaviour that varies by customer kind ----

    public abstract String getTypeName();

    /** Satisfaction lost at the end of each turn while not yet served. */
    public abstract int getWaitLoss();

    /** Price this customer pays for an item whose list price is baseCents. */
    public int discountedPrice(int baseCents) { return baseCents; }

    /**
     * Food choice rule (deterministic): the n-th customer to arrive picks the
     * ((n-1) mod menu size)-th item, so arrivals cycle Salad, Burger, Pasta, Salad, ...
     */
    public MenuItem chooseItem(Menu menu) {
        return menu.getItems().get((arrivalNumber - 1) % menu.getItems().size());
    }

    /** The satisfaction value that is recorded when the customer pays. */
    public int reviewedSatisfaction() { return satisfaction; }

    // ---- state transitions (each one checks its own precondition) ----

    public void seatAt(Table t) {
        require(CustomerState.WAITING_FOR_TABLE, "is not waiting in the queue");
        table = t;
        state = CustomerState.SEATED;
    }

    public void placeOrder(Order o) {
        require(CustomerState.SEATED, "is not seated and waiting to order");
        order = o;
        state = CustomerState.ORDERED;
    }

    public void markServed(int turn) {
        require(CustomerState.ORDERED, "has no order waiting to be served");
        state = CustomerState.SERVED;
        servedTurn = turn;
    }

    public boolean hasFinishedEating(int currentTurn) {
        return state == CustomerState.SERVED && servedTurn < currentTurn;
    }

    public void markReadyToPay(int turn) {
        require(CustomerState.SERVED, "has not been served");
        state = CustomerState.READY_TO_PAY;
        readyToPayTurn = turn;
    }

    /** Completes payment and returns the satisfaction value to record. */
    public int pay() {
        require(CustomerState.READY_TO_PAY, "is not ready to pay");
        state = CustomerState.PAID;
        return reviewedSatisfaction();
    }

    public void leaveUnhappy() { state = CustomerState.LEFT; }

    public boolean isUnserved() {
        return state == CustomerState.WAITING_FOR_TABLE || state == CustomerState.SEATED
                || state == CustomerState.ORDERED;
    }

    public void loseSatisfaction() {
        satisfaction = Math.max(0, satisfaction - getWaitLoss());
    }

    public boolean isOut() { return state == CustomerState.PAID || state == CustomerState.LEFT; }

    private void require(CustomerState expected, String problem) {
        if (state != expected) {
            throw new InvalidActionException("Customer C" + id + " " + problem + " (currently " + state + ").");
        }
    }

    // ---- getters ----
    public int getId() { return id; }
    public int getArrivalNumber() { return arrivalNumber; }
    public int getArrivalTurn() { return arrivalTurn; }
    public int getSatisfaction() { return satisfaction; }
    protected void setSatisfaction(int value) { satisfaction = Math.max(0, Math.min(MAX_SATISFACTION, value)); }
    public CustomerState getState() { return state; }
    public Table getTable() { return table; }
    public Order getOrder() { return order; }
    public int getReadyToPayTurn() { return readyToPayTurn; }
}
