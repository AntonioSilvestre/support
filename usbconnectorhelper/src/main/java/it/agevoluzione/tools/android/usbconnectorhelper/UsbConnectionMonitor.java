package it.agevoluzione.tools.android.usbconnectorhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

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

    private OnUsbConnectioListener listner;
    private UsbDevice usbDevice;
    private UsbUtils.UsbDeviceFilter filter;

    public UsbConnectionMonitor() {}

    public UsbConnectionMonitor(UsbUtils.UsbDeviceFilter filter, OnUsbConnectioListener listner) {
        this.listner = listner;
        this.filter = filter;
    }

    public UsbConnectionMonitor setListner(OnUsbConnectioListener listner) {
        this.listner = listner;
        return this;
    }

    public UsbConnectionMonitor setFilter(UsbUtils.UsbDeviceFilter filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action) || ACTION_USB_DEVICE_ATTACHED_CHECK.equals(action)) {
            synchronized (this) {
                UsbDevice device = getUsbDeviceFromIntent(intent);
                if (null != device && !UsbUtils.requestPermissionForUsbDevice(context, device)){
                    updateGrantedListener(device, true);
                } else {
                    updateConnectedListener(device);
                }
            }
        } else if (UsbUtils.isReqToActionPermission(intent)) {
            synchronized (this) {
                UsbDevice device = getUsbDeviceFromIntent(intent);
                boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                updateGrantedListener(device, granted);
            }
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            synchronized (this) {
                if (usbDevice != null) {
                    // call your method that cleans up and closes communication with the device
                    UsbUtils.closeUsbConnection(context, usbDevice);
                    updateDisconnectedListener(usbDevice);
                }
            }
        }
        Toast.makeText(context, action, Toast.LENGTH_LONG).show();
    }

    public void scanUsbDevices(Context context){
        Intent intent = new Intent(ACTION_USB_DEVICE_ATTACHED_CHECK);
        UsbDevice device = null;
        if (null == usbDevice) {
            device = UsbUtils.scanUsbDevices(context, filter);
        }
        intent.putExtra(UsbManager.EXTRA_DEVICE, device);
        onReceive(context.getApplicationContext(), intent);
    }


    private void updateConnectedListener(UsbDevice usbDevice) {
        this.usbDevice = usbDevice;
        if (null != listner) {
            listner.connect(usbDevice);
        }
    }

    private void updateGrantedListener(UsbDevice usbDevice, boolean grant) {
        this.usbDevice = usbDevice;
        if (null != listner) {
            listner.granted(usbDevice, grant);
        }
    }

    private void updateDisconnectedListener(UsbDevice usbDevice) {
        if (null != listner) {
            listner.disconnect(usbDevice);
        }
        this.usbDevice = usbDevice;
    }

    @Nullable
    private UsbDevice getUsbDeviceFromIntent(@NonNull Intent intent) {
        return (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
    }

    public void close() {
        usbDevice = null;
        listner = null;
        filter = null;
    }

}
