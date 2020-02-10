package it.agevoluzione.tools.android.utils.reminder;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

public class ReminderFactory {

    @NonNull
    public static Remind generate(@NonNull final Context context, @NonNull final Class<? extends BroadcastReceiver> receiver,
        @NonNull final Long reminderId, final String packageName, final String action) {

        final Intent intent = new Intent(context, receiver);
        if ((null != packageName) && (!packageName.isEmpty())) {
            intent.setPackage(packageName);
        }
        if ((null != action) && (!action.isEmpty())) {
            intent.setAction(action);
        }
        intent.putExtra(IRemind.EXTRA,(long) reminderId);

        return new Remind() {

            @NonNull
            @Override
            Context getContext() {
                return context;
            }

            @NonNull
            @Override
            public PendingIntent getPendingIntent() {
                return PendingIntent.getBroadcast(
                        context,
                        reminderId.intValue(),
                        intent,
                        0
                );
            }
        };
    }

    @NonNull
    public static Remind generate(@NonNull final Context context, @NonNull final Class<? extends BroadcastReceiver> receiver,
                                  @NonNull final Long reminderId) {
        return generate(context,receiver,reminderId,null,null);
    }

}