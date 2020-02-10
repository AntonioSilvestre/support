package it.agevoluzione.tools.android.room;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import it.agevoluzione.tools.android.model.NotificationModel;
import it.agevoluzione.tools.android.utils.ArrayUtils;

public class RepositoryNotify {

    final public static int CREATE = 1;
    final public static int READ = 2;
    final public static int UPDATE = 3;
    final public static int DELETE = 4;
//    final private static int READ_ALL = 5;
//    final private static int DELETE_ALL = 6;

    private DaoNotify daoNotify;

    public RepositoryNotify(Context context) {
        DatabaseNotify db = DatabaseNotify.getDatabase(context);
        daoNotify = db.notificationDao();
    }

    public interface Listener {
        void onResult(NotificationModel[] notifications);
        void onError(CrudOperation op, Throwable t);
    }


//    public void request(NotificationModel notificationModel, int crud) {
//        new AsyncOperationReturnSingle(daoNotify)
//                .setNotify(notificationModel)
//                .execute(noPostOperation(crud));
//    }

    /**
     * Send a sql request
     *
     * @param notificationModel passando null verranno fatto richieste a tutti gli elementi del <br>NB: db attenzione nel delete!
     * @param crudOperation     CREATE READ UPDATE DELETE
     * @param operation         operazione post db
     */
    public void request(@Nullable NotificationModel notificationModel, int crudOperation, Operation operation) {
        request(new NotificationModel[]{notificationModel}
                , generateCrudOperation(crudOperation, operation));
    }

    /**
     * Send a sql request
     *
     * @param listener
     * @param notificationModel
     * @param crudOperation
     * @param operation
     */
    public void request(Listener listener, @Nullable NotificationModel notificationModel, int crudOperation, Operation operation) {
        request(listener, new NotificationModel[]{notificationModel}
                , generateCrudOperation(crudOperation, operation));
    }

    /**
     * Send a sql request
     *
     * @param notificationModel passando null verranno fatto richieste a tutti gli elementi del <br>NB: db attenzione nel delete!
     * @param operations        operazioni post db per semplicita utilizzare gli Helper CreateOperation ReadOperation UpdateOperation DeleteOperation
     */
    public void request(@Nullable NotificationModel notificationModel, CrudOperation... operations) {
        request(new NotificationModel[]{notificationModel}, operations);
    }

    /**
     * Send a sql request
     *
     * @param listener          listener operation
     * @param notificationModel passando null verranno fatto richieste a tutti gli elementi del <br>NB: db attenzione nel delete!
     * @param operations        operazioni post db per semplicita utilizzare gli Helper CreateOperation ReadOperation UpdateOperation DeleteOperation
     */
    public void request(Listener listener, @Nullable NotificationModel notificationModel, CrudOperation... operations) {
        request(listener, new NotificationModel[]{notificationModel}, operations);
    }

    /**
     * Send a sql request
     *
     * @param notifications attenzione passando null o un arrey di 0 non verranno eseguite le operations
     * @param operations    operazioni post db per semplicita utilizzare gli Helper CreateOperation ReadOperation UpdateOperation DeleteOperation
     */
    public void request(@NonNull NotificationModel[] notifications, CrudOperation... operations) {
        new AsyncOperation(daoNotify)
                .setGoThrough(notifications)
                .execute(operations);
    }

    /**
     * Send a sql request
     *
     * @param notifications attenzione passando null o un arrey di 0 non verranno eseguite le operations
     * @param operations    operazioni post db per semplicita utilizzare gli Helper CreateOperation ReadOperation UpdateOperation DeleteOperation
     */
    public void request(Listener listener, @NonNull NotificationModel[] notifications, CrudOperation... operations) {
        if (null == listener) {
            new AsyncOperation(daoNotify)
                    .setGoThrough(notifications)
                    .execute(operations);
        } else {
            new AsyncOperation(daoNotify)
                    .addListener(listener)
                    .setGoThrough(notifications)
                    .execute(operations);
        }
    }

//    public void deleteAll(Operation operation) {
//        new AsyncOperation(daoNotify)
//                .execute(generateCrudOperation(DELETE_ALL, operation));
//    }
//
//    public void readAll(Operation operation) {
//        new AsyncOperation(daoNotify)
//                .execute(generateCrudOperation(READ_ALL, operation));
//    }

    public static CrudOperation generateCrudOperation(final int index, final Operation operation) {
        return new CrudOperation() {
            @Override
            public int crudAction() {
                return index;
            }

            @Override
            public NotificationModel[] afterDbOperation(NotificationModel... notifications) throws Exception{
                return operation.afterDbOperation(notifications);
            }
        };
    }

//    public static CrudOperation simpleCrudOperation(final int index) {
//        Operation operation =  new Operation() {
//            @Nullable
//            @Override
//            public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) {
//                return notifications;
//            }
//        };
//        return generateCrudOperation(index, operation);
//    }

//    public static NotificationModel[] generateArray(NotificationModel... notificationModels) {
//        return notificationModels;
//    }

    private static class PoolListener implements Listener {

        private PoolListener pool;
        private Listener listener;

        public void add(Listener list) {
            if (null == listener) {
                listener = list;
            } else {
                if (null == pool) {
                    pool = new PoolListener();
                }
                pool.add(list);
            }
        }

        void clear() {
            pool = null;
            listener = null;
        }

        @Override
        public void onResult(NotificationModel[] notifications) {
            if (null != listener) {
                listener.onResult(notifications);
                if (null != pool) {
                    pool.onResult(notifications);
                }
            }
        }

        @Override
        public void onError(CrudOperation op, Throwable t) {
            if (null != listener) {
                listener.onError(op, t);
                if (null != pool) {
                    pool.onError(op, t);
                }
            }
        }
    }


    private static class AsyncOperation extends AsyncTask<CrudOperation, Void, NotificationModel[]> {

        static class NotificationsWrapper {
            boolean valid;
            NotificationModel[] nots;
        }

        static class ErrorWrapper {
            public ErrorWrapper(Throwable throwable, CrudOperation crud) {
                this.crud = crud;
                this.throwable = throwable;
            }
            CrudOperation crud;
            Throwable throwable;
        }

        private DaoNotify asyncDao;
        private NotificationsWrapper goThrough;
        private PoolListener listener;
        private ErrorWrapper throwable;

        AsyncOperation(DaoNotify dao) {
            this.asyncDao = dao;
            listener = new PoolListener();
            goThrough = new NotificationsWrapper();
        }

        AsyncOperation addListener(Listener listner) {
            listener.add(listner);
            return this;
        }


        AsyncOperation setGoThrough(NotificationModel[] notificationModels) {
            this.goThrough.nots = notificationModels;
            return this;
        }

        @Override
        protected NotificationModel[] doInBackground(CrudOperation... operations) {
            CrudOperation operr = null;
            try {
                for (CrudOperation op : operations) {
                    operr = op;
                    if ((null == goThrough.nots) || (0 == goThrough.nots.length)) {
                        return null;
                    }
                    NotificationModel[] crudResult = executeCrud(op.crudAction(), goThrough.nots);

                    // Se il risultato della crudOperation è null vuol dire che è stato cancellato
                    // o non ha trovato nulla percui non ce nessun altra crudOperation da eseguire e
                    // ritorno il vecchio stato "goThrough" cioe quello della operation precedente
                    if ((null == crudResult) || (0 == crudResult.length)) {
                        if (goThrough.valid) {
                            return goThrough.nots;
                        } else {
                            return null;
                        }
                    }

                    // Se l operazione esterna mi ritorna null vuol dire che non devo eseguire piu nulla inAsync
                    // ritorno lo stato piu aggiornato che ho "crudResult" altrimenti passo all operation successiva
                    // il risultato  "goThrough = toPass"
                    NotificationModel[] toPass = op.afterDbOperation(crudResult);
                    if ((null == toPass) || (0 == toPass.length)) {
                        return null;
                    } else {
                        goThrough.nots = toPass;
                        goThrough.valid = true;
                    }
                }
            } catch (Exception e) {
                throwable = new ErrorWrapper(e, operr);
            }
            return goThrough.valid ? goThrough.nots : null;
        }

        @Override
        protected void onPostExecute(NotificationModel[] notificationModels) {
            if (null != listener) {
                if (null == throwable) {
                    listener.onResult(notificationModels);
                } else {
                    listener.onError(throwable.crud, throwable.throwable);
                }
            }
            super.onPostExecute(notificationModels);
        }

        @Override
        protected void onCancelled(NotificationModel[] notificationModels) {
            if (null != listener) {
                if (null != throwable) {
                    listener.onError(throwable.crud, throwable.throwable);
                } else {
                    listener.onResult(notificationModels);
                }
            }
            super.onCancelled(notificationModels);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if ((null != listener) && (null != throwable)) {
                listener.onError(throwable.crud, throwable.throwable);
            }
        }

        @Nullable
        private NotificationModel[] onlyWithId(@NonNull NotificationModel[] notificationModels){
            for (int i = 0; i < notificationModels.length; i++) {
                if (null == notificationModels[i].pk) {
                    Log.w("RepositoryNotify", "notify with null id" + notificationModels[i]);
                    notificationModels[i] = null;
                }
            }
            int size = ArrayUtils.arraySize(notificationModels);
            NotificationModel[] newNots = new NotificationModel[size];
            return ArrayUtils.nonNullArray(notificationModels, newNots);
        }

        @Nullable
        private NotificationModel[] executeCrud(int crud, NotificationModel... notificationModels) {
            if ((null == notificationModels) || (notificationModels.length == 0)) {
                return null;
            }

            int typeSelector = getReadOperation(notificationModels[0]);
            Long[] ids = getReadIndexes(typeSelector, notificationModels);

            switch (crud) {
                case CREATE:
                    if (ArrayUtils.arraySize(notificationModels) == 1) {
                        notificationModels[0].pk = asyncDao.insert(notificationModels[0]);
                    } else {
                        // todo verificare all non null
                        Long[] pks = asyncDao.insertAll(notificationModels);
                        for (int i = 0; i < pks.length; i++) {
                            notificationModels[i].pk = pks[i];
                        }
                    }
                    break;
                case READ:

                    switch (typeSelector) {
                        case 1:
                            notificationModels = asyncDao.readByIds(ids);
                            break;
                        case 2:
                            notificationModels = asyncDao.readByType(ArrayUtils.LongToInt(ids));
                            break;
                        case 3:
                            notificationModels = asyncDao.readByStatus(ArrayUtils.LongToInt(ids));
                            break;
                        case 4:
                            notificationModels = asyncDao.readByPkObj(ids);
                            break;
                        case 5:
                            notificationModels = asyncDao.readAll();
                            break;
                        case 6:
                            Long[][] statsObs = ArrayUtils.splitLong(ids);
                            Integer[] stats = ArrayUtils.LongToInt(statsObs[0]);
                            notificationModels = asyncDao.readWithStatusAndObj(stats, statsObs[1]);
                            break;
                        case 7:
                            Long[][] typesObs = ArrayUtils.splitLong(ids);
                            Integer[] types = ArrayUtils.LongToInt(typesObs[0]);
                            notificationModels = asyncDao.readWithTypeAndObj(types, typesObs[1]);
                            break;
                    }

                    break;
                case UPDATE:
                    NotificationModel[] newNots = onlyWithId(notificationModels);
                    asyncDao.update(newNots);
                    notificationModels = newNots;
                    break;
                case DELETE:

                    switch (typeSelector) {
                        case 1:
                            asyncDao.deleteByIds(ids);
                            break;
                        case 2:
                            asyncDao.deleteByType(ArrayUtils.LongToInt(ids));
                        case 3:
                            asyncDao.deleteByStatuses(ArrayUtils.LongToInt(ids));
                            break;
                        case 4:
                            asyncDao.deleteByPkObjs(ids);
                            break;
                        case 5:
                            asyncDao.deleteAll();
                            break;
                        case 6:
                            Long[][] statsObs = ArrayUtils.splitLong(ids);
                            Integer[] statIds = ArrayUtils.LongToInt(statsObs[0]);
                            asyncDao.deleteWithStatusAndObj(statIds, statsObs[1]);
                            break;
                        case 7:
                            Long[][] typesObs = ArrayUtils.splitLong(ids);
                            Integer[] typeIds = ArrayUtils.LongToInt(typesObs[0]);
                            asyncDao.deleteWithTypeAndObj(typeIds, typesObs[1]);
                            break;
                    }

                    notificationModels = null;

                    break;
//                case READ_ALL:
//                    notificationModels = asyncDao.readAll();
//                    break;
//                case DELETE_ALL:
//                    asyncDao.deleteAll();
//                    notificationModels = null;
                default:
                    Log.e("RepositoryNotify", "No crud Action was select");
            }
            return notificationModels;
        }


        @Nullable
        private Long[] getReadIndexes(int index, NotificationModel... notificationModels) {
            if (!((index == 1) || (index == 2) || (index == 3) || (index == 4) || (index == 6) || (index == 7))) {
                return null;
            }

            int size = notificationModels.length;
            Long[] ids = new Long[size];

            for (int i = 0; i < size; i++) {
                NotificationModel notificationModel = notificationModels[i];

                if (null != notificationModel) {
                    switch (index) {
                        case 1:
                            ids[i] = notificationModel.pk;
                            break;
                        case 2:
                            ids[i] = Long.valueOf(notificationModel.type);
                            break;
                        case 3:
                            ids[i] = Long.valueOf(notificationModel.status);
                            break;
                        case 4:
                            ids[i] = notificationModel.pk_obj;
                            break;
                        case 6:
                            if (0 == i) {
                                ids = new Long[size*2];
                            }
                            ids[i] = Long.valueOf(notificationModel.status);
                            ids[i+size] = notificationModel.pk_obj;
                            break;
                        case 7:
                            if (0 == i) {
                                ids = new Long[size*2];
                            }
                            ids[i] = Long.valueOf(notificationModel.type);
                            ids[i+size] = notificationModel.pk_obj;
                            break;
                    }
                }
            }
            int returnSize = ArrayUtils.arraySize(ids);

            return (returnSize == 0) ? null : ArrayUtils.nonNullArray(ids, new Long[returnSize]);
        }


        /**
         * @param notify
         * @return 1 for pk, 2 for type, 3 for status, 4 for pk_obj, 5 for all, 6 for status\obj, 7 for type\obj and -1 for unhandled
         */
        private int getReadOperation(NotificationModel notify) {
            if (null == notify) {
                return 5;
            } else if (null != notify.pk) {
                return 1;
            } else if (null != notify.type) {
                return (null == notify.pk_obj) ? 2 : 7;
            } else if (null != notify.status) {
                return (null == notify.pk_obj) ? 3 : 6;
            } else if (null != notify.pk_obj) {
                return 4;
            }
            return -1;
        }
    }

    /*
         LISTENER\OPERATION CLASSES
    */
    public interface Operation {
        /**
         * Metodo chiamato dopo aver eseguito l operazione CRUD sql. in cui viene passato come argomento il risultato della chiamata sql
         *
         * @param notifications risultato dell azione CRUD richiesta
         * @return NotificationModel[] che verrà utilizzato nel successivo crudOperation
         * <br>NB: Attenzione se si ritorna un NotificationModel[] null o di dimenzione 0 la seguente Operazione non verrà eseguita
         */
        @Nullable
        NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) throws Exception;
    }

    public interface CrudOperation extends Operation {
        int crudAction();
    }

    public static class CreateOperation extends SimpleCrudOperation {
        @Override
        public final int crudAction() {
            return CREATE;
        }
    }

    public static class ReadOperation extends SimpleCrudOperation {
        @Override
        public final int crudAction() {
            return READ;
        }
    }

    public static class UpdateOperation extends SimpleCrudOperation {
        @Override
        public final int crudAction() {
            return UPDATE;
        }
    }

    public static class DeleteOperation extends SimpleCrudOperation {
        @Override
        public final int crudAction() {
            return DELETE;
        }
    }

    /**
     * Esegue solo attività di db passando alla successiva Operation il risultato della CRUD eseguita
     */
    public abstract static class SimpleCrudOperation implements RepositoryNotify.CrudOperation {
        @Nullable
        @Override
        public NotificationModel[] afterDbOperation(@Nullable NotificationModel... notifications) throws Exception {
            return notifications;
        }
    }

}