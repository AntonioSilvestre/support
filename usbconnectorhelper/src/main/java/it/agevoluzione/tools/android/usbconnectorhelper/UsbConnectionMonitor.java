package it.agevoluzione.tools.android.usbconnectorhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UsbConnectionMonitor extends BroadcastReceiver {

//    private final static String ACTION_USB_PERMISSION = "it.agevoluzione.USB_PERMISSION";
    public final static String TAG = "UsbConnectionMonitor";
    private static final String ACTION_USB_DEVICE_ATTACHED_CHECK = "act.device.check";

    public interface OnUsbConnectioListener {
        void connect(@Nullable UsbDevice device);
        void granted(UsbDevice device, boolean granted);
        void disconnect(UsbDevice device);
    }

//    private boolean attached;
    private OnUsbConnectioListener listener;
    private UsbDevice usbDevice;
    private UsbUtils.UsbDeviceFilter filter;

    public UsbConnectionMonitor() {}

    public UsbConnectionMonitor(UsbUtils.UsbDeviceFilter filter, OnUsbConnectioListener listener) {
        this.listener = listener;
        this.filter = filter;
    }

    public UsbConnectionMonitor setListener(OnUsbConnectioListener listener) {
        this.listener = listener;
        return this;
    }

    public UsbConnectionMonitor setFilter(UsbUtils.UsbDeviceFilter filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        UsbDevice device = getUsbDeviceFromIntent(intent);

        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            synchronized (this) {
//                attached = true;
                if (null != device && !UsbUtils.requestPermissionForUsbDevice(context, device)){
                    updateGrantedListener(device, true);
                } else {
                    updateConnectedListener(device);
                }
            }
        } else if (UsbUtils.isReqToActionPermission(intent)) {
            synchronized (this) {
                if (null != usbDevice) {
                    boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                    updateGrantedListener(device, granted);
                }
            }
        } else if (ACTION_USB_DEVICE_ATTACHED_CHECK.equals(action)) {
            synchronized (this) {
                if (null != device && !UsbUtils.requestPermissionForUsbDevice(context, device)) {
                    updateGrantedListener(device, true);
                } else {
                    updateConnectedListener(device);
                }
            }
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            synchronized (this) {
//                attached = false;
                if (usbDevice != null) {
                    // call your method that cleans up and closes communication with the device
                    UsbUtils.closeUsbConnection(context, usbDevice);
                    updateDisconnectedListener(usbDevice);
                }
            }
        }
//        Toast.makeText(context, action, Toast.LENGTH_LONG).show();
        Log.v("UsbConnectionMonitor","Receive=>"+action);
    }

    public void scanUsbDevices(Context context){
        Intent intent = new Intent(ACTION_USB_DEVICE_ATTACHED_CHECK);
        UsbDevice device;
        if (null == usbDevice) {
            device = UsbUtils.scanUsbDevices(context, filter);
        } else {
            device = usbDevice;
        }
        intent.putExtra(UsbManager.EXTRA_DEVICE, device);
        onReceive(context.getApplicationContext(), intent);
    }

    private void updateConnectedListener(UsbDevice usbDevice) {
        setUsbDevice(usbDevice);
        if (null != listener) {
            listener.connect(usbDevice);
        }
    }

    private void updateGrantedListener(UsbDevice usbDevice, boolean grant) {
        setUsbDevice(usbDevice);
        if (null != listener) {
            listener.granted(usbDevice, grant);
        }
    }

    private void updateDisconnectedListener(UsbDevice usbDevice) {
        if (null != listener) {
            listener.disconnect(usbDevice);
        }
        setUsbDevice(null);
    }

    private void setUsbDevice(UsbDevice usbDevice) {
        synchronized (this) {
            this.usbDevice = usbDevice;
        }
    }

    @Nullable
    private UsbDevice getUsbDeviceFromIntent(@NonNull Intent intent) {
        return (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
    }

    public void close() {
        usbDevice = null;
        listener = null;
        filter = null;
    }

}
