package clubmanagement.exception;

public class ClubManagementException extends Exception {
    public ClubManagementException(String message) {
        super(message);
    }

    public ClubManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}