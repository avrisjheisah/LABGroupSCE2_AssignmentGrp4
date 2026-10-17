package rr.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** The restaurant menu: an ordered list of items. */
public class Menu {
    private final List<MenuItem> items = new ArrayList<>();
    private final List<MenuItem> combos = new ArrayList<>();

    public Menu(List<MenuItem> items, List<MenuItem> combos) {
        this.items.addAll(items);
        this.combos.addAll(combos);
    }

    public static Menu standard() {
        List<MenuItem> list = new ArrayList<>();
        list.add(new MenuItem("Salad", 900, 1));
        list.add(new MenuItem("Burger", 1500, 1));
        list.add(new MenuItem("Pasta", 1800, 2));
        List<MenuItem> combos = new ArrayList<>();
        combos.add(new ComboMeal(list.get(0), list.get(2)));   // Salad + Pasta
        combos.add(new ComboMeal(list.get(1), list.get(0)));   // Burger + Salad
        return new Menu(list, combos);
    }

    public List<MenuItem> getItems() { return Collections.unmodifiableList(items); }

    public List<MenuItem> getCombos() { return Collections.unmodifiableList(combos); }

    /** The item with the most prep units (ties: higher price). Used by the Critic. */
    public MenuItem mostDemandingItem() {
        MenuItem best = items.get(0);
        for (MenuItem m : items) {
            if (m.getPrepUnits() > best.getPrepUnits()
                    || (m.getPrepUnits() == best.getPrepUnits() && m.getPriceCents() > best.getPriceCents())) {
                best = m;
            }
        }
        return best;
    }
}
