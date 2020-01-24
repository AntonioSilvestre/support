package it.agevoluzione.tools.android.rfidreaderhelper;


import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import androidx.annotation.Nullable;

import com.thingmagic.AndroidUsbReflection;
import com.thingmagic.ReadExceptionListener;
import com.thingmagic.ReadListener;
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.TagReadData;

import it.agevoluzione.tools.android.usbconnectorhelper.UsbConnectionMonitor;
import it.agevoluzione.tools.android.usbconnectorhelper.UsbUtils;
import it.agevoluzione.tools.android.utils.AndroidUtils;

public class ReaderHelper implements IReaderHelper {

//    public final static int NOT_INITIALIZED = 0;
//    public final static byte INITIALIZED = 1;

//    public final static int DEVICE_DETACHED
//    public final static byte DEVICE_ATTACHED_NOT_AUTHORIZED = INITIALIZED << 1;

//    public final static int DEVICE_AUTHORIZATION_NOT_GRANT,
//    public final static byte DEVICE_AUTHORIZATION_GRANT = INITIALIZED << 2;

//    public final static int READER_DISCONNECTED,
//    public final static byte READER_CONNECTED = INITIALIZED << 3;

//    public final static int READER_NOT_CONFIGURED,
//    public final static byte READER_CONFIGURED = INITIALIZED << 4;

//    public final static int DEVIDE_READY = INITIALIZED << 5;

//    public final static int READING = INITIALIZED << 6;
//    public final static int DEVICE_READING,

    private int currentStatus;

    public final static int NOT_INITIALIZED = 0;
    public final static int INITIALIZED = 1;
    public final static int DEVICE_ATTACHED_NOT_AUTHORIZED = INITIALIZED << 1 | 1;
    public final static int DEVICE_AUTHORIZATION_GRANT = DEVICE_ATTACHED_NOT_AUTHORIZED << 1 | 1;
    public final static int READER_CONNECTED = DEVICE_AUTHORIZATION_GRANT << 1 | 1;
    public final static int READER_CONFIGURED = READER_CONNECTED << 1 | 1;
    public final static int DEVIDE_READY = READER_CONFIGURED << 1 | 1;
    public final static int READING = DEVIDE_READY << 1 | 1;
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

    private Reader reader;
    private UsbDevice usbDevice;
    private AndroidUsbReflection androidUsbReflection;
    private UsbConnectionMonitor usbConnectionMonitor;

    //    Listeners
//      My Listeners
    private ReadExceptionListener myErrorListener;
    private ReadListener myReaderListener;
    //      User Listeners
    private StatusListener statusListener;
    private ErrorListener errorListener;
    private ReaderListener readerListener;

//    private UsbManager usbManager;

    @Override
    public void init(final Context context) {
        synchronized (this) {
            setStatus(INITIALIZED);
//            usbManager = AndroidUtils.usbManager(context);
            generateInternalListner();
            configureUsbConnectionMonitor(context);
        }
    }

    @Override
    public void connect(Context context, ReaderConfigurator configurator) throws Exception {
        synchronized (this) {
//            if (!canOperate(READER_CONNECTED)) {
//                int status = getStatus();
//                if (status == DEVICE_ATTACHED_NOT_AUTHORIZED || status == DEVICE_AUTHORIZATION_GRANT) {
//                   usbConnectionMonitor.scanUsbDevices(context);
//                }
//            } else {
            canOperateWithExeption(DEVICE_AUTHORIZATION_GRANT);
            androidUsbReflection = ReaderUtils.setupUsbManagerWithFtdi(context, usbDevice);
            reader = ReaderUtils.connect(usbDevice);
            tryUpdateStatus(READER_CONNECTED);

            canOperateWithExeption(READER_CONFIGURED);
            configurator.configure(reader);
            tryUpdateStatus(DEVIDE_READY);
//            }
        }
    }

    @Override
    public void startReading() throws Exception {
        synchronized (this) {
            canOperateWithExeption(READING);
            reader.startReading();
            tryUpdateStatus(READING);
        }
    }

    @Override
    public void stopReading() {
        synchronized (this) {
            if (isReading()) {
                reader.stopReading();
                tryUpdateStatus(DEVIDE_READY);
            }
        }
    }

    @Override
    public void disconnect(Context context) {
        if (isReading()) {
            stopReading();
        }
        synchronized (this) {

            if (null != androidUsbReflection) {
                AndroidUsbReflection.close();
                androidUsbReflection = null;
            }

            if (null != reader) {
                reader.removeReadExceptionListener(myErrorListener);
                reader.removeReadListener(myReaderListener);
                ReaderUtils.close(reader);
                reader = null;
            }

            if (null != usbDevice) {
                UsbUtils.closeUsbConnection(context, usbDevice);
            }
            tryUpdateStatus(DEVICE_ATTACHED_NOT_AUTHORIZED);
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

            setStatus(NOT_INITIALIZED);
        }
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

    private void configureUsbConnectionMonitor(Context context) {
        if (null == usbConnectionMonitor) {
            usbConnectionMonitor = new UsbConnectionMonitor();
        }
        usbConnectionMonitor.setFilter(ReaderUtils.getFTDIDeviceFilter());
        usbConnectionMonitor.setListner(new UsbConnectionMonitor.OnUsbConnectioListener() {
            @Override
            public void connect(@Nullable UsbDevice device) {
                if (canOperate(DEVICE_ATTACHED_NOT_AUTHORIZED)) {
                    setUsbDevice(device);
                    int newStatus = null == device ? INITIALIZED : DEVICE_ATTACHED_NOT_AUTHORIZED;
                    setStatus(newStatus);
                }
            }

            @Override
            public void granted(UsbDevice device, boolean granted) {
                if (canOperate(DEVICE_ATTACHED_NOT_AUTHORIZED)) {
                    setUsbDevice(device);
                    int newStatus = granted ? DEVICE_AUTHORIZATION_GRANT : DEVICE_ATTACHED_NOT_AUTHORIZED;
                    setStatus(newStatus);
                }
            }

            @Override
            public void disconnect(UsbDevice device) {
                setStatus(INITIALIZED);
                ReaderHelper.this.disconnect(null);
            }
        });

        IntentFilter filter = UsbUtils.getIntenFilter();
        context.registerReceiver(usbConnectionMonitor, filter);

        usbConnectionMonitor.scanUsbDevices(context);
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
            case DEVICE_ATTACHED_NOT_AUTHORIZED:
                return "DEVICE_ATTACHED_NOT_AUTHORIZED";
            case DEVICE_AUTHORIZATION_GRANT:
                return "DEVICE_AUTHORIZATION_GRANT";
            case READER_CONNECTED:
                return "READER_CONNECTED";
            case READER_CONFIGURED:
                return "READER_CONFIGURED";
            case DEVIDE_READY:
                return "DEVIDE_READY";
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

    private void setUsbDevice(UsbDevice usbDevice) {
        synchronized (this) {
            this.usbDevice = usbDevice;
        }
    }

    private UsbDevice getUsbDevice() {
        synchronized (this) {
            return usbDevice;
        }
    }

    private boolean tryUpdateStatus(int newStatusProposed) {
        if (canOperate(newStatusProposed)) {
            setStatus(newStatusProposed);
            return true;
        }
        updatetErrorListener(generateErrorForChangeStatusNotPermitted(newStatusProposed));
        return false;
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
}
