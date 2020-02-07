package it.agevoluzione.tools.android.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;
//import androidx.core.content.ContextCompat;

public final class AndroidUtils {

    private AndroidUtils() {
    }


    /**
     * Return AlarmCommander
     *
     * @param context
     * @return
     */
    @Nullable
    public static AlarmManager alarmManager(@NonNull Context context) {
        return (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Return NoficationManager
     *
     * @param context
     * @return
     */
    @Nullable
    public static NotificationManager notificationManager(@NonNull Context context) {
        return (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createNotificationChannel(@NonNull Context context, @NonNull NotificationChannel channel) {
        NotificationManager notMgr = notificationManager(context);
        if (null != notMgr) {
            notMgr.createNotificationChannel(channel);
        }
    }

    @Nullable
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationChannel getNotificationChannel(@NonNull Context context, @NonNull String channelId) {
        NotificationManager notMgr = notificationManager(context);
        if (null != notMgr) {
            return notMgr.getNotificationChannel(channelId);
        }
        return null;
    }

    @Nullable
    public static PowerManager powerManager(@NonNull Context context) {
        return (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
    }


    /**
     * @param context
     * @return Returns true if the device is in an interactive state.
     */
    @SuppressWarnings(value = "Deprecated")
    public static boolean isOn(@NonNull Context context) {
        PowerManager pm = powerManager(context);
        if (null != pm) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                return pm.isInteractive();
            } else {
                return pm.isScreenOn();
            }
        }
        return false;
    }

    public static void showInWakeLock(@NonNull Activity activity) {
        activity.getWindow().addFlags(
                  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
    }

    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public static void acquireWakeLock(@NonNull Context context, long timeout) {
        PowerManager pwrMgr = powerManager(context);
        if (null != pwrMgr) {
            PowerManager.WakeLock wl = pwrMgr.newWakeLock(
//                    PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK
                    PowerManager.ACQUIRE_CAUSES_WAKEUP
//                    PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.PARTIAL_WAKE_LOCK
//                    PowerManager.PARTIAL_WAKE_LOCK
                    , "it.agevoluzione.tools.android.utils:AndroidUtils");
            wl.acquire(timeout);
        }
    }

    @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public static void releaseWakeLock(@NonNull Context context) {
        PowerManager pwrMgr = powerManager(context);
        if (null != pwrMgr) {
            PowerManager.WakeLock wl = pwrMgr.newWakeLock(
//                    PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK
                    PowerManager.ACQUIRE_CAUSES_WAKEUP
//                    PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.PARTIAL_WAKE_LOCK
//                    PowerManager.PARTIAL_WAKE_LOCK
                    , "it.agevoluzione.tools.android.utils:AndroidUtils");
            wl.release();
        }
    }

    /**
     * Return Vibrator if have permission and device have vibrator
     *
     * @param context
     * @return
     */
    @Nullable
    public static Vibrator vbr(@NonNull Context context) {
        if (checkPermission(context, Manifest.permission.VIBRATE)) {
            Vibrator vibrator = (Vibrator) context.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            if ((null != vibrator) && (vibrator.hasVibrator())) {
                return vibrator;
            }
        }
        return null;
    }

    @Nullable
    public static AudioManager audioManager(@NonNull Context context) {
        return (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * @param context
     * @param audioStream use constant from AudioManager es: AudioManager.STREAM_ALARM
     * @param volume      volume you want to set
     * @return -1 audioManager null, 0 volume not set, other return are the old volume
     */
    public static int setSystemVolume(@NonNull Context context, int audioStream, int volume) {
        AudioManager audioMgr = audioManager(context);
        if (null == audioMgr) {
            return -1;
        }
        int nowVolume = audioMgr.getStreamVolume(audioStream);

        if (nowVolume != volume) {
            audioMgr.setStreamVolume(AudioManager.STREAM_ALARM, volume, AudioManager.FLAG_PLAY_SOUND);
            return nowVolume;
        }
        return 0;
    }

    /**
     * @param context
     * @param audioStream use constant from AudioManager es: AudioManager.STREAM_ALARM
     * @return -1 audioManager null, 0 volume not set, other return are the old volume
     */
    public static int setSystemVolumeToMax(@NonNull Context context, int audioStream) {
        AudioManager audioMgr = audioManager(context);
        if (null == audioMgr) {
            return -1;
        }
        final int maxVolume = audioMgr.getStreamMaxVolume(audioStream);
        int nowVolume = audioMgr.getStreamVolume(audioStream);
        System.out.println("Now volume:" + nowVolume + " max: " + maxVolume);
        if (nowVolume != maxVolume) {
            audioMgr.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, AudioManager.FLAG_PLAY_SOUND);
            return nowVolume;
        }
        return 0;
    }

    /**
     * check permission
     *
     * @param context
     * @param permissionToCheck use "Manifest.permission constant"
     * @return
     */
    public static boolean checkPermission(@NonNull Context context, @NonNull String permissionToCheck) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permissionToCheck));
    }

    @SuppressLint("MissingPermission")
    public static void startVibration(Context context, long[] pattern, int repet) {
        Vibrator vibrator = vbr(context);
        if (null != vibrator) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, repet));
            } else {
                vibrator.vibrate(pattern, repet);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public static void stopVibration(Context context) {
        Vibrator vibrator = vbr(context);
        if (null != vibrator) {
            vibrator.cancel();
        }
    }

    /**
     * @return 0 - If DnD is off.
     * 1 - If DnD is on - Priority Only
     * 2 - If DnD is on - Total Silence
     * 3 - If DnD is on - Alarms Only
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getDndState(@NonNull Context context) throws Settings.SettingNotFoundException {
        return Settings.Global.getInt(context.getContentResolver(), "zen_mode");
    }

    public static void hideKeyboardFrom(@NonNull View view) {
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context.getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (null != imm && imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboardFrom(@NonNull View view) {
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context.getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (null != imm && imm.isActive()) {
            imm.showSoftInput(view, 0);
        }
    }

    @Nullable
    public static WifiManager wifiManager(@NonNull Context context) {
        return (WifiManager) context.getApplicationContext().getSystemService (Context.WIFI_SERVICE);
    }

    public static boolean isGoodSignal(int rssi, int minSignalNeedInPercet) {
        int maxValue = 100;
        int val = WifiManager.calculateSignalLevel(rssi, maxValue);
//        Log.d("WiFi Signal", "signal: "+ rssi + "db calculated: "+val+"/"+maxValue);
        return  val >= minSignalNeedInPercet;
//        return WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4) >= minSignalNeed;
    }

    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    public static boolean wifiIsUp(WifiManager wifiManager) {
//        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        Log.d("WIFI_STAT", "Status: " +wifiManager.getWifiState());
//        Log.d("WIFI_STAT", "conInf: " +wifiManager.getConnectionInfo());
//        Log.d("WIFI_STAT", "netId: " +wifiManager.getConnectionInfo().getNetworkId());

        return wifiManager.isWifiEnabled()
                && wifiManager.getConnectionInfo().getNetworkId() != -1;
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION})
    public static void wifiTryToConnect(@NonNull WifiManager wifiManager, @NonNull ScanResult result) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            WifiNetworkSuggestion sug = new WifiNetworkSuggestion.Builder().setSsid(result.SSID).build();
            List<WifiNetworkSuggestion> sugs = new ArrayList<>();
            sugs.add(sug);
            wifiManager.addNetworkSuggestions(sugs);
        } else {
            List<WifiConfiguration> confNets = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration conf : confNets) {
                if (conf.SSID.equals("\""+result.SSID+"\"")){
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(conf.networkId, true);
                    wifiManager.reconnect();
                    break;
                }
            }
        }
    }


    public static boolean checkActionFromIntent(@Nullable Intent intent, @NonNull String actionToCheck) {
        return null != intent && actionToCheck.equals(intent.getAction());
    }

    /**
     * Generate a BroadCastReceiver for Activity that need to be closed from external Service\Activity.<br>
     * In onCreate(Bundle savedInstanceState) method  Save it.<br>
     * <code>closer = closerForActivity()</code><br>
     * Then Overide onDestroy() and unregister it! <br>
     *   <code>LocalBroadcastManager.getInstance(this).unregisterReceiver(closer);</code>
     * @param activity
     * @return
     */
    @NonNull
    public static BroadcastReceiver closerForActivity(@NonNull final Activity activity) {
        final String action = "act.close."+activity.getClass().getSimpleName();
        BroadcastReceiver closer = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (AndroidUtils.checkActionFromIntent(intent, action)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        activity.finishAndRemoveTask();
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        activity.finishAffinity();
                    } else {
                        activity.finish();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(action);
        LocalBroadcastManager.getInstance(activity)
                .registerReceiver(closer, filter);
        return closer;
    }

    @NonNull
    public static void closerForActivityAutoClose(@NonNull final AppCompatActivity activity) {
        final String action = "act.close."+activity.getClass().getSimpleName();
        final BroadcastReceiver closer = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (AndroidUtils.checkActionFromIntent(intent, action)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        activity.finishAndRemoveTask();
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        activity.finishAffinity();
                    } else {
                        activity.finish();
                    }
                }
            }
        };

        final IntentFilter filter = new IntentFilter(action);

        activity.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                switch (event) {
                    case ON_CREATE:
                        LocalBroadcastManager.getInstance(activity)
                                .registerReceiver(closer, filter);
                        break;
                    case ON_DESTROY:
                        LocalBroadcastManager.getInstance(activity)
                                .unregisterReceiver(closer);
                        source.getLifecycle().removeObserver(this);
                        break;
                }
            }
        });
    }

    public static void closeActivity(Context context, Class<? extends Activity> activity) {
        String action = "act.close."+activity.getSimpleName();
        Intent intentClose =  new Intent(action);
        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intentClose);
    }


    @Nullable
    public static UsbManager usbManager(@NonNull Context context) {
        return (UsbManager) context.getApplicationContext().getSystemService(Context.USB_SERVICE);
    }

}
