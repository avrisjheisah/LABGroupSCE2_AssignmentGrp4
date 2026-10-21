package rr.domain;

public class Chef extends Staff {
    public Chef() { super("Chef"); }

    public void cook(Order o, int turn) {
        ensureCanAct();
        o.advance(turn);
        markActed();
    }
}
