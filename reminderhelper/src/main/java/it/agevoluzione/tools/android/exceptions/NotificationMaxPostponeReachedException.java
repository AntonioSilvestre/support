package it.agevoluzione.tools.android.exceptions;

public class NotificationMaxPostponeReachedException extends NotificationException {
    public NotificationMaxPostponeReachedException() {
    }

    public NotificationMaxPostponeReachedException(String message) {
        super(message);
    }

    public NotificationMaxPostponeReachedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationMaxPostponeReachedException(Throwable cause) {
        super(cause);
    }

    public NotificationMaxPostponeReachedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
