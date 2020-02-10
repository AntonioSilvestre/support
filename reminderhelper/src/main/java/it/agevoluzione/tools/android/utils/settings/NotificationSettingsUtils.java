package it.agevoluzione.tools.android.utils.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import it.agevoluzione.tools.android.model.NotificationSettings;

public class NotificationSettingsUtils {
    public static NotificationSettings get(@NonNull Context context) {
        SharedPreferences pref_not = PreferenceManager.getDefaultSharedPreferences(context);
        NotificationSettings settings = new NotificationSettings();
        settings.enable = pref_not.getBoolean("enable", true);
        settings.sound = pref_not.getBoolean("sound", true);
        settings.vibrate = pref_not.getBoolean("vibrate", true);
        settings.showInWake = pref_not.getBoolean("show_in_wake", true);
        settings.overrideVolume = pref_not.getBoolean("override_volume", false);

//        Log.d("NotificationSettings",settings.toString());
        return settings;
    }

    public static void set(Context context, NotificationSettings settings) {
        SharedPreferences pref_not = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = pref_not.edit();
        ed.putBoolean("enable", settings.enable);
        ed.putBoolean("sound", settings.sound);
        ed.putBoolean("vibrate", settings.vibrate);
        ed.putBoolean("show_in_wake", settings.showInWake);
        ed.putBoolean("override_volume", settings.overrideVolume);
        ed.apply();
    }

    public static boolean isEnable(Context context) {
        SharedPreferences pref_not = PreferenceManager.getDefaultSharedPreferences(context);
        return pref_not.getBoolean("enable", true);
    }
}
