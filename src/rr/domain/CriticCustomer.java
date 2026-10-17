package rr.domain;

/**
 * Critic: loses 12 satisfaction per unserved turn. Special behaviour (our own design):
 *  - ORDER PREFERENCE: always orders the most demanding dish on the menu (most prep units), i.e. Pasta.
 *  - REACTION TO SERVICE: the satisfaction recorded when they pay is a published review.
 *    Satisfied (>= 60) -> +10 (max 100). Unsatisfied (< 60) -> -10 (min 0).
 */
public class CriticCustomer extends Customer {
    public CriticCustomer(int id, int arrivalNumber, int arrivalTurn) {
        super(id, arrivalNumber, arrivalTurn);
    }

    @Override public String getTypeName() { return "Critic"; }

    @Override public int getWaitLoss() { return 12; }

    @Override public MenuItem chooseItem(Menu menu) { return menu.mostDemandingItem(); }

    @Override public int reviewedSatisfaction() {
        int s = getSatisfaction();
        return s >= 60 ? Math.min(MAX_SATISFACTION, s + 10) : Math.max(0, s - 10);
    }
}
