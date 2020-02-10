package it.agevoluzione.tools.android.utils.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import it.agevoluzione.tools.android.model.NotificationModel;
import it.agevoluzione.tools.android.utils.notification.NotificationModelUtils;

/**
 * Utility per impostare reminder di sistema utilizzando la classe NotificationModel
 */
public class ReminderUtils {

    public static void setReminder(@NonNull Context context
            , @NonNull Class<? extends BroadcastReceiver> receiverClass
            , @NonNull NotificationModel notify) {
        Long time = notify.starting;
        if (null != time) {
            try {
                getReminderByNotify(context, receiverClass, notify).at(time);
            } catch (Exception e) {
                Log.e("ReminderUtils", "Err: ", e);
            }
        } else {
            Log.e("ReminderUtils", "alarm not set because no time has been set!");
        }
    }


    public static void cancelReminder(@NonNull Context context
            , @NonNull Class<? extends BroadcastReceiver> receiverClass
            , @NonNull NotificationModel notify) {
        try {
            getReminderByNotify(context, receiverClass, notify).cancel();
        } catch (Exception e) {
            Log.e("ReminderUtils", "Err: ", e);
        }
    }

    private static Remind getReminderByNotify(@NonNull Context context
            , @NonNull Class<? extends BroadcastReceiver> receiverClass
            , @NonNull NotificationModel notify) throws Exception {
        NotificationModelUtils.checkNotify(notify);
        String action = Remind.ACTION + "-" + notify.type;
//        return ReminderFactory.generate(context, receiverClass, notify.pk, BuildConfig.APPLICATION_ID, action);
        return ReminderFactory.generate(context, receiverClass, notify.pk, context.getPackageName(), action);
    }




}
