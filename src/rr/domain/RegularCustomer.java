package rr.domain;

/** The standard customer: loses 8 satisfaction per unserved turn, pays full price. */
public class RegularCustomer extends Customer {
    public RegularCustomer(int id, int arrivalNumber, int arrivalTurn) {
        super(id, arrivalNumber, arrivalTurn);
    }

    @Override public String getTypeName() { return "Regular"; }

    @Override public int getWaitLoss() { return 8; }
}
