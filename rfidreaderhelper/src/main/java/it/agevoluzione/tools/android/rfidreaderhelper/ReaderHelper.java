package it.agevoluzione.tools.android.rfidreaderhelper;


import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.thingmagic.AndroidUsbReflection;
import com.thingmagic.ReadExceptionListener;
import com.thingmagic.ReadListener;
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.TagReadData;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import it.agevoluzione.tools.android.usbconnectorhelper.UsbConnectionMonitor;
import it.agevoluzione.tools.android.usbconnectorhelper.UsbUtils;
import it.agevoluzione.tools.android.utils.Locker;

public final class ReaderHelper implements IReaderHelper {

//    public final static int NOT_INITIALIZED = 0;
//    public final static byte INITIALIZED = 1;

//    public final static int DEVICE_DETACHED
//    public final static byte USB_DEVICE_ATTACHED_NOT_AUTHORIZED = INITIALIZED << 1;

//    public final static int DEVICE_AUTHORIZATION_NOT_GRANT,
//    public final static byte USB_DEVICE_AUTHORIZATION_GRANT = INITIALIZED << 2;

//    public final static int READER_DISCONNECTED,
//    public final static byte READER_CONNECTING = INITIALIZED << 3;

//    public final static int READER_NOT_CONFIGURED,
//    public final static byte READER_CONNECTED = INITIALIZED << 4;

//    public final static int READER_CONFIGURED = INITIALIZED << 5;

//    public final static int READING = INITIALIZED << 6;
//    public final static int DEVICE_READING,


    public final static int NOT_INITIALIZED = 0;
    public final static int INITIALIZED = 1;
    public final static int USB_DEVICE_ATTACHED_NOT_AUTHORIZED = INITIALIZED << 1 | 1;
    public final static int USB_DEVICE_AUTHORIZATION_GRANT = USB_DEVICE_ATTACHED_NOT_AUTHORIZED << 1 | 1;
    public final static int READER_CONNECTING = USB_DEVICE_AUTHORIZATION_GRANT << 1 | 1;
    public final static int READER_CONNECTED = READER_CONNECTING << 1 | 1;
    public final static int READER_CONFIGURED = READER_CONNECTED << 1 | 1;
    public final static int READING = READER_CONFIGURED << 1 | 1;
    private final static int LAST = READING;

    private volatile int currentStatus;

    public void recheckPresence(Context context) {
        if (null != usbConnectionMonitor) {
            usbConnectionMonitor.scanUsbDevices(context);
        }
    }

    public interface StatusListener {
        void onStatusChange(int status);
    }

    public interface ErrorListener {
        void onError(Throwable throwable);
    }

    public interface ReaderListener {
        void onRead(TagReadData tagReadData);
    }

    private Reader connectedReader;
    private UsbDevice usbDevice;
    private boolean androidUsbReflectionSetted;
    private UsbConnectionMonitor usbConnectionMonitor;

    private AsyncConfigReaderTask asyncTaskConfig;
    private AsyncReaderTask asyncReaderTask;

//    Listeners
//      My Listeners
    private ReadExceptionListener myErrorListener;
    private ReadListener myReaderListener;
//      User Listeners
    private StatusListener statusListener;
    private ErrorListener errorListener;
    private ReaderListener readerListener;

    @Override
    public synchronized void init(final Context context) {
        synchronized (this) {
            setStatus(INITIALIZED);
//            usbManager = AndroidUtils.usbManager(context);
            generateInternalListener();
            configureUsbConnectionMonitor(context);
        }
    }

    public void connect(Context context) throws Exception {
        connect(context, new ReaderConfiguratorImpl());
    }

    @Override
    public synchronized void connect(Context context, ReaderConfigurator configurator) throws Exception {
        canOperateWithExeption(READER_CONNECTING);
//        Reader reader = getConnectedReader();
//        if (null != reader) {
//            configurator.configure(reader);
//        } else {
            synchronized (this) {
                if (null == asyncTaskConfig || AsyncTask.Status.RUNNING != asyncTaskConfig.getStatus()) {
                    asyncTaskConfig = new AsyncConfigReaderTask(this)
                            .setTimeout(2500)
                            .setConfigurator(configurator);
                    asyncTaskConfig.execute(context);
                } else {
                    throw new Exception("Connecting process already running");
                }
            }
//        }
    }
//    ExecutorService executorService;
//    Future future;
//    Reading reading;
    @Override
    public synchronized void startReading() throws Exception {
//        synchronized (this) {
//            canOperateWithExeption(READING);
//            reader.startReading();
//            tryUpdateStatus(READING);
//        }
        canOperateWithExeption(READING);
        synchronized (this) {
            if (null == asyncReaderTask || AsyncTask.Status.RUNNING != asyncReaderTask.getStatus()) {
                asyncReaderTask = new AsyncReaderTask(this);
                asyncReaderTask.execute(getConnectedReader());
            }
//                executorService = Executors.newSingleThreadExecutor();
//                reading = new Reading(this);
//                future = executorService.submit(reading);
//            }
        }
    }

//    private static class Reading implements Runnable {
//        private Locker<Boolean> locker;
//        private ReaderHelper bind;
//
//        private Reading(ReaderHelper reader) {
//            this.bind = reader;
//            locker = new Locker<>();
//        }
//
//        @Override
//        public void run() {
//            locker.setPayload(true);
//            bind.reader.startReading();
//            try {
//                bind.tryUpdateStatus(READING);
//                while (locker.getPayload()) {
//                    locker.lock(150L);
//                }
//                bind.reader.stopReading();
//                bind.tryUpdateStatus(READER_CONFIGURED);
//            } catch (Exception e) {
//                bind.updatetErrorListener(e);
//            } finally {
//                bind = null;
//                locker = null;
//            }
//        }
//
//        public void stop() {
//            locker.setPayload(false);
//            locker.unlockAll();
//        }
//
//
//    }

    @Override
    public synchronized void stopReading() {
        synchronized (this) {
            if (isReading()) {
                asyncReaderTask.stopReading();
            }
        }
    }

    @Override
    public synchronized void disconnect(@NonNull Context context) {
        if (isReading()) {
            stopReading();
        }
        synchronized(this) {
            if (androidUsbReflectionSetted) {
                AndroidUsbReflection.close();
                androidUsbReflectionSetted = false;
            }

            if (null != getConnectedReader()) {
                connectedReader.removeReadExceptionListener(myErrorListener);
                connectedReader.removeReadListener(myReaderListener);
//                ReaderUtils.close(reader);
                connectedReader.destroy();
                setConnectedReader(null);
            }

            if (null != usbDevice) {
                UsbUtils.closeUsbConnection(context, usbDevice);
                tryUpdateStatus(USB_DEVICE_ATTACHED_NOT_AUTHORIZED);
            } else {
                tryUpdateStatus(INITIALIZED);
            }
        }
    }

    @Override
    public synchronized void close(Context context) {
        disconnect(context);
        synchronized (this) {

//todo verificare se mettere qui o in stop reading
            if (null != asyncReaderTask) {
                asyncReaderTask.cancel(true);
                asyncReaderTask = null;
            }

            if (null != asyncTaskConfig) {
                asyncTaskConfig.cancel(true);
                asyncTaskConfig = null;
            }

            closeUsbConnectionMonitor(context);

            usbDevice = null;

            myErrorListener = null;
            myReaderListener = null;

//todo verificare se cancellare le interfacce dell utente qui o creare un destroy()
            statusListener = null;
            errorListener = null;
            readerListener = null;

            setStatus(NOT_INITIALIZED);
        }
    }

    public void setLifecycleOwner(AppCompatActivity appCompatActivity) {
        setLifecycleOwner(appCompatActivity, appCompatActivity);
    }

    public void setLifecycleOwner(LifecycleOwner lifeCycleOwner, final Context context) {
        lifeCycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                switch (event) {
                    case ON_CREATE:
                        break;
                    case ON_START:
                        configureUsbConnectionMonitor(context);
                        break;
                    case ON_RESUME:
                        break;
                    case ON_PAUSE:
                        break;
                    case ON_STOP:
                        disconnect(context);
                        closeUsbConnectionMonitor(context);
                        break;
                    case ON_DESTROY:
                        close(context);
                        source.getLifecycle().removeObserver(this);
                        break;
                    case ON_ANY:
                        break;
                }
            }
        });
    }

    public boolean isReading() {
        return getStatus() == READING;
    }

    public void setStatusListener(StatusListener statusListener) {
        this.statusListener = statusListener;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void setReaderListener(ReaderListener readerListener) {
        this.readerListener = readerListener;
    }

    public int getStatus() {
//        synchronized (ReaderHelper.class) {
            return currentStatus;
//        }
    }

    public String getStatusName(int status) {
        switch (status) {
            case NOT_INITIALIZED:
                return "NOT_INITIALIZED";
            case INITIALIZED:
                return "INITIALIZED";
            case USB_DEVICE_ATTACHED_NOT_AUTHORIZED:
                return "USB_DEVICE_ATTACHED_NOT_AUTHORIZED";
            case USB_DEVICE_AUTHORIZATION_GRANT:
                return "USB_DEVICE_AUTHORIZATION_GRANT";
            case READER_CONNECTING:
                return "READER_CONNECTING";
            case READER_CONNECTED:
                return "READER_CONNECTED";
            case READER_CONFIGURED:
                return "READER_CONFIGURED";
            case READING:
                return "READING";
        }
        return "Unrecongnized value "+status;
    }

    public String getStatusName() {
        return getStatusName(getStatus());
    }

    private void closeUsbConnectionMonitor(Context context) {
        if (null != usbConnectionMonitor) {
            context.unregisterReceiver(usbConnectionMonitor);
            usbConnectionMonitor.close();
            usbConnectionMonitor = null;
        }
    }

    private void configureUsbConnectionMonitor(final Context context) {
        if (null == usbConnectionMonitor) {
            usbConnectionMonitor = new UsbConnectionMonitor();
            usbConnectionMonitor.setFilter(ReaderUtils.getFTDIDeviceFilter());
            usbConnectionMonitor.setListener(new UsbConnectionMonitor.OnUsbConnectioListener() {
                @Override
                public void connect(@Nullable UsbDevice device) {
                    if (canOperate(USB_DEVICE_ATTACHED_NOT_AUTHORIZED)) {
                        setUsbDevice(device);
                        int newStatus = null == device ? INITIALIZED : USB_DEVICE_ATTACHED_NOT_AUTHORIZED;
                        setStatus(newStatus);
                    }
                }

                @Override
                public void granted(UsbDevice device, boolean granted) {
                    if (canOperate(USB_DEVICE_ATTACHED_NOT_AUTHORIZED)) {
                        setUsbDevice(device);
                        int newStatus = granted ? USB_DEVICE_AUTHORIZATION_GRANT : USB_DEVICE_ATTACHED_NOT_AUTHORIZED;
                        setStatus(newStatus);
                    }
                }

                @Override
                public void disconnect(UsbDevice device) {
                    setUsbDevice(null);
                    ReaderHelper.this.disconnect(context);
                }
            });

            IntentFilter filter = UsbUtils.getIntenFilter();
            context.registerReceiver(usbConnectionMonitor, filter);

        }
        usbConnectionMonitor.scanUsbDevices(context);
    }



    private Reader createReader(Context context) throws Exception {
//            tryUpdateStatus(READER_CONNECTING);
//        canOperateWithExeption(READER_CONNECTING);
        synchronized (this) {
            if (!androidUsbReflectionSetted) {
                ReaderUtils.setupUsbManagerWithFtdi(context, usbDevice);
                androidUsbReflectionSetted = true;
            }

            Reader reader = ReaderUtils.create(usbDevice);
            setListenerToReader(reader);
            return reader;
        }
    }

    private int connectToReader(Reader reader) throws Exception {
        reader.connect();
        setConnectedReader(reader);
        return READER_CONNECTED;
    }

    private int configureConnectedReader(Reader reader, ReaderConfigurator configurator) throws Exception {
//        synchronized (this) {
//            canOperateWithExeption(READER_CONFIGURED);
        if (null == reader) {
            throw new Exception("No Reader passed");
        } else if (null == configurator) {
            throw new Exception("No ReaderConfigurator passed");
        }

        configurator.configure(reader);
        setConnectedReader(reader);
//            tryUpdateStatus(READER_CONFIGURED);
        return READER_CONFIGURED;
//        }
    }

    private Exception generateErrorForChangeStatusNotPermitted(int newStatusRequest) {
        return new Exception("Change Status Not permitted!\nTry to change status from:" + getStatusName(getStatus()) + " to:" + getStatusName(newStatusRequest));
    }

    private boolean canOperate(int newStatusProposed) {
        int permitted = getStatus() << 1 | 1;
        return permitted >= newStatusProposed;
//        return permitted > newStatusProposed;
    }

    private void canOperateWithExeption(int newStatusProposed) throws Exception {
        if (!canOperate(newStatusProposed)) {
            throw generateErrorForChangeStatusNotPermitted(newStatusProposed);
        }
    }

    private void next() {
        int next = getStatus() << 1 | 1;
        setStatus(Math.min(next, LAST));
    }

    private void prev() {
        setStatus(getStatus() >> 1);
    }

    private void setStatus(int status) {
//        synchronized (this) {
        this.currentStatus = status;
        updateStatusListener(status);
//        }
    }

    private void setConnectedReader(Reader reader) {
        synchronized (this) {
            this.connectedReader = reader;
        }
    }

    @Nullable
    private Reader getConnectedReader() {
        synchronized (this) {
            return this.connectedReader;
        }
    }

    private void setUsbDevice(UsbDevice usbDevice) {
        synchronized (this) {
            this.usbDevice = usbDevice;
        }
    }

    private boolean tryUpdateStatus(int newStatusProposed) {
        try {
            tryUpdateStatusException(newStatusProposed);
            return true;
        } catch (Exception e) {
            updatetErrorListener(e);
            return false;
        }
    }

    private void tryUpdateStatusException(int newStatusProposed) throws Exception {
        if (canOperate(newStatusProposed)) {
            setStatus(newStatusProposed);
        } else {
            throw generateErrorForChangeStatusNotPermitted(newStatusProposed);
        }
    }

    private void updateStatusListener(int newStatus) {
        if (null != statusListener) {
            statusListener.onStatusChange(newStatus);
        }
    }

    private void updatetErrorListener(Throwable throwable) {
        if (null != errorListener) {
            errorListener.onError(throwable);
        }
    }

    private void updateReaderListener(TagReadData tagReadData) {
        if (null != readerListener && canOperate(READING)) {
            readerListener.onRead(tagReadData);
        }
    }

    private void generateInternalListener() {
        myReaderListener = new ReadListener() {
            @Override
            public void tagRead(Reader reader, TagReadData tagReadData) {
                updateReaderListener(tagReadData);
            }
        };
        myErrorListener = new ReadExceptionListener() {
            @Override
            public void tagReadException(Reader reader, ReaderException e) {
                updatetErrorListener(e);
            }
        };
    }

    private void setListenerToReader(Reader reader) {
        reader.addReadListener(myReaderListener);
        reader.addReadExceptionListener(myErrorListener);
    }

    private static abstract class AsyncTasks<E> extends AsyncTask<E,Integer,Exception> {
        protected ReaderHelper bind;

        public AsyncTasks(ReaderHelper bind) {
            this.bind = bind;
        }

        @Override
        protected void onCancelled(Exception e) {
            super.onCancelled(e);
            if (null != e) {
                bind.updatetErrorListener(e);
            }
            close();
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            if (null != e) {
                bind.updatetErrorListener(e);
            }
            close();
        }

        protected void close() {
            bind = null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int status = values[0];
            try {
                bind.tryUpdateStatusException(status);
            } catch (Exception e) {
                bind.updatetErrorListener(e);
            }
        }
    }

    private static class AsyncConfigReaderTask extends AsyncTasks<Context> {
        private ReaderConfigurator configurator;
        private long timeout;

        private Future<Reader> future;
        private ExecutorService connectorService;

        public AsyncConfigReaderTask(ReaderHelper bind) {
            super(bind);
//            connectorService = Executors.newSingleThreadExecutor();
            timeout = 2500L;
        }

        @Override
        protected void close() {
            super.close();
            configurator = null;
            if (null != future) {
                future.cancel(true);
                future = null;
            }

            if (null != connectorService) {
                connectorService.shutdownNow();
                while (!connectorService.isTerminated()) {
                    Log.d("Connector close", "WAIT WAIT WAIT connectorService");
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException ignored) {}
                }
                connectorService = null;
            }
        }

        AsyncConfigReaderTask setConfigurator(ReaderConfigurator configurator) {
            this.configurator = configurator;
            return this;
        }

        AsyncConfigReaderTask setTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }


        @Override
        protected Exception doInBackground(Context... contexts) {
            Exception err = null;
            Reader connectedReader = null;
            final Context context = contexts[0];

            try {

                connectedReader = bind.getConnectedReader();
                if (null != connectedReader) {
                    publishProgress(READER_CONNECTED);
                } else {
                    connectorService = Executors.newSingleThreadExecutor();
                    future = connectorService.submit(new Callable<Reader>() {
                        @Override
                        public Reader call() throws Exception {
                            bind.canOperateWithExeption(READER_CONNECTING);
                            publishProgress(READER_CONNECTING);
                            Reader reader = bind.createReader(context);
                            int stat = bind.connectToReader(reader);
                            publishProgress(stat);
                            return reader;
                        }
                    });

                    connectedReader = future.get(timeout, TimeUnit.MILLISECONDS);
                }
                int stat = bind.configureConnectedReader(connectedReader, configurator);
                publishProgress(stat);

            } catch (ExecutionException e) {
                err = e;
            } catch (InterruptedException e) {
                connectorService.shutdownNow();
                Thread.currentThread().interrupt();
                err = e;
            } catch (TimeoutException e) {
                err = new Exception("Rfid reader may not be attached to USB");
//                publishProgress(USB_DEVICE_ATTACHED_NOT_AUTHORIZED);
            } catch (Exception e) {
                err = e;
//                bind.setConnectedReader(null);
//                publishProgress(INITIALIZED);
            }
            if (null != err) {
                bind.disconnect(context);
            }
            return err;
        }

    }

    private static class AsyncReaderTask extends AsyncTasks<Reader>{

        private Locker<Boolean> locker;

        public AsyncReaderTask(ReaderHelper bind) {
            super(bind);
            locker = new Locker<>(false);
        }

        @Override
        protected Exception doInBackground(Reader... readers) {
            Exception err = null;
            locker.setPayload(true);
//            bind.reader.startReading();
            Reader reader = readers[0];
            try {
                reader.startReading();
                publishProgress(READING);
                while (locker.getPayload()) {
                    locker.lock(150L);
                }
                reader.stopReading();
                publishProgress(READER_CONFIGURED);
            } catch (Exception e) {
                err = e;
            } finally {
                locker = null;
            }
            return  err;
        }

        @Override
        protected void close() {
            super.close();
            locker = null;
        }

        public void stopReading() {
            locker.setPayload(false);
            locker.unlockAll();
        }
    }
}
