package it.agevoluzione.tools.android.room;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import it.agevoluzione.tools.android.model.NotificationModel;

//@Database(entities = {NotificationModel.class}, version = 1)
@Database(entities = {NotificationModel.class}, version = 2, exportSchema = false)
public abstract class DatabaseNotify extends RoomDatabase {
    public abstract DaoNotify notificationDao();
    private static volatile DatabaseNotify INSTANCE;

    static DatabaseNotify getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DatabaseNotify.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DatabaseNotify.class, "notification_db.db")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
            database.execSQL("ALTER TABLE notification_table ADD COLUMN maxRepeating INTEGER");
        }
    };
}