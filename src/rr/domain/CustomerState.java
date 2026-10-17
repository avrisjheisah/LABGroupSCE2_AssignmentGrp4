package rr.domain;

/** Where a customer is in their visit. */
public enum CustomerState {
    WAITING_FOR_TABLE, SEATED, ORDERED, SERVED, READY_TO_PAY, PAID, LEFT
}
