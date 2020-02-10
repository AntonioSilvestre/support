package it.agevoluzione.tools.android.exceptions;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class DBExceptionCompleteRemoval extends DBException {
    public DBExceptionCompleteRemoval() {
    }

    public DBExceptionCompleteRemoval(String message) {
        super(message);
    }

    public DBExceptionCompleteRemoval(String message, Throwable cause) {
        super(message, cause);
    }

    public DBExceptionCompleteRemoval(Throwable cause) {
        super(cause);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public DBExceptionCompleteRemoval(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
