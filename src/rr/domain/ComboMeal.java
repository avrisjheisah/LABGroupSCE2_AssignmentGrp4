package rr.domain;

/** Two menu items sold together at 90% of their combined price; prep units are added together. */
public class ComboMeal extends MenuItem {
    private final MenuItem first;
    private final MenuItem second;

    public ComboMeal(MenuItem first, MenuItem second) {
        super(first.getName() + " + " + second.getName(),
                Money.percentOf(first.getPriceCents() + second.getPriceCents(), 90),
                first.getPrepUnits() + second.getPrepUnits());
        this.first = first;
        this.second = second;
    }

    public MenuItem getFirst() { return first; }
    public MenuItem getSecond() { return second; }
}
