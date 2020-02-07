package it.agevoluzione.tools.android.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NotificationSettings implements Parcelable, Cloneable {
    public boolean enable;
    public boolean sound;
    public boolean vibrate;
    public boolean showInWake;
    public boolean overrideVolume;

    @Override
    public String toString() {
        return "NotificationSettings{" +
                "enable=" + enable +
                ", sound=" + sound +
                ", vibrate=" + vibrate +
                ", showInWake=" + showInWake +
                ", overrideVolume=" + overrideVolume +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotificationSettings settings = (NotificationSettings) o;

        if (enable != settings.enable) return false;
        if (sound != settings.sound) return false;
        if (vibrate != settings.vibrate) return false;
        if (showInWake != settings.showInWake) return false;
        return overrideVolume == settings.overrideVolume;
    }

    @Override
    public int hashCode() {
        int result = (enable ? 1 : 0);
        result = 31 * result + (sound ? 1 : 0);
        result = 31 * result + (vibrate ? 1 : 0);
        result = 31 * result + (showInWake ? 1 : 0);
        result = 31 * result + (overrideVolume ? 1 : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.enable ? (byte) 1 : (byte) 0);
        dest.writeByte(this.sound ? (byte) 1 : (byte) 0);
        dest.writeByte(this.vibrate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showInWake ? (byte) 1 : (byte) 0);
        dest.writeByte(this.overrideVolume ? (byte) 1 : (byte) 0);
    }

    public NotificationSettings() {
    }

    protected NotificationSettings(Parcel in) {
        this.enable = in.readByte() != 0;
        this.sound = in.readByte() != 0;
        this.vibrate = in.readByte() != 0;
        this.showInWake = in.readByte() != 0;
        this.overrideVolume = in.readByte() != 0;
    }

    public static final Creator<NotificationSettings> CREATOR = new Creator<NotificationSettings>() {
        @Override
        public NotificationSettings createFromParcel(Parcel source) {
            return new NotificationSettings(source);
        }

        @Override
        public NotificationSettings[] newArray(int size) {
            return new NotificationSettings[size];
        }
    };

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (Exception e){
            return null;
        }
    }
}
