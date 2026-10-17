package rr.control;

import java.util.ArrayList;
import java.util.List;

import rr.domain.ArrivalSchedule;
import rr.domain.Customer;
import rr.domain.GameConfig;
import rr.domain.GameResult;
import rr.domain.Menu;
import rr.domain.Money;
import rr.domain.Order;
import rr.domain.OrderStatus;
import rr.domain.Restaurant;
import rr.domain.Table;

/**
 * Runs the turn loop. It holds the turn counter and applies the six steps in the required order.
 * It contains no console code: the UI only calls these methods and reads the results.
 *
 * Step 1 (arrivals)            -> startTurn()
 * Step 2+3 (choices, kitchen)  -> waiterXxx() then chefXxx(): each choice is validated and applied
 *                                 immediately, Waiter always before Chef, which gives the same
 *                                 result as batching them (see report section 3.3).
 * Steps 4-6 (eat/pay, abandon, result) -> finishTurn()
 */
public class GameController {
    private final GameConfig config;
    private final ArrivalSchedule schedule;
    private final Restaurant restaurant;
    private final List<String> log = new ArrayList<>();
    private int turn = 0;
    private int nextCustomerId = 1;
    private Customer arrivedThisTurn;
    private GameResult result;

    public GameController(GameConfig config, ArrivalSchedule schedule) {
        this.config = config;
        this.schedule = schedule;
        this.restaurant = new Restaurant(config, Menu.standard());
    }

    // ------------------------------------------------------------ STEP 1
    public void startTurn() {
        if (isGameOver()) throw new IllegalStateException("The game is over.");
        turn++;
        restaurant.startTurn();
        arrivedThisTurn = null;
        if (schedule.hasArrival(turn)) {
            Customer c = schedule.createCustomer(turn, nextCustomerId++);
            restaurant.admit(c);
            arrivedThisTurn = c;
            log("Arrival: C" + c.getId() + " (" + c.getTypeName() + ") joins the queue.");
        }
    }

    // ------------------------------------------------------------ STEP 2 + 3
    public void waiterSeat(int customerId) {
        Customer c = restaurant.findCustomer(customerId);
        Table t = restaurant.getWaiter().seat(c, restaurant);
        log("Waiter seated C" + c.getId() + " (" + c.getTypeName() + ") at T" + t.getId() + ".");
    }

    public void waiterTakeOrder(int customerId) {
        Customer c = restaurant.findCustomer(customerId);
        Order o = restaurant.getWaiter().takeOrder(c, restaurant);
        log("Waiter took C" + c.getId() + "'s order. C" + c.getId() + " chose " + o.getItem().getName()
                + " (order #" + o.getId() + ", bill " + Money.format(o.getBillCents()) + ", "
                + o.getItem().getPrepUnits() + " prep unit(s)).");
    }

    public void waiterServe(int orderId) {
        Order o = restaurant.findOrder(orderId);
        restaurant.getWaiter().serve(o, turn, restaurant.getScoreboard());
        log("Waiter served order #" + o.getId() + " (" + o.getItem().getName() + ") to C" + o.getCustomer().getId() + ".");
    }

    public void waiterWait() {
        restaurant.getWaiter().rest();
        log("Waiter waits.");
    }

    public void chefCook(int orderId) {
        Order o = restaurant.findOrder(orderId);
        restaurant.getChef().cook(o, turn);
        String msg = "Chef worked on order #" + o.getId() + " (" + o.getItem().getName() + ") "
                + o.getUnitsDone() + "/" + o.getItem().getPrepUnits() + ".";
        if (o.getStatus() == OrderStatus.READY) msg += " It is READY (can be served from next turn).";
        log(msg);
    }

    public void chefWait() {
        restaurant.getChef().rest();
        log("Chef waits.");
    }

    // ------------------------------------------------------------ STEPS 4, 5, 6
    public void finishTurn() {
        // Step 4a: Cashier takes the oldest customer who became READY_TO_PAY on an earlier turn.
        Customer toPay = restaurant.nextCustomerToPay(turn);
        if (toPay != null) {
            int bill = toPay.getOrder().getBillCents();
            int recorded = restaurant.getCashier().collect(toPay, restaurant.getScoreboard(), turn);
            log("Cashier collected " + Money.format(bill) + " from C" + toPay.getId()
                    + " (recorded satisfaction " + recorded + "). Table freed.");
        }
        // Step 4b: customers served on an earlier turn finish eating.
        for (Customer c : restaurant.getCustomers()) {
            if (c.hasFinishedEating(turn)) {
                c.markReadyToPay(turn);
                log("C" + c.getId() + " finished eating and is READY_TO_PAY.");
            }
        }
        // Step 5: unserved customers lose satisfaction; at 0 they leave.
        for (Customer c : new ArrayList<>(restaurant.getCustomers())) {
            if (!c.isUnserved()) continue;
            c.loseSatisfaction();
            if (c.getSatisfaction() <= 0) {
                restaurant.customerLeaves(c);
                log("C" + c.getId() + " (" + c.getTypeName() + ") ran out of patience and LEFT. Order cancelled, table freed.");
            }
        }
        // Step 6: result after the last turn.
        if (turn >= config.getMaxTurns()) {
            result = restaurant.getScoreboard().evaluate(config);
        }
    }

    // ------------------------------------------------------------ queries for the UI
    public boolean isGameOver() { return result != null; }
    public GameResult getResult() { return result; }
    public int getTurn() { return turn; }
    public GameConfig getConfig() { return config; }
    public Restaurant getRestaurant() { return restaurant; }
    public Customer getArrivedThisTurn() { return arrivedThisTurn; }

    public List<String> drainLog() {
        List<String> copy = new ArrayList<>(log);
        log.clear();
        return copy;
    }

    protected void log(String message) { log.add(message); }
}
