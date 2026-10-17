package rr.domain;

/** The Chef has one prep unit per turn and spends it on one order. */
public class Chef extends Staff {
    public Chef() { super("Chef"); }

    public void cook(Order o, int turn) {
        ensureCanAct();
        o.advance(turn);
        markActed();
    }
}
