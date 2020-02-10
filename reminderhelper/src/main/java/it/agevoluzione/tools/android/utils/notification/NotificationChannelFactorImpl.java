//package it.agevoluzione.tools.android.utils.notification;
//
//import androidx.core.app.NotificationManagerCompat;
//import it.agevoluzione.tools.android.model.NotificationModel;
//
//public class NotificationChannelFactorImpl implements NotificationChannelFactor {
//
//    private String baseName;
//
//    public NotificationChannelFactorImpl(String baseName) {
//        this.baseName = baseName;
//    }
//
//    @Override
//    public String id(NotificationModel notificationModel) {
//        return baseName + notificationModel.type;
//    }
//
//    @Override
//    public CharSequence name(NotificationModel notificationModel) {
//        return baseName + notificationModel.type;
//    }
//
//    @Override
//    public int importance(NotificationModel notificationModel) {
//        return  NotificationManagerCompat.IMPORTANCE_LOW;
//    }
//
//    @Override
//    public String description(NotificationModel notificationModel) {
//        return "Notifica di tipo " + notificationModel.type;
//    }
//}
//
