package rr.domain;

public class Table {
    private final int id;
    private Customer occupant;

    public Table(int id) { this.id = id; }

    public int getId() { return id; }
    public Customer getOccupant() { return occupant; }
    public boolean isFree() { return occupant == null; }

    public void occupy(Customer c) {
        if (!isFree()) throw new InvalidActionException("Table T" + id + " is already occupied.");
        occupant = c;
    }

    public void release() { occupant = null; }
}
