package it.agevoluzione.tools.android.utils.notification;

public interface NotificationChannelFactor {
    String chId();
    CharSequence chName();
    int importance();
    String chDesc();
}
