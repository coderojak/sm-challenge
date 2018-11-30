package sm.challenge.email.util;

public final class Exceptions {

    public static Throwable getRootCause(Throwable t) {
        Throwable cause = null;
        Throwable result = t;

        int maxIteration = 5000;
        while ((cause = result.getCause()) != null && result != cause && maxIteration > 0) {
            result = cause;
            maxIteration--;
        }

        return result;
    }

}
