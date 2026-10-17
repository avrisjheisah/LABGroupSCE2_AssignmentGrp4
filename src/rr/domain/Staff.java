package rr.domain;

/** A staff member who can perform exactly one task per turn. */
public abstract class Staff {
    private final String name;
    private boolean acted;

    protected Staff(String name) { this.name = name; }

    public String getName() { return name; }
    public boolean hasActed() { return acted; }
    public void startTurn() { acted = false; }

    /** Choosing to wait also uses up this turn's task. */
    public void rest() {
        ensureCanAct();
        markActed();
    }

    protected void ensureCanAct() {
        if (acted) throw new InvalidActionException("The " + name + " has already used this turn's task.");
    }

    protected void markActed() { acted = true; }
}
