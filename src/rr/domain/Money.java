package rr.domain;

/** Money helpers. All money is stored as integer cents to avoid floating-point errors. */
public final class Money {
    private Money() { }

    public static String format(int cents) {
        return String.format("$%d.%02d", cents / 100, cents % 100);
    }

    /** Returns pct% of cents, rounded half up (integer arithmetic only). */
    public static int percentOf(int cents, int pct) {
        return (cents * pct + 50) / 100;
    }
}
