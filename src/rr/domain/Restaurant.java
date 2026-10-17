package rr.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The restaurant: owns the tables, queue, orders, staff and scoreboard, and enforces the
 * rules about who may be seated / have an order taken.
 */
public class Restaurant {
    private final List<Table> tables = new ArrayList<>();
    private final List<Customer> queue = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final Menu menu;
    private final Waiter waiter = new Waiter();
    private final Chef chef = new Chef();
    private final Cashier cashier = new Cashier();
    private final Host host = new Host();
    private final HappyHour happyHour = new HappyHour();
    private int currentTurn;
    private final Scoreboard scoreboard = new Scoreboard();
    private int nextOrderId = 1;

    public Restaurant(GameConfig config, Menu menu) {
        this.menu = menu;
        for (int i = 1; i <= config.getTableCount(); i++) tables.add(new Table(i));
    }

    /** Called at the start of every turn. */
    public void startTurn(int turn) {
        currentTurn = turn;
        host.startTurn();
        waiter.startTurn();
        chef.startTurn();
        cashier.startTurn();
    }

    public void admit(Customer c) {
        customers.add(c);
        queue.add(c);
    }

    /** Seats a queued customer at the lowest-numbered free table. */
    public Table seat(Customer c) {
        if (!queue.contains(c)) {
            throw new InvalidActionException("Customer C" + c.getId() + " is not in the waiting queue.");
        }
        Table free = firstFreeTable();
        if (free == null) throw new InvalidActionException("There is no free table.");
        free.occupy(c);
        c.seatAt(free);
        queue.remove(c);
        return free;
    }

    /** The customer chooses a dish; the bill is calculated and locked in now. */
    public Order takeOrderFrom(Customer c) {
        if (c.getState() != CustomerState.SEATED) {
            throw new InvalidActionException("Customer C" + c.getId() + " is not seated and waiting to order (currently " + c.getState() + ").");
        }
        MenuItem item = c.chooseItem(menu);
        Order o = new Order(nextOrderId++, c, item, priceFor(c, item));
        c.placeOrder(o);
        orders.add(o);
        return o;
    }

    /** Pricing order: customer discount first (VIP 10%), then Happy Hour (20%); rounded in whole cents. */
    protected int priceFor(Customer c, MenuItem item) {
        int price = c.discountedPrice(item.getPriceCents());
        if (happyHour.isActive(currentTurn)) price = happyHour.applyDiscount(price);
        return price;
    }

    /** Happy Hour boost: every unserved customer recovers satisfaction. Returns how many were boosted. */
    public int applyHappyHourBoost() {
        int count = 0;
        for (Customer c : customers) {
            if (c.isUnserved()) {
                c.recoverSatisfaction(HappyHour.SATISFACTION_BOOST);
                count++;
            }
        }
        return count;
    }

    public boolean anyStaffHasActed() {
        return host.hasActed() || waiter.hasActed() || chef.hasActed() || cashier.hasActed();
    }

    /** A customer whose satisfaction hit 0 walks out; their order is cancelled and the table freed. */
    public void customerLeaves(Customer c) {
        queue.remove(c);
        if (c.getTable() != null) c.getTable().release();
        if (c.getOrder() != null) c.getOrder().cancel();
        c.leaveUnhappy();
        scoreboard.recordDeparture();
    }

    // ---- queries ----

    private Table firstFreeTable() {
        for (Table t : tables) if (t.isFree()) return t;
        return null;
    }

    public List<Customer> seatedAwaitingOrder() {
        List<Customer> result = new ArrayList<>();
        for (Customer c : customers) if (c.getState() == CustomerState.SEATED) result.add(c);
        return result;
    }

    public List<Order> servableOrders(int turn) {
        List<Order> result = new ArrayList<>();
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.READY && o.getReadyTurn() < turn) result.add(o);
        }
        return result;
    }

    public List<Order> cookableOrders() {
        List<Order> result = new ArrayList<>();
        for (Order o : orders) if (o.isCookable()) result.add(o);
        return result;
    }

    /** Oldest READY_TO_PAY customer who became ready on an earlier turn, or null. */
    public Customer nextCustomerToPay(int turn) {
        List<Customer> candidates = new ArrayList<>();
        for (Customer c : customers) {
            if (c.getState() == CustomerState.READY_TO_PAY && c.getReadyToPayTurn() < turn) candidates.add(c);
        }
        if (candidates.isEmpty()) return null;
        Collections.sort(candidates, Comparator.comparingInt(Customer::getReadyToPayTurn).thenComparingInt(Customer::getId));
        return candidates.get(0);
    }

    public Customer findCustomer(int id) {
        for (Customer c : customers) if (c.getId() == id) return c;
        throw new InvalidActionException("There is no customer C" + id + ".");
    }

    public Order findOrder(int id) {
        for (Order o : orders) if (o.getId() == id) return o;
        throw new InvalidActionException("There is no order #" + id + ".");
    }

    public boolean hasFreeTable() { return firstFreeTable() != null; }

    public List<Table> getTables() { return Collections.unmodifiableList(tables); }
    public List<Customer> getQueue() { return Collections.unmodifiableList(queue); }
    public List<Customer> getCustomers() { return Collections.unmodifiableList(customers); }
    public List<Order> getOrders() { return Collections.unmodifiableList(orders); }
    public Menu getMenu() { return menu; }
    public Host getHost() { return host; }
    public HappyHour getHappyHour() { return happyHour; }
    public Waiter getWaiter() { return waiter; }
    public Chef getChef() { return chef; }
    public Cashier getCashier() { return cashier; }
    public Scoreboard getScoreboard() { return scoreboard; }
}
