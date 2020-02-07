package it.agevoluzione.tools.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "notification_table")
public class NotificationModel implements Parcelable, Cloneable {

    @PrimaryKey
    public Long pk;
    public Long starting;
    public Integer repeating;
    public Integer maxRepeating;
    public Integer type;
    public Integer status;
    public String title;
    public String message;
    public String ringtone;
    public Boolean vibrate;
    public Long pk_obj;

    public NotificationModel() {
    }

    @Ignore
    public NotificationModel(long pk) {
        this.pk = pk;
    }

    @Override
    public String toString() {
        return "NotificationModel{" +
                "pk=" + pk +
                ", starting=" + starting +
                ", repeating=" + repeating +
                ", maxRepeating=" + maxRepeating +
                ", type=" + type +
                ", status=" + status +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", ringtone='" + ringtone + '\'' +
                ", vibrate=" + vibrate +
                ", pk_obj=" + pk_obj +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotificationModel that = (NotificationModel) o;

        if (pk != null ? !pk.equals(that.pk) : that.pk != null) return false;
        if (starting != null ? !starting.equals(that.starting) : that.starting != null)
            return false;
        if (repeating != null ? !repeating.equals(that.repeating) : that.repeating != null)
            return false;
        if (maxRepeating != null ? !maxRepeating.equals(that.maxRepeating) : that.maxRepeating != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (ringtone != null ? !ringtone.equals(that.ringtone) : that.ringtone != null)
            return false;
        if (vibrate != null ? !vibrate.equals(that.vibrate) : that.vibrate != null) return false;
        return pk_obj != null ? pk_obj.equals(that.pk_obj) : that.pk_obj == null;
    }

    @Override
    public int hashCode() {
        int result = pk != null ? pk.hashCode() : 0;
        result = 31 * result + (starting != null ? starting.hashCode() : 0);
        result = 31 * result + (repeating != null ? repeating.hashCode() : 0);
        result = 31 * result + (maxRepeating != null ? maxRepeating.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (ringtone != null ? ringtone.hashCode() : 0);
        result = 31 * result + (vibrate != null ? vibrate.hashCode() : 0);
        result = 31 * result + (pk_obj != null ? pk_obj.hashCode() : 0);
        return result;
    }

    @Override
    public NotificationModel clone() {
        try {
            return (NotificationModel) super.clone();
        } catch (CloneNotSupportedException e) {
            return new NotificationModel();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.pk);
        dest.writeValue(this.starting);
        dest.writeValue(this.repeating);
        dest.writeValue(this.maxRepeating);
        dest.writeValue(this.type);
        dest.writeValue(this.status);
        dest.writeString(this.title);
        dest.writeString(this.message);
        dest.writeString(this.ringtone);
        dest.writeValue(this.vibrate);
        dest.writeValue(this.pk_obj);
    }

    protected NotificationModel(Parcel in) {
        this.pk = (Long) in.readValue(Long.class.getClassLoader());
        this.starting = (Long) in.readValue(Long.class.getClassLoader());
        this.repeating = (Integer) in.readValue(Integer.class.getClassLoader());
        this.maxRepeating = (Integer) in.readValue(Integer.class.getClassLoader());
        this.type = (Integer) in.readValue(Integer.class.getClassLoader());
        this.status = (Integer) in.readValue(Integer.class.getClassLoader());
        this.title = in.readString();
        this.message = in.readString();
        this.ringtone = in.readString();
        this.vibrate = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.pk_obj = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Creator<NotificationModel> CREATOR = new Creator<NotificationModel>() {
        @Override
        public NotificationModel createFromParcel(Parcel source) {
            return new NotificationModel(source);
        }

        @Override
        public NotificationModel[] newArray(int size) {
            return new NotificationModel[size];
        }
    };
}

