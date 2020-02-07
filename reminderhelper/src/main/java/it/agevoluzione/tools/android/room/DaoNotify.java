package it.agevoluzione.tools.android.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import it.agevoluzione.tools.android.model.NotificationModel;

@Dao
public interface DaoNotify {

    @Insert
    Long[] insertAll(NotificationModel... notificationModels);

    @Insert
    long insert(NotificationModel notify);

    @Update
    void update(NotificationModel... notifys);

    @Delete
    void delete(NotificationModel notify);

    @Query("DELETE FROM notification_table")
    void deleteAll();

    @Query("DELETE FROM notification_table WHERE pk IN (:pks)")
    void deleteByIds(Long... pks);

    @Query("DELETE FROM notification_table WHERE type IN (:types)")
    void deleteByType(Integer... types);

    @Query("DELETE FROM notification_table WHERE status IN (:statuses)")
    void deleteByStatuses(Integer... statuses);

    @Query("DELETE FROM notification_table WHERE pk_obj IN (:pk_objs)")
    void deleteByPkObjs(Long... pk_objs);

    @Query("DELETE FROM notification_table WHERE status  IN (:statuses) AND pk_obj IN (:pk_objs)")
    void deleteWithStatusAndObj(Integer[] statuses, Long[] pk_objs);

    @Query("DELETE FROM notification_table WHERE type  IN (:types) AND pk_obj IN (:pk_objs)")
    void deleteWithTypeAndObj(Integer[] types, Long[] pk_objs);

    @Query("SELECT * FROM notification_table")
    NotificationModel[] readAll();

    @Query("SELECT * FROM notification_table WHERE pk IN (:pks)")
    NotificationModel[] readByIds(Long... pks);

    @Query("SELECT * FROM notification_table WHERE type IN (:types)")
    NotificationModel[] readByType(Integer... types);

    @Query("SELECT * FROM notification_table WHERE status IN (:statuses)")
    NotificationModel[] readByStatus(Integer... statuses);

    @Query("SELECT * FROM notification_table WHERE pk_obj IN (:pkObjs)")
    NotificationModel[] readByPkObj(Long... pkObjs);

    @Query("SELECT * FROM notification_table WHERE status  IN (:statuses) AND pk_obj IN (:pk_objs)")
    NotificationModel[] readWithStatusAndObj(Integer[] statuses, Long[] pk_objs);

    @Query("SELECT * FROM notification_table WHERE type  IN (:types) AND pk_obj IN (:pk_objs)")
    NotificationModel[] readWithTypeAndObj(Integer[] types, Long[] pk_objs);

//    @Query("SELECT * FROM notification_table")
//    LiveData<List<NotificationModel>> getLiveData();

//    @Query("SELECT * FROM notification_table WHERE :colum")
//    List<NotificationModel> getQuery(String colum);

//    @Query("SELECT :p1 FROM notification_table WHERE :p3")
//    void testQuery(String p1, String p2, String p3,String p4);
//    NotificationModel findBy(String first, String last);
//    @Query("SELECT * FROM notificationmodel WHERE pk IN (:pks)")
//    List<NotificationModel> loadAllByIds(int[] pks);

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
}