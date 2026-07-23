package clubmanagement.exception;

public class DataAccessException
        extends ClubManagementException {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}