package rr.domain;

public class GameConfig {
    private final int maxTurns;
    private final int tableCount;
    private final int minPaidCustomers;
    private final int minRevenueCents;
    private final int minAverageSatisfaction;

    public GameConfig(int maxTurns, int tableCount, int minPaidCustomers, int minRevenueCents, int minAverageSatisfaction) {
        this.maxTurns = maxTurns;
        this.tableCount = tableCount;
        this.minPaidCustomers = minPaidCustomers;
        this.minRevenueCents = minRevenueCents;
        this.minAverageSatisfaction = minAverageSatisfaction;
    }

    public static GameConfig base() { return new GameConfig(18, 2, 4, 4500, 60); }

    public int getMaxTurns() { return maxTurns; }
    public int getTableCount() { return tableCount; }
    public int getMinPaidCustomers() { return minPaidCustomers; }
    public int getMinRevenueCents() { return minRevenueCents; }
    public int getMinAverageSatisfaction() { return minAverageSatisfaction; }
}
