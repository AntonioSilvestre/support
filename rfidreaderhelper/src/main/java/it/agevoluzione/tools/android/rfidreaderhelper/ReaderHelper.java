package it.agevoluzione.tools.android.rfidreaderhelper;


import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thingmagic.AndroidUsbReflection;
import com.thingmagic.ReadExceptionListener;
import com.thingmagic.ReadListener;
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.SerialReader;
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

    private volatile int currentStatus;
    private Reader reader;
    private UsbDevice usbDevice;
    private boolean androidUsbReflectionSetted;
    private UsbConnectionMonitor usbConnectionMonitor;

    private AsyncConfigReaderTask asyncTaskConfig;
//    private AsyncReader asyncTaskReader;
//    private ExecutorService executorService;
//    private Future future;

//    Listeners
//      My Listeners
    private ReadExceptionListener myErrorListener;
    private ReadListener myReaderListener;
//      User Listeners
    private StatusListener statusListener;
    private ErrorListener errorListener;
    private ReaderListener readerListener;

    @Override
    public void init(final Context context) {
        synchronized (this) {
            setStatus(INITIALIZED);
//            usbManager = AndroidUtils.usbManager(context);
            generateInternalListner();
            configureUsbConnectionMonitor(context);
        }
    }

    public void connect(Context context) throws Exception {
        connect(context, new ReaderConfiguratorImpl());
    }

    @Override
    public void connect(Context context, ReaderConfigurator configurator) throws Exception {
//        synchronized (this) {
//            canOperateWithExeption(USB_DEVICE_AUTHORIZATION_GRANT);
//            androidUsbReflection = ReaderUtils.setupUsbManagerWithFtdi(context, usbDevice);
//            reader = ReaderUtils.connect(usbDevice);
//            tryUpdateStatus(READER_CONNECTING);
//
//            canOperateWithExeption(READER_CONNECTED);
//            configurator.configure(reader);
//            tryUpdateStatus(READER_CONFIGURED);
//        }
        synchronized (this) {
            canOperateWithExeption(READER_CONNECTING);
            if (null == asyncTaskConfig || AsyncTask.Status.RUNNING != asyncTaskConfig.getStatus()) {
                asyncTaskConfig = new AsyncConfigReaderTask(this)
                        .setTimeout(3000)
                        .setConfigurator(configurator);
                asyncTaskConfig.execute(context);
            }

        }



//        if (null != asyncTaskConfig && AsyncTask.Status.RUNNING != asyncTaskConfig.getStatus()) {
//            asyncTaskConfig.cancel(true);
//            while (asyncTaskConfig.isCancelled()) {
//                Thread.sleep(100);
//                System.out.println("WAIT WAIT WAIT");
//            }
//        }
//        asyncTaskConfig = new AsyncConfigReaderTask().setBind(this).setTimeout(3000).setConfigurator(configurator);


//        if (null == executorService) {
//            executorService = Executors.newFixedThreadPool(2);
//        }
//        if (null == future || future.isDone()) {
//            future = executorService.submit(new BgReaderConfigureTask(this)
//                    .setContext(context)
//                    .setConfigurator(configurator)
//                    .setTimeout(3000L));
//        }

    }

//    private class BgOperationConnect implements Callable<Reader> {
//        Context context;
//
//        public BgOperationConnect(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        public Reader call() throws Exception {
//            return createReader(context);
//        }
//
//    }
//
//    private class BgOperationConfig implements Callable<Reader> {
//        Future<Reader> connectFuture;
//        ReaderConfigurator configurator;
//        long timeout;
//
//        public BgOperationConfig(Future<Reader> connectFuture, ReaderConfigurator configurator, long timeout) {
//            this.connectFuture = connectFuture;
//            this.configurator = configurator;
//            this.timeout = timeout;
//        }
//
//        @Override
//        public Reader call() throws Exception {
//            try {
//                try {
//                    Reader reader = connectFuture.get(timeout, TimeUnit.MILLISECONDS);
//                    configureConnectedReader(reader, configurator);
//                    return reader;
//                } catch (TimeoutException e) {
//                    connectFuture.cancel(true);
//                    updatetErrorListener(new Exception("Rfid reader may not be attached"));
//                    tryUpdateStatus(USB_DEVICE_AUTHORIZATION_GRANT);
//                }
//            } catch (Exception e) {
//                updatetErrorListener(e);
//            }
//            return null;
//        }
//    }

    @Override
    public void startReading() throws Exception {
        synchronized (this) {
            canOperateWithExeption(READING);
//            if (null == asyncTaskReader || AsyncTask.Status.RUNNING != asyncTaskReader.getStatus()) {
//                asyncTaskReader = new AsyncReader(this).setReadTimeout(150);
//                asyncTaskReader.execute(AsyncReader.READ);
//            }
            reader.startReading();
            tryUpdateStatus(READING);
        }
    }

    @Override
    public void stopReading() {
        synchronized (this) {
            if (isReading()) {
//                if (null == asyncTaskReader || AsyncTask.Status.RUNNING != asyncTaskReader.getStatus()) {
//                    asyncTaskReader = new AsyncReader(this);
//                    asyncTaskReader.execute(AsyncReader.STOP);
//                }
//                getReader().stopReading();

                try {
                    if (((SerialReader)reader).stopReading()) {
                        tryUpdateStatus(READER_CONFIGURED);
                    } else {
                        updatetErrorListener(new Exception("Reader Not Stopped!"));
                    }
                } catch (Exception e) {
                    updatetErrorListener(e);
                }
            }
        }
    }

    @Override
    public void disconnect(@NonNull Context context) {
        if (isReading()) {
            stopReading();
        }
        synchronized (this) {

            if (androidUsbReflectionSetted) {
                AndroidUsbReflection.close();
                androidUsbReflectionSetted = false;
            }

            if (null != reader) {
                reader.removeReadExceptionListener(myErrorListener);
                reader.removeReadListener(myReaderListener);
                ReaderUtils.close(reader);
                reader = null;
            }

//            if (null != future) {
//                future.cancel(true);
//            }
//
//            if(null != executorService) {
//                executorService.shutdownNow();
//            }

            if (null != usbDevice) {
                UsbUtils.closeUsbConnection(context, usbDevice);
            }
            try {
                tryUpdateStatus(USB_DEVICE_ATTACHED_NOT_AUTHORIZED);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void close(Context context) {
        disconnect(context);

        synchronized (this) {

            if (null != usbConnectionMonitor) {
                context.unregisterReceiver(usbConnectionMonitor);
                usbConnectionMonitor.close();
                usbConnectionMonitor = null;
            }

            usbDevice = null;

            myErrorListener = null;
            myReaderListener = null;

            statusListener = null;
            errorListener = null;
            readerListener = null;

//            usbManager = null;

//            executorService = null;
//            future = null;

            setStatus(NOT_INITIALIZED);
        }
    }

//    private Reader connectedReader(Context context) throws Exception {
//        canOperateWithExeption(USB_DEVICE_AUTHORIZATION_GRANT);
//        if (!androidUsbReflectionSetted) {
//            ReaderUtils.setupUsbManagerWithFtdi(context, usbDevice);
//            androidUsbReflectionSetted = true;
//        }
//        Reader reader = ReaderUtils.connect(usbDevice);
//        tryUpdateStatus(READER_CONNECTING);
//        return reader;
//    }
//
//    private void configureConnectedReader(Reader reader, ReaderConfigurator configurator) throws Exception {
//        canOperateWithExeption(READER_CONNECTED);
//        configurator.configure(reader);
//        tryUpdateStatus(READER_CONFIGURED);
//    }


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

//    public void setExecutorService(ExecutorService executorService) {
//        this.executorService = executorService;
//    }

    private void configureUsbConnectionMonitor(final Context context) {
        if (null == usbConnectionMonitor) {
            usbConnectionMonitor = new UsbConnectionMonitor();
        }
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
                ReaderHelper.this.disconnect(context);
                setStatus(INITIALIZED);
            }
        });

        IntentFilter filter = UsbUtils.getIntenFilter();
        context.registerReceiver(usbConnectionMonitor, filter);

        usbConnectionMonitor.scanUsbDevices(context);
    }

    private Reader createReader(Context context) throws Exception {
        synchronized (this) {
            tryUpdateStatus(READER_CONNECTING);
            if (!androidUsbReflectionSetted) {
                ReaderUtils.setupUsbManagerWithFtdi(context, usbDevice);
                androidUsbReflectionSetted = true;
            }
            Reader reader = ReaderUtils.create(usbDevice);
            setListenerToReader(reader);
            return reader;
        }
    }

    private void connectToReader(Reader reader) throws Exception {
        synchronized (this) {
            canOperate(READER_CONNECTED);
            reader.connect();
            setReader(reader);
            tryUpdateStatus(READER_CONNECTED);
        }
    }

    private void configureConnectedReader(Reader reader, ReaderConfigurator configurator) throws Exception {
        synchronized (this) {
            canOperateWithExeption(READER_CONFIGURED);
            configurator.configure(reader);
            setReader(reader);
            tryUpdateStatus(READER_CONFIGURED);
        }
    }

    private Exception generateErrorForChangeStatusNotPermitted(int newStatusRequest) {
        return new Exception("Change Status Not permitted!\nTry to change status from:" + getStatusName(getStatus()) + " to:" + getStatusName(newStatusRequest));
    }

    public String getStatusName() {
        return getStatusName(getStatus());
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
        return "Unrecongnized";
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

    public int getStatus() {
        synchronized (this) {
            return currentStatus;
        }
    }

    private void setStatus(int status) {
        synchronized (this) {
            this.currentStatus = status;
            updateStatusListener(status);
        }
    }

    private void setReader(Reader reader) {
        synchronized (this) {
            this.reader = reader;
        }
    }


    private Reader getReader() {
        synchronized (this) {
            return this.reader;
        }
    }

    private void setUsbDevice(UsbDevice usbDevice) {
        synchronized (this) {
            this.usbDevice = usbDevice;
        }
    }

//    private UsbDevice getUsbDevice() {
//        synchronized (this) {
//            return usbDevice;
//        }
//    }

//    private boolean tryUpdateStatus(int newStatusProposed) {
//        if (canOperate(newStatusProposed)) {
//            setStatus(newStatusProposed);
//            return true;
//        }
////        updatetErrorListener(generateErrorForChangeStatusNotPermitted(newStatusProposed));
//        return false;
//    }

    private void tryUpdateStatus(int newStatusProposed) throws Exception {
        if (canOperate(newStatusProposed)) {
            setStatus(newStatusProposed);
        } else {
            Exception exception = generateErrorForChangeStatusNotPermitted(newStatusProposed);
//            updatetErrorListener(exception);
            throw exception;
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
        if (null != readerListener) {
            readerListener.onRead(tagReadData);
        }
    }

    private void generateInternalListner() {
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
        synchronized (reader) {
            reader.addReadListener(myReaderListener);
            reader.addReadExceptionListener(myErrorListener);
        }
    }

//    private static class AsyncReader extends AsyncTask<Integer,Integer,Exception> {
//
//        public static final int READ = 1;
//        public static final int STOP = 2;
//
//        private long readTimeout;
//        private Locker<Boolean> locker;
//        private ReaderHelper bind;
//
//        public AsyncReader(ReaderHelper bind) {
//            this.bind = bind;
//            locker = new Locker<>();
//            readTimeout = 100;
//        }
//
//        public AsyncReader setReadTimeout(long readTimeout) {
//            this.readTimeout = readTimeout;
//            return this;
//        }
//
//        @Override
//        protected Exception doInBackground(Integer... actions) {
//            Exception err = null;
//            try {
//                int action = actions[0];
//
//                switch (action) {
//                    case READ:
//                        locker.setPayload(true);
//                        bind.getReader().startReading();
//                        publishProgress(READING);
////                        while (locker.getPayload()) {
////                            locker.lock(readTimeout);
////                        }
//                        break;
//                    case STOP:
//                        bind.getReader().stopReading();
//                        publishProgress(READER_CONFIGURED);
//                        break;
//                }
//            } catch (Exception e) {
//                err = e;
//            }
//            return err;
//        }
//
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//            int status = values[0];
//            try {
//                bind.tryUpdateStatus(status);
//            } catch (Exception e) {
//                bind.updatetErrorListener(e);
//            }
//        }
//
//        @Override
//        protected void onCancelled(Exception e) {
//            super.onCancelled(e);
//            bind.updatetErrorListener(e);
//            try {
//                bind.tryUpdateStatus(READER_CONFIGURED);
//            } catch (Exception ignored) {}
//            close();
//        }
//
//        @Override
//        protected void onPostExecute(Exception e) {
//            super.onPostExecute(e);
//            if(null != e) {
//                bind.updatetErrorListener(e);
//            }
//            close();
//        }
//
//        private void close() {
//            locker.unlockAll();
//            locker = null;
//            bind = null;
//        }
//
//        public void stop() {
//            locker.setPayload(false);
//            locker.unlockAll();
//        }
//    }


    private static class AsyncConfigReaderTask extends AsyncTask<Context,Void,Exception> {
        private ReaderHelper bind;
        private ReaderConfigurator configurator;
        private long timeout;

        private Future<Reader> future;
        private ExecutorService connectorService;

        public AsyncConfigReaderTask(ReaderHelper bind) {
            connectorService = Executors.newSingleThreadExecutor();
            this.bind = bind;
        }

        private void close() {
            bind = null;
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

        public AsyncConfigReaderTask setConfigurator(ReaderConfigurator configurator) {
            this.configurator = configurator;
            return this;
        }

        public AsyncConfigReaderTask setTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
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

        @Override
        protected Exception doInBackground(Context... contexts) {
            Exception err = null;
            Reader connectedReader = null;
            try {

                if (null == configurator) {
                    throw new Exception("Configurator pass are Null");
                }

                if (null == contexts || 1 > contexts.length) {
                    throw new Exception("Contexts pass are Null");
                }

                final Context context = contexts[0];
                future = connectorService.submit(new Callable<Reader>() {
                    @Override
                    public Reader call() throws Exception {
                        Reader reader = bind.createReader(context);
                        bind.connectToReader(reader);
                        return reader;
                    }
                });

                connectedReader = future.get(timeout,TimeUnit.MILLISECONDS);

                bind.configureConnectedReader(connectedReader, configurator);

            } catch (ExecutionException e) {
                err = e;
            } catch (InterruptedException e) {
                connectorService.shutdownNow();
                Thread.currentThread().interrupt();
                err = e;
            } catch (TimeoutException e) {
                err = new Exception("Rfid reader may not be attached");
            } catch (Exception e) {
                err = e;
            }

            return err;
        }



    }

}
