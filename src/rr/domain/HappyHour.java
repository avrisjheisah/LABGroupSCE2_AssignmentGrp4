package rr.domain;


public class HappyHour {
    public static final int DURATION_TURNS = 2;
    public static final int PRICE_PERCENT = 80;
    public static final int SATISFACTION_BOOST = 15;

    private int startTurn = -1;

    public boolean isAvailable() { return startTurn < 0; }

    public void activate(int turn) {
        if (!isAvailable()) throw new InvalidActionException("Happy Hour can only be used once per game, and it has already been used.");
        startTurn = turn;
    }

    public boolean isActive(int turn) {
        return startTurn >= 0 && turn >= startTurn && turn < startTurn + DURATION_TURNS;
    }

    public int turnsLeft(int turn) { return isActive(turn) ? startTurn + DURATION_TURNS - turn : 0; }

    public int applyDiscount(int cents) { return Money.percentOf(cents, PRICE_PERCENT); }
}
