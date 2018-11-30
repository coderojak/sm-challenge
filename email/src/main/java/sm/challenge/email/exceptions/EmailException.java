package sm.challenge.email.exceptions;

public class EmailException extends RuntimeException {

    private static final long serialVersionUID = -6629118795093249690L;

    public EmailException() {
        super();
    }

    public EmailException(String message) {
        super(message);
    }

    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailException(Throwable cause) {
        super(cause);
    }

}
