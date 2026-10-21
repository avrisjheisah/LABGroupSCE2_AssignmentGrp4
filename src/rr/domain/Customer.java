package rr.domain;


public abstract class Customer {
    public static final int MAX_SATISFACTION = 100;

    private final int id;
    private final int arrivalNumber;  
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


    public abstract String getTypeName();

    public abstract int getWaitLoss();

    public int discountedPrice(int baseCents) { return baseCents; }


    public MenuItem chooseItem(Menu menu) {
        if (!menu.getCombos().isEmpty() && arrivalNumber % 2 == 0) {
            return menu.getCombos().get((arrivalNumber / 2 - 1) % menu.getCombos().size());
        }
        return menu.getItems().get((arrivalNumber - 1) % menu.getItems().size());
    }

    public int reviewedSatisfaction() { return satisfaction; }


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

    public void recoverSatisfaction(int amount) { setSatisfaction(satisfaction + amount); }

    public boolean isOut() { return state == CustomerState.PAID || state == CustomerState.LEFT; }

    private void require(CustomerState expected, String problem) {
        if (state != expected) {
            throw new InvalidActionException("Customer C" + id + " " + problem + " (currently " + state + ").");
        }
    }

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
