//package it.agevoluzione.test.notificationtest.utils.notification;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//
//public class MediaServiceUtils {
//
//    //    public static void startMusic(@NonNull Context context, int id, @NonNull String channelName
////            , @Nullable String music,@NonNull Class<? extends AppillSoundServiceHelper> soundSeviceClass) {
//    public static void startMusic(@NonNull Context context, NotificationAdministration notify, boolean vibrate, @NonNull Class<? extends Service> soundSeviceClass) {
//        Intent serviceIntent = new Intent(context, soundSeviceClass);
//        serviceIntent.setAction(ACTION_MEDIAPLAYER_PLAY_MUSIC);
////        serviceIntent.putExtra(EXTRA_NOTIFICATION_ID, id);
//        serviceIntent.putExtra(EXTRA_NOTIFICATION, notify);
////        serviceIntent.putExtra(EXTRA_CHANNEL_NAME, channelName);
////        serviceIntent.putExtra(EXTRA_MUSIC_URI, notify.getRingtone());
//        serviceIntent.putExtra(EXTRA_ENABLE_VIBRATE, vibrate);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(serviceIntent);
//        } else {
//            context.startService(serviceIntent);
//        }
//    }
//
//    //    public static void stopMusic(@NonNull Context context, int id,@NonNull String channelName
////            ,@NonNull Class<? extends AppillSoundServiceHelper> mediaServiceClass) {
//    public static void stopMusic(@NonNull Context context, @NonNull Class<? extends Service> mediaServiceClass) {
//        Intent serviceIntent = new Intent(context, mediaServiceClass);
//        serviceIntent.setAction(ACTION_MEDIAPLAYER_STOP_MUSIC);
////        serviceIntent.putExtra(EXTRA_NOTIFICATION, notify);
////        serviceIntent.putExtra(EXTRA_CHANNEL_NAME, channelName);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(serviceIntent);
//        } else {
//            context.startService(serviceIntent);
//        }
//    }
//}
