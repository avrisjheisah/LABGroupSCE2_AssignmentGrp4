package rr.control;

import java.util.ArrayList;
import java.util.List;

import rr.domain.ArrivalSchedule;
import rr.domain.Customer;
import rr.domain.GameConfig;
import rr.domain.InvalidActionException;
import rr.domain.GameResult;
import rr.domain.HappyHour;
import rr.domain.Menu;
import rr.domain.Money;
import rr.domain.Order;
import rr.domain.OrderStatus;
import rr.domain.Restaurant;
import rr.domain.Table;


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

    public void startTurn() {
        if (isGameOver()) throw new IllegalStateException("The game is over.");
        turn++;
        restaurant.startTurn(turn);
        arrivedThisTurn = null;
        if (restaurant.getHappyHour().isActive(turn)) {
            int n = restaurant.applyHappyHourBoost();
            log("Happy Hour continues: " + n + " unserved customer(s) recover " + HappyHour.SATISFACTION_BOOST + " satisfaction.");
        }
        if (schedule.hasArrival(turn)) {
            Customer c = schedule.createCustomer(turn, nextCustomerId++);
            restaurant.admit(c);
            arrivedThisTurn = c;
        }
    }


    public void activateHappyHour() {
        if (restaurant.anyStaffHasActed()) {
            throw new InvalidActionException("Happy Hour must be started at the start of a turn, before any staff member has acted.");
        }
        restaurant.getHappyHour().activate(turn);
        int n = restaurant.applyHappyHourBoost();
        log("HAPPY HOUR started (this turn and next): orders taken cost 80% of their price; "
                + n + " unserved customer(s) recover " + HappyHour.SATISFACTION_BOOST + " satisfaction.");
    }

    public void hostSeat(int customerId) {
        Customer c = restaurant.findCustomer(customerId);
        Table t = restaurant.getHost().seat(c, restaurant);
        log("Host seated C" + c.getId() + " (" + c.getTypeName() + ") at T" + t.getId() + ".");
    }

    public void hostWait() {
        restaurant.getHost().rest();
        log("Host waits.");
    }

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

    public void finishTurn() {
        Customer toPay = restaurant.nextCustomerToPay(turn);
        if (toPay != null) {
            int bill = toPay.getOrder().getBillCents();
            int recorded = restaurant.getCashier().collect(toPay, restaurant.getScoreboard(), turn);
            log("Cashier collected " + Money.format(bill) + " from C" + toPay.getId()
                    + " (recorded satisfaction " + recorded + "). Table freed.");
        }
        for (Customer c : restaurant.getCustomers()) {
            if (c.hasFinishedEating(turn)) {
                c.markReadyToPay(turn);
                log("C" + c.getId() + " finished eating and is READY_TO_PAY.");
            }
        }
        for (Customer c : new ArrayList<>(restaurant.getCustomers())) {
            if (!c.isUnserved()) continue;
            c.loseSatisfaction();
            if (c.getSatisfaction() <= 0) {
                restaurant.customerLeaves(c);
                log("C" + c.getId() + " (" + c.getTypeName() + ") ran out of patience and LEFT. Order cancelled, table freed.");
            }
        }
        if (turn >= config.getMaxTurns()) {
            result = restaurant.getScoreboard().evaluate(config);
        }
    }

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
