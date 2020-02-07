package it.agevoluzione.tools.android.utils.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

public abstract class Remind implements IRemind {

    @Override
    public void at(long timeInMillis) {
        PendingIntent pendingIntent = getPendingIntent();
        setAlarm(pendingIntent,timeInMillis);
    }

    private void setAlarm(PendingIntent pendingIntent, long time) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr().setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmMgr().setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else {
            alarmMgr().set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }

    @Override
    public void cancel() {
        PendingIntent pendingIntent = getPendingIntent();
        alarmMgr().cancel(pendingIntent);
    }

    private AlarmManager alarmMgr() {
        return (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
    }

    @NonNull
    abstract PendingIntent getPendingIntent();

    @NonNull
    abstract Context getContext();

}
