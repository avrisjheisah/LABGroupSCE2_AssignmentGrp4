package rr.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** The restaurant menu: an ordered list of items. */
public class Menu {
    private final List<MenuItem> items = new ArrayList<>();

    public Menu(List<MenuItem> items) {
        this.items.addAll(items);
    }

    public static Menu standard() {
        List<MenuItem> list = new ArrayList<>();
        list.add(new MenuItem("Salad", 900, 1));
        list.add(new MenuItem("Burger", 1500, 1));
        list.add(new MenuItem("Pasta", 1800, 2));
        return new Menu(list);
    }

    public List<MenuItem> getItems() { return Collections.unmodifiableList(items); }
}
