package it.agevoluzione.tools.android.utils.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import it.agevoluzione.tools.android.AlarmCommander;
import it.agevoluzione.tools.android.reminder.R;
import it.agevoluzione.tools.android.model.NotificationSettings;

public class NotificationSettingsFragment extends PreferenceFragmentCompat {

    private SwitchPreferenceCompat enable;
    private SwitchPreferenceCompat sound;
    private SwitchPreferenceCompat vibrate;
    private SwitchPreferenceCompat showInWake;
    private SwitchPreferenceCompat override_volume;
    private NotificationSettings initSettings;
    private AlarmCommander alarmCommander;

    public void setAlarmCommander(AlarmCommander alarmCommander) {
        this.alarmCommander = alarmCommander;
    }

    @Override
    public void onStart() {
        super.onStart();
        Context context = getContext();
        if (null != context) {
            initSettings = NotificationSettingsUtils.get(context);
        }
        setVisibility(initSettings.enable);
    }

    @Override
    public void onStop() {
        if ((null != enable) && (null != alarmCommander) && (initSettings.enable != enable.isChecked())) {
            if (enable.isChecked()) {
                alarmCommander.enable();
            } else {
                alarmCommander.disable();
                alarmCommander.close();
            }
        }

//        if (initSettings.sound != sound.isChecked() && sound.isChecked()
//                || initSettings.vibrate != vibrate.isChecked() && vibrate.isChecked()){
        if (initSettings.sound != sound.isChecked() || initSettings.vibrate != vibrate.isChecked()){
            alarmCommander.reload();
        }
        super.onStop();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.notification_settings, rootKey);
        bindPref();
        init();
    }

    private void bindPref() {
        enable = findPreference("enable");
        sound = findPreference("sound");
        vibrate = findPreference("vibrate");
        showInWake = findPreference("show_in_wake");
        override_volume = findPreference("override_volume");
    }

    private void init() {
        Context context = getContext();
        if (null != context) {
            PreferenceManager.setDefaultValues(context, R.xml.notification_settings, false);
        }

        if (null != enable) {
            enable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue instanceof Boolean) {
                        setVisibility((boolean) newValue);
                    }
                    return true;
                }
            });
        }

        if (null != sound) {
            sound.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue instanceof Boolean) {
                        override_volume.setEnabled((boolean) newValue);
                    }
                    return true;
                }
            });
        }
    }

    private void setVisibility(boolean value) {
        if (null != sound) {
            sound.setEnabled(value);
        }

        if (null != vibrate) {
            vibrate.setEnabled(value);
        }

        if (null != showInWake) {
            showInWake.setEnabled(value);
        }

        if (null != override_volume) {
            override_volume.setEnabled(value & initSettings.sound);
        }

    }
}