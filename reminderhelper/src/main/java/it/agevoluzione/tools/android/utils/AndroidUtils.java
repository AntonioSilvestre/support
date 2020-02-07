//package it.agevoluzione.tools.android.utils;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.AlarmManager;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.media.AudioManager;
//import android.os.Build;
//import android.os.PowerManager;
//import android.os.VibrationEffect;
//import android.os.Vibrator;
//import android.provider.Settings;
//import android.view.View;
//import android.view.WindowManager;
//import android.view.inputmethod.InputMethodManager;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.annotation.RequiresApi;
//import androidx.annotation.RequiresPermission;
//import androidx.core.content.ContextCompat;
////import androidx.core.content.ContextCompat;
//
//public final class AndroidUtils {
//
//    private AndroidUtils() {
//    }
//
//
//    /**
//     * Return AlarmCommander
//     *
//     * @param context
//     * @return
//     */
//    @Nullable
//    public static AlarmManager alarmManager(@NonNull Context context) {
//        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//    }
//
//    /**
//     * Return NoficationManager
//     *
//     * @param context
//     * @return
//     */
//    @Nullable
//    public static NotificationManager notMgr(@NonNull Context context) {
//        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public static void createNotificationChannel(@NonNull Context context, @NonNull NotificationChannel channel) {
//        NotificationManager notMgr = notMgr(context);
//        if (null != notMgr) {
//            notMgr.createNotificationChannel(channel);
//        }
//    }
//
//    @Nullable
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public static NotificationChannel getNotificationChannel(@NonNull Context context, @NonNull String channelId) {
//        NotificationManager notMgr = notMgr(context);
//        if (null != notMgr) {
//            return notMgr.getNotificationChannel(channelId);
//        }
//        return null;
//    }
//
//    @Nullable
//    public static PowerManager pwrMgr(@NonNull Context context) {
//        return (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//    }
//
//
//    /**
//     * @param context
//     * @return Returns true if the device is in an interactive state.
//     */
//    public static boolean isOn(@NonNull Context context) {
//        PowerManager pm = pwrMgr(context);
//        if (null != pm) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//                return pm.isInteractive();
//            } else {
//                return pm.isScreenOn();
//            }
//        }
//        return false;
//    }
//
//    public static void showInWakeLock(@NonNull Activity activity) {
//        activity.getWindow().addFlags(
//                  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//        );
//    }
//
//    @RequiresPermission(Manifest.permission.WAKE_LOCK)
//    public static void acquireWakeLock(@NonNull Context context, long timeout) {
//        PowerManager pwrMgr = pwrMgr(context);
//        if (null != pwrMgr) {
//            PowerManager.WakeLock wl = pwrMgr.newWakeLock(
////                    PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK
//                    PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.PARTIAL_WAKE_LOCK
////                    PowerManager.PARTIAL_WAKE_LOCK
//                    , "it.agevoluzione.tools.android.utils:AndroidUtils");
//            wl.acquire(timeout);
//        }
//    }
//
//    @RequiresPermission(Manifest.permission.WAKE_LOCK)
//    public static void releaseWakeLock(@NonNull Context context) {
//        PowerManager pwrMgr = pwrMgr(context);
//        if (null != pwrMgr) {
//            PowerManager.WakeLock wl = pwrMgr.newWakeLock(
////                    PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK
//                    PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.PARTIAL_WAKE_LOCK
////                    PowerManager.PARTIAL_WAKE_LOCK
//                    , "it.agevoluzione.tools.android.utils:AndroidUtils");
//            wl.release();
//        }
//    }
//
//    /**
//     * Return Vibrator if have permission and device have vibrator
//     *
//     * @param context
//     * @return
//     */
//    @Nullable
//    public static Vibrator vbr(@NonNull Context context) {
//        if (checkPermission(context, Manifest.permission.VIBRATE)) {
//            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//            if ((null != vibrator) && (vibrator.hasVibrator())) {
//                return vibrator;
//            }
//        }
//        return null;
//    }
//
//    @Nullable
//    public static AudioManager audioMgr(@NonNull Context context) {
//        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//    }
//
//    /**
//     * @param context
//     * @param audioStream use constant from AudioManager es: AudioManager.STREAM_ALARM
//     * @param volume      volume you want to set
//     * @return -1 audioMgr null, 0 volume not set, other return are the old volume
//     */
//    public static int setSystemVolume(@NonNull Context context, int audioStream, int volume) {
//        AudioManager audioMgr = audioMgr(context);
//        if (null == audioMgr) {
//            return -1;
//        }
//        int nowVolume = audioMgr.getStreamVolume(audioStream);
//
//        if (nowVolume != volume) {
//            audioMgr.setStreamVolume(AudioManager.STREAM_ALARM, volume, AudioManager.FLAG_PLAY_SOUND);
//            return nowVolume;
//        }
//        return 0;
//    }
//
//    /**
//     * @param context
//     * @param audioStream use constant from AudioManager es: AudioManager.STREAM_ALARM
//     * @return -1 audioMgr null, 0 volume not set, other return are the old volume
//     */
//    public static int setSystemVolumeToMax(@NonNull Context context, int audioStream) {
//        AudioManager audioMgr = audioMgr(context);
//        if (null == audioMgr) {
//            return -1;
//        }
//        final int maxVolume = audioMgr.getStreamMaxVolume(audioStream);
//        int nowVolume = audioMgr.getStreamVolume(audioStream);
//        System.out.println("Now volume:" + nowVolume + " max: " + maxVolume);
//        if (nowVolume != maxVolume) {
//            audioMgr.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, AudioManager.FLAG_PLAY_SOUND);
//            return nowVolume;
//        }
//        return 0;
//    }
//
//    /**
//     * check permission
//     *
//     * @param context
//     * @param permissionToCheck use "Manifest.permission constant"
//     * @return
//     */
//    public static boolean checkPermission(@NonNull Context context, @NonNull String permissionToCheck) {
//        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permissionToCheck));
//    }
//
//    @SuppressLint("MissingPermission")
//    public static void startVibration(Context context, long[] pattern, int repet) {
//        Vibrator vibrator = vbr(context);
//        if (null != vibrator) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                vibrator.vibrate(VibrationEffect.createWaveform(pattern, repet));
//            } else {
//                vibrator.vibrate(pattern, repet);
//            }
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    public static void stopVibration(Context context) {
//        Vibrator vibrator = vbr(context);
//        if (null != vibrator) {
//            vibrator.cancel();
//        }
//    }
//
//    /**
//     * @return 0 - If DnD is off.
//     * 1 - If DnD is on - Priority Only
//     * 2 - If DnD is on - Total Silence
//     * 3 - If DnD is on - Alarms Only
//     */
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
//    public static int getDndState(@NonNull Context context) throws Settings.SettingNotFoundException {
//        return Settings.Global.getInt(context.getContentResolver(), "zen_mode");
//    }
//
//    public static void hideKeyboardFrom(@NonNull View view) {
//        Context context = view.getContext();
//        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        if (null != imm && imm.isActive()) {
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }
//
//    public static void showKeyboardFrom(@NonNull View view) {
//        Context context = view.getContext();
//        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        if (null != imm && imm.isActive()) {
//            imm.showSoftInput(view, 0);
//        }
//    }
//
//
//}
