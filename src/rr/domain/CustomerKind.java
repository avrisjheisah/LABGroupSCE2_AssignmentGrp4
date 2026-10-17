package rr.domain;

/** Factory for the kinds of customer. Each constant knows how to build its own customer. */
public enum CustomerKind {
    REGULAR {
        @Override public Customer create(int id, int arrivalNumber, int turn) {
            return new RegularCustomer(id, arrivalNumber, turn);
        }
    },
    VIP {
        @Override public Customer create(int id, int arrivalNumber, int turn) {
            return new VIPCustomer(id, arrivalNumber, turn);
        }
    },
    CRITIC {
        @Override public Customer create(int id, int arrivalNumber, int turn) {
            return new CriticCustomer(id, arrivalNumber, turn);
        }
    };

    public abstract Customer create(int id, int arrivalNumber, int turn);
}
