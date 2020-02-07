package it.agevoluzione.tools.android.utils.receiver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.agevoluzione.tools.android.AlarmCommander;
import it.agevoluzione.tools.android.model.NotificationModel;
import it.agevoluzione.tools.android.model.NotificationSettings;
import it.agevoluzione.tools.android.room.RepositoryNotify;
import it.agevoluzione.tools.android.utils.AndroidUtils;
import it.agevoluzione.tools.android.utils.ArrayUtils;
import it.agevoluzione.tools.android.utils.media.ServiceMediaPlayer;
import it.agevoluzione.tools.android.utils.reminder.IRemind;
import it.agevoluzione.tools.android.utils.reminder.Remind;
import it.agevoluzione.tools.android.utils.settings.NotificationSettingsUtils;

public abstract class ReceiverHelper extends BroadcastReceiver {


//    public AlarmCommander(@NonNull Context context, @NonNull Class<? extends BroadcastReceiver> receiver, @NonNull Class<? extends ServiceMediaPlayer> mediaService) {

    private Class<? extends ServiceMediaPlayer> mediaService;

    private AlarmCommander alarmCmd;

    public ReceiverHelper(Class<? extends ServiceMediaPlayer> mediaService) {
        this.mediaService = mediaService;
    }

    protected abstract boolean debug();

    protected abstract void receiveReminder(NotificationModel notificationModel, AlarmCommander alarmCmd);

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (null == alarmCmd) {
            alarmCmd = new AlarmCommander(context, getClass(), mediaService);
        }

        String action = intent.getAction();

        if (null == action) {
//            Toast.makeText(context, "No action received!", Toast.LENGTH_SHORT).show();
            Log.w("TestReceiver", "No action received!");
            return;
        }


        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            NotificationModel rebootNotify = new NotificationModel();
            // todo verifcare se è il caso di utilizzare un valore diverso da 0 per notifiche che non possono essere previste come il riavvio del dispositivo
            rebootNotify.type = 0;
            receiveReminder(rebootNotify, alarmCmd);
//            manageReboot(alarmCmd, null);
        } else if (action.contains(Remind.ACTION)) {
            long pk = intent.getLongExtra(IRemind.EXTRA, 0);
            alarmCmd.get(new RepositoryNotify.Listener() {
                @Override
                public void onResult(NotificationModel[] notifications) {
                    if ((null != notifications) && (null != notifications[0]) ) {
                        receiveReminder(notifications[0], alarmCmd);
                    }
                }
                @Override
                public void onError(RepositoryNotify.CrudOperation op, Throwable t) {
                    Log.e("ReceiverHelper", "Received err", t);
                }
            }, new NotificationModel(pk));
        }

        if (debug()) {
            logIntent(intent);
        }
    }

    public static void logIntent(Intent intent) {
        StringBuilder sb = new StringBuilder("### ");
        sb.append("URI: ").append(intent.toUri(Intent.URI_INTENT_SCHEME)).append("\n");
        sb.append("PackageName: ").append(intent.getPackage()).append("\n");
        sb.append("Flag: ").append(intent.getFlags()).append("\n");
        sb.append("Action: ").append(intent.getAction()).append("\n");
        if (intent.hasExtra(Remind.EXTRA)) {
            long pk = intent.getLongExtra(Remind.EXTRA,-1);
            sb.append("extra: ").append(pk).append(" pk\n");
        } else {
            sb.append("extra: Null").append("\n");
        }
        sb.append("data: ").append(intent.getDataString()).append("\n");

        String log = sb.toString();

        Log.d("## RECEIVED", log);
    }

    /**
     * Show Notification by ID
     * @param mgr
     * @param pk
     */
//    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    @SuppressLint("MissingPermission")
    public static void manageReminder(@NonNull AlarmCommander mgr, long pk) {
        NotificationSettings settings = NotificationSettingsUtils.get(mgr.getContext());
        if (settings.enable) {
//            if (settings.showInWake && !AndroidUtils.isOn(context)) {
//            if (!AndroidUtils.isOn(context)) {
            if (AndroidUtils.checkPermission(mgr.getContext(), Manifest.permission.WAKE_LOCK) && !AndroidUtils.isOn(mgr.getContext())) {
                AndroidUtils.acquireWakeLock(mgr.getContext(), 1000L);
                Log.d("RECEIVER","Wake acquire");
            }
            if (pk != 0) {
                mgr.show(pk);
            } else {
                Log.w("TestReceiver", "Pk not Set!");
            }
        } else {
//            Toast.makeText(context, "Setting disabled!", Toast.LENGTH_SHORT).show();
            Log.w("TestReceiver", "Setting disabled!");
        }
    }

    /**
     * Update notification stats and restore them;
     * @param mgr
     * @param listener
     */
    public static void manageReboot(@NonNull AlarmCommander mgr, @Nullable RepositoryNotify.Listener listener) {
        mgr.updateStats();
        if (null == listener) {
            listener = new RepositoryNotify.Listener() {
                @Override
                public void onResult(NotificationModel[] notifications) {
                    if (null != notifications) {
                        Log.d("ReceiverHelper", "restored:"+ ArrayUtils.arrayToString(notifications));
                    } else {
                        Log.w("ReceiverHelper", "Restore Err:\nNothing to restore!");
                    }
                }
                @Override
                public void onError(RepositoryNotify.CrudOperation op, Throwable t) {
                    Log.w("ReceiverHelper", "Restore Err:\n"+t.getMessage());
                }
            };
        }
        mgr.restore(listener);
    }
}
