package it.agevoluzione.tools.android.utils.media;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import it.agevoluzione.tools.android.model.NotificationModel;
import it.agevoluzione.tools.android.model.NotificationSettings;
import it.agevoluzione.tools.android.model.NotificationStatus;
import it.agevoluzione.tools.android.room.RepositoryNotify;
import it.agevoluzione.tools.android.utils.AndroidUtils;
import it.agevoluzione.tools.android.utils.settings.NotificationSettingsUtils;

public abstract class ServiceMediaPlayer extends Service {

    public static final String ACTION_PLAY = "mp.act.play";
    public static final String ACTION_STOP = "mp.act.stop";
    public static final String ACTION_PAUSE = "mp.act.pause";
    public static final String ACTION_RESUME = "mp.act.resume";
//    public static final String ACTION_RELOAD = "mp.act.reload";
//    public static final String ACTION_CLOSE = "mp.act.close";


    public static final String EXTRA_ID = "mp.xtr.id";
//    public static final String EXTRA_VIBRATE = "mp.xtr.vbr";

    private MediaPlayer mediaPlayer;
//    private int streamMedia;

    public static long[] DEFAULT_VIBRATE_PATTERN = {100, 200, 300, 400, 500, 400, 300, 200, 400};

    //    public static final int STAT_UNDEFINED = -1;
    public static final int STAT_INIT = 0;
    public static final int STAT_PLAY = 1;
    public static final int STAT_PAUSE = 2;

    /*
            STATUS
     */

    private static class Status {
        static NotificationModel notify;
        static int stat;
        static int systemVolume;
    }

    private static int getStat() {
        synchronized (Status.class) {
            return Status.stat;
        }
    }

    private static void setStatus(int status) {
        synchronized (Status.class) {
            Status.stat = status;
        }
    }

    private static NotificationModel getNotify() {
        synchronized (Status.class) {
            return Status.notify;
        }
    }

    private static void setNotify(NotificationModel notify) {
        synchronized (Status.class) {
            Status.notify = notify;
        }
    }

    private static boolean isActiveOnId(long id) {
        return isActive() && null != getNotify() && id == getNotify().pk;
    }

    public static boolean isActive() {
        return (STAT_INIT != getStat());
//        return isPlaying() || isPaused();
    }

    private static int getVolumeSys() {
        synchronized (Status.class) {
            return Status.systemVolume;
        }
    }

    private static void setVolumeSys(int volume) {
        synchronized (Status.class) {
            Status.systemVolume = volume;
        }
    }

    public static boolean isPlaying() {
        return (STAT_PLAY == getStat());
    }

    public static boolean isPaused() {
        return (STAT_PAUSE == getStat());
    }

    private RepositoryNotify repo;

    public ServiceMediaPlayer() {
        repo = new RepositoryNotify(this);
    }

    /**
     * Produce Notification gestire la notifica
     *
     * @param notify se null serve per gestire le chiamate con errore verra chiamato poi doNothingAndClose()
     * @return
     */
    @NonNull
    protected abstract Notification produceAndroidNotify(@Nullable NotificationModel notify, NotificationSettings settings);

    /**
     * Vibrate Pattern to Play. If null return will use a default pattern
     *
     * @return
     */
    @Nullable
    protected long[] getVibratePattern() {
        return DEFAULT_VIBRATE_PATTERN;
    }

    protected abstract void startFullScreenActivity(@NonNull NotificationModel notificationModel, @NonNull NotificationSettings settings);

    protected NotificationSettings getSettings() {
        return NotificationSettingsUtils.get(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void doNothingAndClose() {
        Notification emptyNotification = produceAndroidNotify(null, null);
        startForeground(-1, emptyNotification);
        cleanupService();
        Log.d("ServiceMediaPlayer", "Close! service");
    }


    /*
    if (ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.WRITE_CALENDAR)
        != PackageManager.PERMISSION_GRANTED) {
     */

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
//        Log.d("ServiceMediaPlayer", "intent:" + intent + " flag:" + flags + " startId:" + startId);
        String action = intent.getAction();
        if ((null == action) || (action.isEmpty())) {
            Log.e("ServiceMediaPlayer", "No action Passed on onStartCommand!");
            doNothingAndClose();
            return START_NOT_STICKY;
        }

        NotificationSettings settings = getSettings();

        long id = intent.getLongExtra(EXTRA_ID, 0);

        switch (action) {
            case ACTION_PLAY:
                getNotifyFromDb(id, settings);
                break;
            case ACTION_STOP:
                stopMedia();
                break;
            case ACTION_RESUME:
                resumeMedia(getNotify(), settings);
                break;
            case ACTION_PAUSE:
                pauseMedia();
                break;
//            case ACTION_RELOAD:
//                pauseMedia();
//                resumeMedia(getNotify(), settings);
//                break;
//            case ACTION_CLOSE:
//                cleanupService();
//                break;
        }
        // service will not be recreated if abnormally terminated
        return START_NOT_STICKY;
    }

    private void getNotifyFromDb(long id, final NotificationSettings settings) {
        RepositoryNotify.CrudOperation opRead = new RepositoryNotify.ReadOperation() {
            @Nullable
            @Override
            public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
                if ((null != notifications) && (null != notifications[0])) {
                    notifications[0].status = NotificationStatus.SHOWED;
                    startMedia(notifications[0], settings);
                    return notifications;
                }
                cleanupService();
                return null;
            }
        };
        RepositoryNotify.CrudOperation opUpdate = new RepositoryNotify.UpdateOperation();
        repo.request(new NotificationModel(id), opRead, opUpdate);
    }

    private void startMedia(NotificationModel notificationModel, NotificationSettings settings) {
        if (!isActive()) {
            Notification notification = produceAndroidNotify(notificationModel, settings);
            startForeground(notificationModel.pk.intValue(), notification);
        }
        startFullScreenActivity(notificationModel, settings);

        setNotify(notificationModel);

        if (settings.enable) {
            setStatus(STAT_PLAY);

            if (settings.vibrate) {
                AndroidUtils.startVibration(this, pattern(), 0);
            }

            if (settings.sound) {
                initMediaPlayer();
                startSound(notificationModel.ringtone);

                if (settings.overrideVolume) {
                    overrideVolume();
                } else {
                    resetSystemVolume();
                }
            }

        }
//        if (isNeedToVibrate(settings, notificationModel)) {
    }

    private void stopMedia() {
        cleanupService();
//        AndroidUtils.stopVibration(this);
//        stopSound();
//        setStatus(STAT_INIT);
//        setNotify(null);
    }

    private void pauseMedia() {
        AndroidUtils.stopVibration(this);
        pauseSound();
        setStatus(STAT_PAUSE);
    }

    private void resumeMedia(NotificationModel notify, NotificationSettings settings) {
        if (settings.enable) {
            if (settings.vibrate) {
                AndroidUtils.startVibration(this, pattern(), 0);
            } else {
                AndroidUtils.stopVibration(this);
            }

            if (settings.sound) {
                resumeSound(notify);

                if (settings.overrideVolume) {
                    overrideVolume();
                } else {
                    resetSystemVolume();
                }
            } else {
                pauseSound();
            }
            setStatus(STAT_PLAY);
        }
    }

    private void overrideVolume() {
//        if (null != mediaPlayer) {
//            mediaPlayer.setVolume(1.0f, 1.0f);
//        }
        int oldVolume = AndroidUtils.setSystemVolumeToMax(this, AudioManager.STREAM_ALARM);
        if (0 < oldVolume) {
            setVolumeSys(oldVolume);
        }
    }

    private void resetSystemVolume() {
        int volume = getVolumeSys();
        if (volume != 0) {
            AndroidUtils.setSystemVolume(this, AudioManager.STREAM_ALARM, volume);
//            if (null != mediaPlayer) {
//                float volumeF = volume / AndroidUtils.audioMgr(this).getStreamMaxVolume(streamMedia);
//                mediaPlayer.setVolume(volumeF, volumeF);
//            }
        }
    }

//    private boolean isNeedToVibrate(NotificationSettings setting, NotificationModel notify) {
////        if ((null != notify) && (setting.vibrate) && (null != notify.vibrate)) {
////            return notify.vibrate;
////        }
////        return false;
////        return setting.enable && null != notify && setting.vibrate && null != notify.vibrate && notify.vibrate;
//        return setting.enable && setting.vibrate && null != notify.vibrate && notify.vibrate;
//    }

    private long[] pattern() {
        long[] vibratePattern = getVibratePattern();
        return (null == vibratePattern) ? DEFAULT_VIBRATE_PATTERN : vibratePattern;
    }


    private void startSound(String uriString) {
        Uri soundUri;
        try {
            soundUri = Uri.parse(uriString);
        } catch (Exception e) {
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        try {
            mediaPlayer.setDataSource(this, soundUri);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e("ServiceMediaPlayer", "Error on start uri: " + soundUri, e);
            cleanupService();
        }
    }

    //    private void initMediaPlayer(final NotificationModel notify) {
    private void initMediaPlayer() {
        if (null == mediaPlayer) {
            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                streamMedia = AudioManager.STREAM_ALARM;
//            } else {
//                streamMedia = AudioManager.STREAM_MUSIC;
//            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
//            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    cleanupService();
                    return true;
                }
            });
        } else {
//            todo verificare se necessario
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        }

        mediaPlayer.setLooping(true);

        mediaPlayer.setVolume(1.0F, 1.0F);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
//                setNotify(notify);
//                setStatus(STAT_PLAY);
            }
        });
    }

    private void stopSound() {
        if (null != mediaPlayer) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
    }

    private void pauseSound() {
        if (null != mediaPlayer) {
            mediaPlayer.pause();
        }
    }

    private void resumeSound(NotificationModel notify) {
        if (null == mediaPlayer) {
            initMediaPlayer();
            startSound(notify.ringtone);
        } else {
            try {
                mediaPlayer.start();
            } catch (Exception e) {
                Log.e("ServiceMediaPlayer", "Resume err", e);
            }
        }
    }



    private void cleanupService() {
        resetSystemVolume();
        stopSound();
        AndroidUtils.stopVibration(this);
        setNotify(null);
        setStatus(STAT_INIT);
        mediaPlayer = null;
        stopSelf();
    }

    /*
        PUBLIC STATIC LAUNCHER
     */
    public static void start(@NonNull Context context, Long notifyPk, @NonNull Class<? extends ServiceMediaPlayer> service) {
        Intent intent = intentRequest(ACTION_PLAY, notifyPk, context, service);
        serviceRequest(context, intent);
    }

    public static boolean startIfAvailable(@NonNull Context context, Long notifyPk, @NonNull Class<? extends ServiceMediaPlayer> service) {
        if (!isActive()) {
            Intent intent = intentRequest(ACTION_PLAY, notifyPk, context, service);
            serviceRequest(context, intent);
            return true;
        }
        return false;
    }

    public static boolean stop(@NonNull Context context, @NonNull Class<? extends ServiceMediaPlayer> service) {
//        if (isPlaying() || isPaused()) {
        if (isActive()) {
            Intent intent = intentRequest(ACTION_STOP, null, context, service);
            serviceRequest(context, intent);
            return true;
        }
        return false;
    }

    public static boolean stopIfAvailableId(@NonNull Context context, Long notifyPk, @NonNull Class<? extends ServiceMediaPlayer> service) {
        if (isActiveOnId(notifyPk)) {
            Intent intent = intentRequest(ACTION_STOP, notifyPk, context, service);
            serviceRequest(context, intent);
            return true;
        }
        return false;
    }

    public static boolean pause(@NonNull Context context, @NonNull Class<? extends ServiceMediaPlayer> service) {
        if (isPlaying()) {
            Intent intent = intentRequest(ACTION_PAUSE, null, context, service);
            serviceRequest(context, intent);
            return true;
        }
        return false;
    }

    public static boolean resume(@NonNull Context context, @NonNull Class<? extends ServiceMediaPlayer> service) {
        if (isPaused()) {
            Intent intent = intentRequest(ACTION_RESUME, null, context, service);
            serviceRequest(context, intent);
            return true;
        }
        return false;
    }

    public static boolean reload(@NonNull Context context, @NonNull Class<? extends ServiceMediaPlayer> service) {
        if (isActive()) {
            Intent intent = intentRequest(ACTION_RESUME, null, context, service);
            serviceRequest(context, intent);
            return true;
        }
        return false;
    }


//    public static boolean reload(@NonNull Context context, @NonNull Class<? extends ServiceMediaPlayer> service) {
//        if (isActive()) {
//            Intent intent = intentRequest(ACTION_RELOAD, null, context, service);
//            serviceRequest(context, intent);
//            return true;
//        }
//        return false;
//    }

//    public static void close(@NonNull Context context, @NonNull Class<? extends ServiceMediaPlayer> service) {
//        if (isActive()) {
//            Intent intent = intentRequest(ACTION_CLOSE, null, context, service);
//            serviceRequest(context, intent);
//        }
//    }

    private static Intent intentRequest(String intentAction, Long notifyPk, Context context, Class<? extends ServiceMediaPlayer> service) {
        Intent serviceIntent = new Intent(context, service);
        serviceIntent.setAction(intentAction);
        if (null != notifyPk) {
            serviceIntent.putExtra(EXTRA_ID, notifyPk);
        }
        return serviceIntent;
    }

    private static void serviceRequest(Context context, Intent serviceIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    /*
        PROTECTED STATIC HELPER
     */

//    protected static NotificationManager getNotifyMan(@NonNull Context context) {
//        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//    }
//
//    @Nullable
//    protected static Vibrator getVibrator(@NonNull Context context) {
//        if (checkPermission(context, Manifest.permission.VIBRATE)) {
//            return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        }
//        return null;
//    }
//
//    protected static boolean checkPermission(@NonNull Context context, String permissionToCheck) {
//        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permissionToCheck));
//    }

//    protected boolean checkPermission(String permissionToCheck) {
//        return AndroidUtils.checkPermission(this, permissionToCheck);
//    }

}
