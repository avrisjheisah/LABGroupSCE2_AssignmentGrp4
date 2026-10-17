package rr.domain;

/** Thrown when the manager asks for an action the rules do not allow. The message is shown to the player. */
public class InvalidActionException extends RuntimeException {
    public InvalidActionException(String message) {
        super(message);
    }
}
