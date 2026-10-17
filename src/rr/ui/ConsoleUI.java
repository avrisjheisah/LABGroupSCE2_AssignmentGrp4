package rr.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import rr.control.GameController;
import rr.domain.Customer;
import rr.domain.GameResult;
import rr.domain.InvalidActionException;
import rr.domain.Money;
import rr.domain.Order;
import rr.domain.Restaurant;
import rr.domain.Scoreboard;
import rr.domain.Table;

/** Command-line boundary: prints state, reads commands, shows rule errors. Contains no game rules. */
public class ConsoleUI {
    /** Handles one command line for one staff member. Throws InvalidActionException if not allowed. */
    private interface Handler {
        void handle(String verb, Integer arg);
    }

    private final GameController game;
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private final boolean echoInput = System.console() == null;

    public ConsoleUI(GameController game) { this.game = game; }

    public void run() {
        printIntro();
        while (!game.isGameOver()) {
            game.startTurn();
            printTurnState();
            game.drainLog();   // the arrival is already shown in the state block above
            if (!askWaiter()) { System.out.println("Game ended early."); return; }
            if (!askChef()) { System.out.println("Game ended early."); return; }
            game.finishTurn();
            System.out.println("-- End of turn " + game.getTurn() + " --");
            printLog();
            printScoreLine();
        }
        printResult();
    }

    // ------------------------------------------------------------ prompts
    private boolean askWaiter() {
        System.out.println("Waiter commands: seat <C#> | order <C#> | serve <order#> | wait | help | quit");
        return prompt("Waiter", (verb, arg) -> {
            switch (verb) {
                case "seat": game.waiterSeat(need(arg, "seat <customer number>")); break;
                case "order": game.waiterTakeOrder(need(arg, "order <customer number>")); break;
                case "serve": game.waiterServe(need(arg, "serve <order number>")); break;
                case "wait": game.waiterWait(); break;
                default: throw new InvalidActionException("Unknown Waiter command '" + verb + "'. Try: seat, order, serve, wait.");
            }
        });
    }

    private boolean askChef() {
        System.out.println("Chef commands: cook <order#> | wait | help | quit");
        return prompt("Chef", (verb, arg) -> {
            switch (verb) {
                case "cook": game.chefCook(need(arg, "cook <order number>")); break;
                case "wait": game.chefWait(); break;
                default: throw new InvalidActionException("Unknown Chef command '" + verb + "'. Try: cook, wait.");
            }
        });
    }

    /** Repeats until a valid command succeeds. Returns false if the player quits or input ends. */
    private boolean prompt(String label, Handler handler) {
        while (true) {
            System.out.print(label + "> ");
            String line;
            try {
                line = in.readLine();
            } catch (IOException e) {
                line = null;
            }
            if (line == null) { System.out.println("(input ended)"); return false; }
            if (echoInput) System.out.println(line);
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");
            String verb = parts[0].toLowerCase();
            if (verb.equals("quit")) return false;
            if (verb.equals("help")) { printHelp(); continue; }
            try {
                Integer arg = parts.length > 1 ? parseNumber(parts[1]) : null;
                handler.handle(verb, arg);
                printLog();
                return true;
            } catch (InvalidActionException e) {
                System.out.println("  [Not allowed] " + e.getMessage() + " Choose again.");
            }
        }
    }

    private static Integer parseNumber(String token) {
        String digits = token.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) throw new InvalidActionException("'" + token + "' is not a number.");
        return Integer.valueOf(digits);
    }

    private static int need(Integer arg, String usage) {
        if (arg == null) throw new InvalidActionException("Missing number. Usage: " + usage + ".");
        return arg;
    }

    // ------------------------------------------------------------ output
    private void printIntro() {
        System.out.println("=====================================================");
        System.out.println("  RESTAURANT RUSH  -  survive " + game.getConfig().getMaxTurns() + " turns");
        System.out.println("  Win: >= " + game.getConfig().getMinPaidCustomers() + " paid customers, revenue >= "
                + Money.format(game.getConfig().getMinRevenueCents()) + ", average satisfaction >= "
                + game.getConfig().getMinAverageSatisfaction());
        System.out.println("=====================================================");
    }

    private void printTurnState() {
        Restaurant r = game.getRestaurant();
        System.out.println();
        System.out.println("=============== TURN " + game.getTurn() + " of " + game.getConfig().getMaxTurns() + " ===============");
        printScoreLine();
        Customer arrival = game.getArrivedThisTurn();
        System.out.println("Arrival this turn: " + (arrival == null ? "none" : describe(arrival)));
        System.out.println("Queue: " + (r.getQueue().isEmpty() ? "(empty)" : joinCustomers(r.getQueue())));
        System.out.println("Tables:");
        for (Table t : r.getTables()) {
            System.out.println("  T" + t.getId() + ": " + (t.isFree() ? "free" : describe(t.getOccupant())));
        }
        System.out.println("Orders:");
        boolean any = false;
        for (Order o : r.getOrders()) {
            if (o.getStatus() == rr.domain.OrderStatus.PAID || o.getStatus() == rr.domain.OrderStatus.CANCELLED) continue;
            any = true;
            System.out.println("  #" + o.getId() + " C" + o.getCustomer().getId() + " " + o.getItem().getName()
                    + "  " + o.getStatus() + " (" + o.getUnitsDone() + "/" + o.getItem().getPrepUnits() + ")  bill "
                    + Money.format(o.getBillCents()));
        }
        if (!any) System.out.println("  (none)");
        System.out.println("Available now:");
        System.out.println("  Waiter -> " + waiterOptions());
        System.out.println("  Chef   -> " + chefOptions());
    }

    private String waiterOptions() {
        Restaurant r = game.getRestaurant();
        StringBuilder sb = new StringBuilder();
        if (!r.getQueue().isEmpty() && r.hasFreeTable()) {
            sb.append("seat ").append(idList(r.getQueue(), "C")).append("; ");
        }
        List<Customer> needOrder = r.seatedAwaitingOrder();
        if (!needOrder.isEmpty()) sb.append("order ").append(idList(needOrder, "C")).append("; ");
        List<Order> servable = r.servableOrders(game.getTurn());
        if (!servable.isEmpty()) {
            sb.append("serve");
            for (Order o : servable) sb.append(" #").append(o.getId());
            sb.append("; ");
        }
        sb.append("wait");
        return sb.toString();
    }

    private String chefOptions() {
        List<Order> cookable = game.getRestaurant().cookableOrders();
        StringBuilder sb = new StringBuilder();
        if (!cookable.isEmpty()) {
            sb.append("cook");
            for (Order o : cookable) sb.append(" #").append(o.getId());
            sb.append("; ");
        }
        sb.append("wait");
        return sb.toString();
    }

    private static String idList(List<Customer> list, String prefix) {
        StringBuilder sb = new StringBuilder();
        for (Customer c : list) sb.append(prefix).append(c.getId()).append(' ');
        return sb.toString().trim();
    }

    private static String joinCustomers(List<Customer> list) {
        StringBuilder sb = new StringBuilder();
        for (Customer c : list) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(describe(c));
        }
        return sb.toString();
    }

    private static String describe(Customer c) {
        return "C" + c.getId() + " " + c.getTypeName() + " [sat " + c.getSatisfaction() + ", " + c.getState() + "]";
    }

    private void printScoreLine() {
        Scoreboard s = game.getRestaurant().getScoreboard();
        String avg = s.getPaidCount() == 0 ? "n/a" : String.format("%.1f", s.getAverageSatisfaction());
        System.out.println("Revenue " + Money.format(s.getRevenueCents()) + " | Paid " + s.getPaidCount()
                + " | Served " + s.getServedCount() + " | Left unhappy " + s.getUnhappyDepartures()
                + " | Avg satisfaction (paid) " + avg);
    }

    private void printLog() {
        for (String line : game.drainLog()) System.out.println("  > " + line);
    }

    private void printHelp() {
        System.out.println("  seat <C#>    Waiter: seat a queued customer at the lowest free table");
        System.out.println("  order <C#>   Waiter: take the order of a seated customer (they choose their dish)");
        System.out.println("  serve <#>    Waiter: serve an order that was READY at the start of the turn");
        System.out.println("  cook <#>     Chef: spend this turn's prep unit on an order");
        System.out.println("  wait         use no task this turn");
        System.out.println("  quit         end the game");
    }

    private void printResult() {
        Scoreboard s = game.getRestaurant().getScoreboard();
        System.out.println();
        System.out.println("================= FINAL RESULT =================");
        printScoreLine();
        System.out.println("Targets: paid >= " + game.getConfig().getMinPaidCustomers()
                + ", revenue >= " + Money.format(game.getConfig().getMinRevenueCents())
                + ", avg satisfaction >= " + game.getConfig().getMinAverageSatisfaction());
        System.out.println(game.getResult() == GameResult.VICTORY ? "*** VICTORY ***" : "*** DEFEAT ***");
    }
}
