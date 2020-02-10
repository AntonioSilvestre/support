package it.agevoluzione.tools.android.utils.notification;

import android.text.format.DateFormat;

import it.agevoluzione.tools.android.exceptions.NotificationException;
import it.agevoluzione.tools.android.model.NotificationModel;
import it.agevoluzione.tools.android.model.NotificationStatus;

public class NotificationModelUtils {

    public static void checkNotify(NotificationModel notify) throws NotificationException {
        if (null == notify) {
            throw new NotificationException("NotificationModel null");
        }

        if (null == notify.pk) {
            throw new NotificationException("NotificationModel without Pk");
        }

        if (null == notify.type) {
            throw new NotificationException("NotificationModel without Type");
        }
    }

    /**
     * @param notify
     * @param format Example "HH:mm:ss"
     * @return
     */
    public static CharSequence readTime(NotificationModel notify, String format) {
        if ((null == notify) || (null == notify.starting)) {
            return "Not Set";
        }
        return DateFormat.format(format, notify.starting);
    }

    public static CharSequence readTimeStd(NotificationModel notify) {
        return readTime(notify, "yyyy/MM/dd - HH:mm:ss");
    }

    public static String readStatus(NotificationModel notify) {
        if (null == notify.status) {
            return "NOT_SET";
        }
        switch (notify.status) {
            case NotificationStatus.SCHEDULED:
                return "SCHEDULED";
            case NotificationStatus.DISABLED:
                return "DISABLED";
            case NotificationStatus.SHOWED:
                return "SHOWED";
            case NotificationStatus.EXPIRED:
                return "EXPIRED";
            case NotificationStatus.SKIPPED:
                return "SKIPPED";
            case NotificationStatus.LOST:
                return "LOST";
            case NotificationStatus.CATCH:
                return "CATCH";
            default:
                return "UNKNOW";
        }
    }

}
