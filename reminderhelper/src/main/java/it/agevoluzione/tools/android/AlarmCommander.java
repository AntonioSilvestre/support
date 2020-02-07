package it.agevoluzione.tools.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import it.agevoluzione.tools.android.exceptions.DBExceptionCompleteRemoval;
import it.agevoluzione.tools.android.exceptions.NotificationException;
import it.agevoluzione.tools.android.exceptions.NotificationMaxPostponeReachedException;
import it.agevoluzione.tools.android.model.NotificationModel;
import it.agevoluzione.tools.android.model.NotificationStatus;
import it.agevoluzione.tools.android.room.RepositoryNotify;
import it.agevoluzione.tools.android.utils.media.ServiceMediaPlayer;
import it.agevoluzione.tools.android.utils.reminder.ReminderUtils;
import it.agevoluzione.tools.android.utils.settings.NotificationSettingsUtils;

public class AlarmCommander {

    private Context context;
    private Class<? extends BroadcastReceiver> receiver;
    private Class<? extends ServiceMediaPlayer> mediaService;

    private RepositoryNotify repo;

//    private boolean vibrate;
//    public @interface RequireMediaService {
//    }

    public AlarmCommander(@NonNull Context context, @NonNull Class<? extends BroadcastReceiver> receiver, @NonNull Class<? extends ServiceMediaPlayer> mediaService) {
        this.context = context;
        this.receiver = receiver;
        this.mediaService = mediaService;
        this.repo = new RepositoryNotify(context);
    }

//    public AlarmCommander(@NonNull Context context, @NonNull Class<? extends BroadcastReceiver> receiver) {
//        this.context = context;
//        this.receiver = receiver;
//        repo = new RepositoryNotify(context);
//    }

//
//    public void setMediaService(Class<? extends ServiceMediaPlayer> mediaService) {
//        this.mediaService = mediaService;
//    }

//    public void stopMedia() {
//        if (!ServiceMediaPlayer.isActiveId(0)) {
//            ServiceMediaPlayer.stopMusic(context, mediaService);
//        }
//    }


//    private void stopMediaServiceById(long id) {
//        if (ServiceMediaPlayer.isActiveId(id)) {
//            ServiceMediaPlayer.stopMusic(context, id, mediaService);
//        }
//    }

    public void create(RepositoryNotify.Listener listener, NotificationModel... notifications) {
        RepositoryNotify.CrudOperation opCreate = new RepositoryNotify.CreateOperation() {
            @Nullable
            @Override
            public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
                if ((null != notifications) && (null != notifications[0])) {
                    long now = Calendar.getInstance().getTimeInMillis();

                    for (NotificationModel notify : notifications) {
                        if (now < notify.starting) {
                            if (!NotificationSettingsUtils.isEnable(context)) {
                                notify.status = NotificationStatus.DISABLED;
                            } else {
                                ReminderUtils.setReminder(context, receiver, notify);
                                notify.status = NotificationStatus.SCHEDULED;
                                //todo remove this print
//                                System.out.println("CREATE: "+notify);
                            }
                        } else {
                            notify.status = NotificationStatus.EXPIRED;
                        }
                    }
                }
                return notifications;
            }
        };
        RepositoryNotify.CrudOperation opUpdate = new RepositoryNotify.UpdateOperation();

        repo.request(listener, notifications, opCreate, opUpdate);
    }


    public void create(NotificationModel... notifications) {
        create(null, notifications);
    }

//    //      todo creare edit notify
//    public void edit(RepositoryNotify.Listener listener, final NotificationModel... notifications) {
//        RepositoryNotify.CrudOperation opRead = new RepositoryNotify.ReadOperation() {
//            @Nullable
//            @Override
//            public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
//                if ((null == notifications) || (0 == notifications.length)) {
//                    return null;
//                }
//                for (int i = 0, size = notifications.length; i < size; i++) {
//                    NotificationModel notification = notifications[i];
//                    public Integer type;
//                    public String title;
//                    public String message;
//                    public String ringtone;
//                    public Boolean vibrate;
//                    public Long pk_obj;
//
//                }
//                return super.afterDbOperation(notifications);
//            }
//        };
//
//    }
//
//    public void edit(NotificationModel... notifications) {
//        edit(null, notifications);
//    }


    /**
     * Per ottenere il risultato delle query senza nessuna manipolazione
     *
     * @param notificationModel se null -> tutti no
     *                          <ul>
     *                          <li>
     *                          notificationModel == null --> all
     *                          </li>
     *                          <li>
     *                          notificationModel.pk --> all with pk
     *                          </li>
     *                          <li>
     *                          notificationModel.type --> all with type
     *                          </li>
     *                          <li>
     *                          notificationModel.status --> all with status
     *                          </li>
     *                          <li>
     *                          notificationModel.pk_obj --> all with status
     *                          </li>
     *                          <li>
     *                          notificationModel.status & notificationModel.pk_obj
     *                          --> all with statusAndPk
     *                          </li>
     *                          </ul>
     * @param listener          listener to grab query
     */
    public void get(RepositoryNotify.Listener listener, NotificationModel... notificationModel) {
        RepositoryNotify.CrudOperation op = new RepositoryNotify.ReadOperation();
        repo.request(listener, notificationModel, op);
    }

    //    public void show(long pk, Class<? extends ServiceMediaPlayer> mediaService) {
    public void show(long pk) {
        ServiceMediaPlayer.start(context, pk, mediaService);
    }

//    @RequireMediaService
//    @RequiresPermission(android.Manifest.permission.VIBRATE)
//    public void showAndVibe(long pk) {
//        ServiceMediaPlayer.startMusic(context, pk, true, mediaService);
//    }


    //    public void catchIt(long pk, Class<? extends ServiceMediaPlayer> mediaService) {

    public void catchIt(NotificationModel notify) {
        RepositoryNotify.CrudOperation readOp = new RepositoryNotify.ReadOperation() {
            @Nullable
            @Override
            public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
                if (null != notifications && 0 != notifications.length) {
//                            && NotificationStatus.SHOWED == notifications[0].status) {
                    for (int i = 0, size = notifications.length; i < size; i++) {
                        switch (notifications[i].status) {
                            case NotificationStatus.SCHEDULED:
                                ReminderUtils.cancelReminder(context, receiver, notifications[i]);
                            case NotificationStatus.SHOWED:
                                ServiceMediaPlayer.stopIfAvailableId(context, notifications[i].pk, mediaService);
                                break;
                        }
                        notifications[i].status = NotificationStatus.CATCH;
                    }
                    return notifications;
                }
                return null;
            }
        };
        RepositoryNotify.CrudOperation opUpdate = new RepositoryNotify.UpdateOperation();
        repo.request(notify, readOp, opUpdate);
    }


    public void catchIt(long pk) {
        if (ServiceMediaPlayer.stopIfAvailableId(context, pk, mediaService)) {
            RepositoryNotify.CrudOperation readOp = new RepositoryNotify.ReadOperation() {
                @Nullable
                @Override
                public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
                    if (null != notifications && 0 != notifications.length
                            && NotificationStatus.SHOWED == notifications[0].status) {
                        notifications[0].status = NotificationStatus.CATCH;
                        return notifications;
                    }
                    return null;
                }
            };
            RepositoryNotify.CrudOperation opUpdate = new RepositoryNotify.UpdateOperation();
            repo.request(new NotificationModel(pk), readOp, opUpdate);
        }
    }

    public void postpone(NotificationModel notify, final long newTime, RepositoryNotify.Listener lister) {
        long now = Calendar.getInstance().getTimeInMillis();
        if (now > newTime) {
            if (null != lister) {
                lister.onError(null, new NotificationException("New time < now! newTime:" + newTime));
            }
            Log.e("AlarmCommander", "postpone Err newtime expired! now is:" + now + " reminder at:" + newTime);
        } else if (!NotificationSettingsUtils.isEnable(context)) {
            if (null != lister) {
                lister.onError(null, new NotificationException("Notification was disabled!"));
            }
            Log.e("AlarmCommander", "Notification was disabled!");
        } else {
            RepositoryNotify.CrudOperation opRead = new RepositoryNotify.ReadOperation() {
                @Override
                public NotificationModel[] afterDbOperation(NotificationModel... notifications) throws Exception {
                    if ((null == notifications) || (null == notifications[0])) {
                        return null;
                    } else {
                        ServiceMediaPlayer.stopIfAvailableId(context, notifications[0].pk, mediaService);
                        ReminderUtils.cancelReminder(context, receiver, notifications[0]);
                        if (null == notifications[0].repeating) {
                            notifications[0].repeating = 0;
                        }

                        if (notifications[0].maxRepeating != null && notifications[0].repeating >= notifications[0].maxRepeating) {
                            notifications[0].status = NotificationStatus.SKIPPED;
                        } else {
                            notifications[0].starting = newTime;
                            notifications[0].status = NotificationStatus.SCHEDULED;
                            notifications[0].repeating++;
                            ReminderUtils.setReminder(context, receiver, notifications[0]);
                        }
                        return notifications;
                    }
                }
            };

            RepositoryNotify.CrudOperation opUpdateTime = new RepositoryNotify.UpdateOperation() {
                @Nullable
                @Override
                public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) throws Exception {
                    if ((null == notifications) || (null == notifications[0])) {
                        return null;
                    }
                    if (notifications[0].status == NotificationStatus.SKIPPED) {
                        throw new NotificationMaxPostponeReachedException("repeating:" + notifications[0].repeating + " max:" + notifications[0].maxRepeating);
                    }
                    return notifications;
                }
            };
            repo.request(lister, notify, opRead, opUpdateTime);
        }
    }


        //    public void postpone(long pkNotify, final long newTime, RepositoryNotify.Listener lister, final Class<? extends ServiceMediaPlayer> mediaService) {
    public void postpone(long pkNotify, final long newTime, RepositoryNotify.Listener lister) {
        postpone(new NotificationModel(pkNotify), newTime, lister);
//        long now = Calendar.getInstance().getTimeInMillis();
//        if (now > newTime) {
//            if (null != lister) {
//                lister.onError(null, new NotificationException("New time < now! newTime:" + newTime));
//            }
//            Log.e("AlarmCommander", "postpone Err newtime expired! now is:" + now + " reminder at:" + newTime);
//        } else if (!NotificationSettingsUtils.isEnable(context)) {
//            if (null != lister) {
//                lister.onError(null, new NotificationException("Notification was disabled!"));
//            }
//            Log.e("AlarmCommander", "Notification was disabled!");
//        } else {
//            RepositoryNotify.CrudOperation opGetNotById = new RepositoryNotify.ReadOperation() {
//                @Override
//                public NotificationModel[] afterDbOperation(NotificationModel... notifications) throws Exception {
//                    if ((null == notifications) || (null == notifications[0])) {
//                        return null;
//                    } else {
//
//                        ServiceMediaPlayer.stopIfAvailableId(context, notifications[0].pk, mediaService);
//                        ReminderUtils.cancelReminder(context, receiver, notifications[0]);
//                        if (null == notifications[0].repeating) {
//                            notifications[0].repeating = 0;
//                        }
//
//                        if (notifications[0].maxRepeating != null && notifications[0].repeating >= notifications[0].maxRepeating) {
//                            notifications[0].status = NotificationStatus.SKIPPED;
//                        } else {
//                            notifications[0].starting = newTime;
//                            notifications[0].status = NotificationStatus.SCHEDULED;
//                            notifications[0].repeating++;
//                            ReminderUtils.setReminder(context, receiver, notifications[0]);
//                        }
//                        return notifications;
//                    }
//                }
//            };
//
//            RepositoryNotify.CrudOperation opUpdateTime = new RepositoryNotify.UpdateOperation() {
//                @Nullable
//                @Override
//                public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) throws Exception {
//                    if ((null == notifications) || (null == notifications[0])) {
//                        return null;
//                    }
//                    if (notifications[0].status == NotificationStatus.SKIPPED) {
//                        throw new NotificationException("Max retry reached! repeat:" + notifications[0].repeating + " max:" + notifications[0].maxRepeating);
//                    }
//                    return notifications;
//                }
//            };
//            repo.request(lister, new NotificationModel(pkNotify), opGetNotById, opUpdateTime);
//        }
    }

    //    public void postpone(long pkNotify, final long newTime, Class<? extends ServiceMediaPlayer> mediaService) {
    public void postpone(long pkNotify, final long newTime) {
        postpone(pkNotify, newTime, null);
    }

//    public void silence(Class<? extends ServiceMediaPlayer> mediaService) {
//        ServiceMediaPlayer.pause(context, mediaService);
//    }

    public void silence() {
        ServiceMediaPlayer.pause(context, mediaService);
    }

    public void resumeSilence() {
        ServiceMediaPlayer.resume(context, mediaService);
    }

    public void reload() {
        ServiceMediaPlayer.reload(context, mediaService);
    }

//    public void resumeSilence(Class<? extends ServiceMediaPlayer> mediaService) {
//        ServiceMediaPlayer.resume(context, mediaService);
//    }

    /**
     * Restore only SCHEDULED Status Notification on DB with correct time
     *
     * @param listener
     */
    public void restore(RepositoryNotify.Listener listener) {
        NotificationModel notSched = new NotificationModel();
        notSched.status = NotificationStatus.SCHEDULED;

        RepositoryNotify.CrudOperation opRead = new RepositoryNotify.ReadOperation() {
            @Nullable
            @Override
            public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
                if ((null != notifications) && (0 != notifications.length)) {
                    long now = Calendar.getInstance().getTimeInMillis();

                    List<NotificationModel> toUpdate = new ArrayList<>();
                    for (NotificationModel notification : notifications) {
                        if (now < notification.starting) {
                            toUpdate.add(notification);
                            ReminderUtils.setReminder(context, receiver, notification);
                            Log.d("AlarmCommander", "Restore Remind: " + notification);
                        }
                    }
                    int size = toUpdate.size();
                    if (0 < size) {
                        return toUpdate.toArray(new NotificationModel[size]);
                    }
                } else {
                    Log.w("AlarmCommander", "No reminder to enable!");
                }
                return null;
            }

        };
        repo.request(listener, notSched, opRead);
    }

    /**
     * Restore only SCHEDULED Status Notification on DB
     */
    public void restore() {
        restore(null);
    }

    /**
     * Enable only DISABLED not EXPIRED Notification
     *
     * @param listener
     */
    public void enable(RepositoryNotify.Listener listener) {
        if (NotificationSettingsUtils.isEnable(context)) {

            RepositoryNotify.CrudOperation opRead = new RepositoryNotify.ReadOperation() {
                @Nullable
                @Override
                public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
                    if ((null != notifications) && (0 != notifications.length)) {
                        long now = Calendar.getInstance().getTimeInMillis();
                        for (NotificationModel notification : notifications) {
                            notification.status = NotificationStatus.SCHEDULED;
                            if (isLost(notification, now)) {
                                Log.w("AlarmCommander", "Notification Expired/Lost not Enabled! " + notification);
                            } else {
                                ReminderUtils.setReminder(context, receiver, notification);
                                Log.d("AlarmCommander", "Enable Remind: " + notification);
                            }
                        }
                        return notifications;
                    } else {
                        Log.d("AlarmCommander", "No reminder to enable!");
                    }
                    return null;
                }
            };

            RepositoryNotify.CrudOperation opUpdate = new RepositoryNotify.UpdateOperation();

            NotificationModel notStatus = new NotificationModel();
            notStatus.status = NotificationStatus.DISABLED;
            repo.request(listener, notStatus, opRead, opUpdate);
        } else {
            if (null != listener) {
                listener.onError(null, new NotificationException("Notification was disabled!"));
            }
            Log.e("AlarmCommander", "Notification was disabled!");
        }

    }

    public void enable() {
        enable(null);
    }

    /**
     * Disable only SCHEDULED Notification
     *
     * @param listener
     */
    public void disable(RepositoryNotify.Listener listener) {
        RepositoryNotify.CrudOperation opRead = new RepositoryNotify.ReadOperation() {
            @Nullable
            @Override
            public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
                if ((null != notifications) && (0 != notifications.length)) {
                    for (NotificationModel notification : notifications) {
                        notification.status = NotificationStatus.DISABLED;
                        ReminderUtils.cancelReminder(context, receiver, notification);
                    }
                    return notifications;
                } else {
                    Log.d("AlarmCommander", "No reminder to enable!");
                }
                return null;
            }
        };
        RepositoryNotify.CrudOperation opUpdate = new RepositoryNotify.UpdateOperation();

        NotificationModel notStatus = new NotificationModel();
        notStatus.status = NotificationStatus.SCHEDULED;
        repo.request(listener, notStatus, opRead, opUpdate);
    }

    public void disable() {
        disable(null);
    }

    public void close() {
        ServiceMediaPlayer.stop(context, mediaService);
    }

    public void updateStats(RepositoryNotify.Listener listener) {
        RepositoryNotify.CrudOperation opRead = new RepositoryNotify.ReadOperation() {
            @Nullable
            @Override
            public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
                if ((null == notifications) || (0 == notifications.length)) {
                    Log.d("AlarmCommander", "Empty db");
                    return null;
                }
                List<NotificationModel> editedList = new ArrayList<>();

                long now = Calendar.getInstance().getTimeInMillis();

                for (NotificationModel notification : notifications) {
                    if (isLost(notification, now)) {
                        editedList.add(notification);
                    }
                }
                int newSize = editedList.size();
                if (0 == newSize) {
                    return null;
                }
                return editedList.toArray(new NotificationModel[newSize]);

            }
        };

        NotificationModel notStat1 = new NotificationModel();
        notStat1.status = NotificationStatus.SCHEDULED;
        NotificationModel notStat2 = new NotificationModel();
        notStat2.status = NotificationStatus.SHOWED;

        RepositoryNotify.CrudOperation opUpdate = new RepositoryNotify.UpdateOperation();
        repo.request(listener, new NotificationModel[]{notStat1, notStat2}, opRead, opUpdate);
    }

    public void updateStats() {
        updateStats(null);
    }

    /**
     * @param listener
     * @param force         true to force clear
     * @param notifications se null -> tutti no
     *                      <ul>
     *                      <li>
     *                      notificationModel == null --> all
     *                      </li>
     *                      <li>
     *                      notificationModel.pk --> all with pk
     *                      </li>
     *                      <li>
     *                      notificationModel.type --> all with type
     *                      </li>
     *                      <li>
     *                      notificationModel.status --> all with status
     *                      </li>
     *                      <li>
     *                      notificationModel.pk_obj --> all with status
     *                      </li>
     *                      <li>
     *                      notificationModel.status & notificationModel.pk_obj
     *                      --> all with statusAndPk
     *                      </li>
     *                      </ul>
     * @param listener      listener to grab query
     * @throws DBExceptionCompleteRemoval
     */
    public void delete(RepositoryNotify.Listener listener, boolean force, NotificationModel... notifications) throws DBExceptionCompleteRemoval {
        if ((null == notifications) || (0 == notifications.length)) {
            if (null != listener) {
                listener.onError(null, new NotificationException("notifications Request Null!"));
            }
            return;
        }
        if ((null == notifications[0]) && (!force)) {
            throw new DBExceptionCompleteRemoval("if you are sure try with force = true");
        }

        RepositoryNotify.CrudOperation opDelete = new RepositoryNotify.DeleteOperation();

        RepositoryNotify.CrudOperation opRead = new RepositoryNotify.ReadOperation() {
            @Nullable
            @Override
            public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
                if ((null != notifications) && (0 != notifications.length)) {
                    for (NotificationModel notification : notifications) {
                        if (null != notification) {
                            switch (notification.status) {
                                case NotificationStatus.SHOWED:
                                    ServiceMediaPlayer.stopIfAvailableId(context, notification.pk, mediaService);
                                    break;
                                case NotificationStatus.SCHEDULED:
                                    ReminderUtils.cancelReminder(context, receiver, notification);
                                    break;
                                default:
                            }
                        }
                    }
                    return notifications;
                }
                return null;
            }
        };
        repo.request(listener, notifications, opRead, opDelete);
    }

    /**
     * @param force         true to force clear
     * @param notifications se null -> tutti no
     *                      <ul>
     *                      <li>
     *                      notificationModel == null --> all
     *                      </li>
     *                      <li>
     *                      notificationModel.pk --> all with pk
     *                      </li>
     *                      <li>
     *                      notificationModel.type --> all with type
     *                      </li>
     *                      <li>
     *                      notificationModel.status --> all with status
     *                      </li>
     *                      <li>
     *                      notificationModel.pk_obj --> all with status
     *                      </li>
     *                      <li>
     *                      notificationModel.status & notificationModel.pk_obj
     *                      --> all with statusAndPk
     *                      </li>
     *                      </ul>
     * @throws DBExceptionCompleteRemoval
     */
    public void delete(boolean force, NotificationModel... notifications) throws DBExceptionCompleteRemoval {
        delete(null, force, notifications);
    }

    /**
     * Verifica e modifica lo status della notifica in stato SCHEDULED/SHOWED in EXPIRED/LOST
     *
     * @param notify
     * @param now
     * @return true se necessita di update, false altrimenti
     */
    private boolean isLost(NotificationModel notify, long now) {
        if ((null == notify) || (null == notify.starting)) {
            return false;
        }
        if (null == notify.status) {
            notify.status = NotificationStatus.UNKNOW;
            return true;
        }

        if (now > notify.starting) {
            switch (notify.status) {
                case NotificationStatus.SCHEDULED:
                    notify.status = (null == notify.repeating)
                            ? NotificationStatus.EXPIRED
                            : NotificationStatus.SKIPPED;
                    return true;
                case NotificationStatus.SHOWED:
                    notify.status = NotificationStatus.LOST;
                    return true;
            }
        }

        return false;
    }

    public Context getContext() {
        return context;
    }


    //    public void setAlarm(final NotificationModel notify, RepositoryNotify.Listener listener) {
//        long now = Calendar.getInstance().getTimeInMillis();
//        if (now >= notify.starting) {
//            Log.e("AlarmCommander", "Reminder expired!" + notify);
//            if (null != listener) {
//                listener.onError(null, new NotificationException("Reminder expired!"));
//            }
//        } else {
//            RepositoryNotify.CrudOperation opCreate = new RepositoryNotify.CreateOperation() {
//                @Nullable
//                @Override
//                public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
//                    if ((null != notifications) && (0 != notifications.length)) {
//                        notifications[0].status = NotificationStatus.SCHEDULED;
//                        ReminderUtils.setReminder(context, receiver, notifications[0]);
//                        Log.d("AlarmCommander", "Add Remind: " + notifications[0]);
//                    } else {
//                        Log.e("AlarmCommander", "Reminder Non trovato " + notify);
//                    }
//                    return notifications;
//                }
//            };
//            RepositoryNotify.CrudOperation opUpdate = new RepositoryNotify.UpdateOperation();
//            repo.request(listener, notify, opCreate, opUpdate);
//        }
//    }

//    public void setAlarm(final NotificationModel notify) {
//        setAlarm(notify, null);
//    }

//    public void setAlarms(final NotificationModel... notifys) {
//        RepositoryNotify.CrudOperation opCreate = new RepositoryNotify.CreateOperation() {
//            @Nullable
//            @Override
//            public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
//                if (null != notifications) {
//                    for (NotificationModel not : notifications) {
//                        not.status = NotificationStatus.PENDING;
//                        ReminderUtils.setReminder(context, receiver, not);
//                        Log.d("AlarmCommander", "Add Remind: " + not);
//                    }
//                } else {
//                    Log.e("AlarmCommander", "Reminder Non trovato"+notifys);
//                }
//                return notifications;
//            }
//        };
//        RepositoryNotify.CrudOperation opUpdate = new RepositoryNotify.SimpleCrudOperation(RepositoryNotify.UPDATE);
//        repo.request(notifys, opCreate, opUpdate);
//    }


//    public void removeAlarm(long pkNotify, RepositoryNotify.Listener lister) {
//        RepositoryNotify.CrudOperation opDelete = new RepositoryNotify.DeleteOperation();
//        RepositoryNotify.CrudOperation opRead = new RepositoryNotify.ReadOperation() {
//            @Override
//            public NotificationModel[] afterDbOperation(NotificationModel... notifications) {
//                if ((null != notifications) && (0 != notifications.length)) {
//                    NotificationModel not = notifications[0];
//                    stopMediaServiceById(not.pk);
//                    ReminderUtils.cancelReminder(context, receiver, not);
//                    Log.d("AlarmCommander", "Remove Remind: " + not);
//                    return notifications;
//                } else {
//                    return null;
//                }
//            }
//        };
//        repo.request(lister, new NotificationModel(pkNotify), opRead, opDelete);
//    }
//
//    public void removeAlarm(long pkNotify) {
//        removeAlarm(pkNotify, null);
//    }
//
//
//    public void clearAll(RepositoryNotify.Listener listener) {
//        RepositoryNotify.CrudOperation opRead = new RepositoryNotify.ReadOperation() {
//            @Nullable
//            @Override
//            public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
//                if ((null != notifications) && (0 != notifications.length)) {
//                    for (NotificationModel notification : notifications) {
//                        stopMediaServiceById(notification.pk);
//                        ReminderUtils.cancelReminder(context, receiver, notification);
//                        Log.d("AlarmCommander", "Remove Remind: " + notification);
//                    }
//                    return new NotificationModel[]{null};
//                } else {
//                    Log.d("AlarmCommander", "NO Remind was removed!");
//                    return null;
//                }
//            }
//        };
//
//        RepositoryNotify.CrudOperation op = new RepositoryNotify.DeleteOperation();
//
////        repo.deleteAll(op);
//        repo.request(listener, new NotificationModel[1], opRead, op);
//    }
//
//
//    public void clearDBExpired(RepositoryNotify.Listener listener) {
////        RepositoryNotify.CrudOperation opRead = new RepositoryNotify.SimpleCrudOperation(RepositoryNotify.READ);
//        RepositoryNotify.CrudOperation opRead = new RepositoryNotify.ReadOperation() {
//            @Nullable
//            @Override
//            public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
//                if ((null == notifications) || (0 == notifications.length)) {
//                    Log.d("AlarmCommander", "clearDBExpired No Removed!");
//                    return null;
//                } else {
//                    Log.d("AlarmCommander", "clearDBExpired Remove: " + Arrays.toString(notifications));
//                    return notifications;
//                }
//            }
//        };
//        RepositoryNotify.CrudOperation opDelete = new RepositoryNotify.DeleteOperation() {
//            @Nullable
//            @Override
//            public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
//                return new NotificationModel[0];
//            }
//        };
//        NotificationModel notication = new NotificationModel();
//        notication.status = NotificationStatus.EXPIRED;
//        repo.request(listener, notication, opRead, opDelete);
//    }
//
//    public void clearDBExpired() {
//        clearDBExpired(null);
//    }
//
////    private NotificationModel[] checkExpiredNotify(NotificationModel[] notifications) {
////        int newSize = 0;
////        long now = Calendar.getInstance().getTimeInMillis();
////        for (int i = 0, size = notifications.length; i < size; i++) {
////            if (NotificationStatus.EXPIRED != notifications[i].status) {
////                if (now >= notifications[i].starting) {
////                    notifications[i].status = NotificationStatus.EXPIRED;
////                    newSize++;
////                    Log.d("AlarmCommander", "Notification expired! " + notifications[i]);
////                } else {
////                    notifications[i] = null;
////                }
////            } else {
////                notifications[i] = null;
////            }
////        }
////        if (0 == newSize) {
////            return null;
////        }
////
////        NotificationModel[] returnList = new NotificationModel[newSize];
////        ArrayUtils.nonNullArray(notifications, returnList);
////        return returnList;
////    }
//
//
//    public void disableAllActive(RepositoryNotify.Listener listener) {
//        RepositoryNotify.CrudOperation opRead = new RepositoryNotify.ReadOperation() {
//            @Nullable
//            @Override
//            public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
//                if ((null != notifications) && (0 != notifications.length)) {
//                    long now = Calendar.getInstance().getTimeInMillis();
//                    int newSize = 0;
//                    for (int i = 0, size = notifications.length; i < size; i++) {
//                        if (NotificationStatus.EXPIRED != notifications[i].status) {
//                            if (now >= notifications[i].starting) {
//                                notifications[i].status = NotificationStatus.EXPIRED;
//                                Log.d("AlarmCommander", "Update status EXPIRED to: " + notifications[i]);
//                            } else {
//                                stopMediaServiceById(notifications[i].pk);
//                                ReminderUtils.cancelReminder(context, receiver, notifications[i]);
//                                notifications[i].status = NotificationStatus.DISABLED;
//                                Log.d("AlarmCommander", "Disable Remind: " + notifications[i]);
//                            }
//                            newSize++;
//                        } else {
//                            // no update need
//                            notifications[i] = null;
//                        }
//                    }
//                    if (0 == newSize) {
//                        Log.w("AlarmCommander", "NO Remind was disabled!");
//                        return null;
//                    } else {
//                        return ArrayUtils.nonNullArray(notifications, new NotificationModel[newSize]);
//                    }
//                } else {
//                    Log.w("AlarmCommander", "NO Remind was disabled!");
//                    return null;
//                }
//            }
//        };
//
//        RepositoryNotify.CrudOperation opUpdate = new RepositoryNotify.UpdateOperation();
//        repo.request(listener, (NotificationModel) null, opRead, opUpdate);
//    }
//
//    public void disableAllActive() {
//        disableAllActive(null);
//    }
//

//
//    private boolean checkExpired(NotificationModel notify, long now) {
//        if (now < notify.starting) {
//            notify.status = NotificationStatus.SCHEDULED;
//            return false;
//        } else {
//            notify.status = NotificationStatus.EXPIRED;
//            return true;
//        }
//    }

}
