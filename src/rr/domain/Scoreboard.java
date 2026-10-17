package rr.domain;

/** Keeps the numbers that decide victory or defeat. */
public class Scoreboard {
    private int revenueCents;
    private int paidCount;
    private int satisfactionTotal;
    private int servedCount;
    private int unhappyDepartures;

    public void recordServed() { servedCount++; }

    public void recordPayment(int billCents, int recordedSatisfaction) {
        revenueCents += billCents;
        paidCount++;
        satisfactionTotal += recordedSatisfaction;
    }

    public void recordDeparture() { unhappyDepartures++; }

    public int getRevenueCents() { return revenueCents; }
    public int getPaidCount() { return paidCount; }
    public int getServedCount() { return servedCount; }
    public int getUnhappyDepartures() { return unhappyDepartures; }

    public double getAverageSatisfaction() {
        return paidCount == 0 ? 0.0 : (double) satisfactionTotal / paidCount;
    }

    public GameResult evaluate(GameConfig config) {
        boolean enoughPaid = paidCount >= config.getMinPaidCustomers();
        boolean enoughRevenue = revenueCents >= config.getMinRevenueCents();
        boolean happyEnough = paidCount > 0 && satisfactionTotal >= config.getMinAverageSatisfaction() * paidCount;
        return (enoughPaid && enoughRevenue && happyEnough) ? GameResult.VICTORY : GameResult.DEFEAT;
    }
}
