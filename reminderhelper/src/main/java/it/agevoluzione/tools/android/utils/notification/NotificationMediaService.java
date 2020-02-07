//package it.agevoluzione.test.notificationtest.utils.notification;
//
//import android.app.Notification;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Build;
//import android.os.IBinder;
//import android.os.VibrationEffect;
//import android.os.Vibrator;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.util.Log;
//
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import it.agevoluzione.appill.basic.activities.MainActivity;
//import it.agevoluzione.appill.basic.tool.AndroidNotificationFactory;
//import it.agevoluzione.appill.model.NotificationAdministration;
//import it.agevoluzione.appill.tool.SettingsData;
//import it.agevoluzione.appill.tool.notification.AndroidNotificationHelper;
//
//import static it.agevoluzione.appill.tool.SettingsData.ACTION_MEDIAPLAYER_PLAY_MUSIC;
//import static it.agevoluzione.appill.tool.SettingsData.ACTION_MEDIAPLAYER_STOP_MUSIC;
//import static it.agevoluzione.appill.tool.SettingsData.EXTRA_ENABLE_VIBRATE;
//import static it.agevoluzione.appill.tool.SettingsData.EXTRA_NOTIFICATION;
//
///**
// * NEED permission Vibrate
// *
// */
//public class NotificationMediaService extends Service {
//
//    private MediaPlayer mediaPlayer;
//
//    private static AtomicBoolean playing;
//
//    public static long[] DEFAULT_VIBRATE_PATTERN = {100, 200, 300, 400, 500, 400, 300, 200, 400};
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//
//    public static boolean isPlaying() {
//        if (null == playing) {
//            playing = new AtomicBoolean();
//        }
//        return playing.format();
//    }
//
//    private void changePlayStat(boolean play) {
//        if (null == playing) {
//            playing = new AtomicBoolean();
//        }
//        playing.set(play);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        Log.d("SoundServiceHelper","intent:"+intent+" flag:"+flags+" startId:"+startId);
//        String action = intent.getAction();
//        if (null == action) {
//            return super.onStartCommand(intent,flags,startId);
//        }
//
//        NotificationAdministration notify = intent.getParcelableExtra(EXTRA_NOTIFICATION);
//        boolean vibrate = intent.getBooleanExtra(EXTRA_ENABLE_VIBRATE,false);
//
//        switch (action) {
//            case ACTION_MEDIAPLAYER_PLAY_MUSIC:
//                startMedia(notify.getRingtone(),vibrate);
//                Notification notification = AndroidNotificationFactory.produceAndroidNotify(this, notify);
//                startForeground(notify.getPk(), notification);
//
//                Intent intentToMain = new Intent(this, MainActivity.class);
//                intentToMain.setAction(SettingsData.ACTION_OPEN_CELL_DETAIL_FROM_REMINDER);
//                intentToMain.putExtra(SettingsData.EXTRA_CELL_ID,notify.getCell().getId());
//                intentToMain.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                startActivity(intentToMain);
//                break;
//            case ACTION_MEDIAPLAYER_STOP_MUSIC:
//                stopMedia();
//                break;
//        }
//
//
//        // service will not be recreated if abnormally terminated
//        return START_NOT_STICKY;
//    }
//
//    private void startMedia(String sound, boolean vibrate) {
//
//        if (vibrate) {
//            playVibration(DEFAULT_VIBRATE_PATTERN);
//        } else {
//            stopVibration();
//        }
//
//        startSound(sound);
//
//        changePlayStat(true);
////
//    }
//
//
//    private void stopVibration() {
//        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        if ((null != vibrator) && (vibrator.hasVibrator())){
//            vibrator.cancel();
//        }
//        changePlayStat(false);
//    }
//
//    private void playVibration(long[] pattern) {
//        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        if ((null == vibrator) || (!vibrator.hasVibrator())){
//            return;
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
//        } else {
//            vibrator.vibrate(pattern,0);
//        }
//
//    }
//
//    private void startSound(String uriString) {
//
//        Uri soundUri;
//        try {
//            soundUri = Uri.parse(uriString);
//        } catch (Exception e) {
//            soundUri = AndroidNotificationHelper.DEFAULT_RING_TONE;
//        }
//
//        if (null == mediaPlayer) {
//            mediaPlayer = new MediaPlayer();
//
//            mediaPlayer.setLooping(true);
//
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    mp.start();
//                }
//            });
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    cleanup();
//                }
//            });
//        }
//        try {
//            mediaPlayer.setDataSource(this, soundUri);
//            mediaPlayer.prepareAsync();
//        } catch (Exception e) {
//            cleanup();
//        }
//
//    }
//
//    private void stopMedia() {
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//        stopVibration();
//        cleanup();
//    }
//
//    private void cleanup() {
//        stopSelf();
//    }
//
//
//
//}
