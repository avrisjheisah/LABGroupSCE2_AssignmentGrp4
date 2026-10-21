package rr.domain;


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
