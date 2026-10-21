package rr.domain;

public class VIPCustomer extends Customer {
    public VIPCustomer(int id, int arrivalNumber, int arrivalTurn) {
        super(id, arrivalNumber, arrivalTurn);
    }

    @Override public String getTypeName() { return "VIP"; }

    @Override public int getWaitLoss() { return 5; }

    @Override public int discountedPrice(int baseCents) { return Money.percentOf(baseCents, 90); }
}
