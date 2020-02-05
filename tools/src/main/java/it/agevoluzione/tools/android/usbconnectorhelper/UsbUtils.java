package it.agevoluzione.tools.android.usbconnectorhelper;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.HashMap;
import java.util.Iterator;

import it.agevoluzione.tools.android.utils.AndroidUtils;

public class UsbUtils {

    private static final String TAG = "UsbUtils";
    public static final String ACTION_USB_PERMISSION = "it.agevoluzione.USB_PERMISSION";
    private static final int USB_OPEN_INDEX = 0;

//    public static ArrayList<String> getConnectedUSBdevices(Context context) {
//        ArrayList<String> connectedDeviceNames = new ArrayList<String>();
//        try {
//            // Get UsbManager from Android.
//            UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
//            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
//            UsbDevice ad;
//            Set<String> keySet = deviceList.keySet();
//            for (String key : keySet) {
//                connectedDeviceNames.add(deviceList.get(key).getDeviceName());
//            }
//
//        } catch (Exception e) {
//            Log.e(TAG, "getConnectedUSBdevices", e);
//        }
//        return connectedDeviceNames;
//    }

    public static boolean closeUsbConnection(@NonNull UsbManager usbManager, @NonNull UsbDevice device) {
        if (null != device && null != usbManager) {
//            if (usbManager.hasPermission(device)) {
            try {
                if (usbManager.hasPermission(device)) {
                    UsbDeviceConnection connection = usbManager.openDevice(device);
                    if (null != connection) {
                        int size = device.getInterfaceCount();
                        for (int i = 0; i < size; i++) {
                            UsbInterface usbInterface = device.getInterface(i);
                            connection.releaseInterface(usbInterface);
                            connection.close();
                            return true;
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        return false;
    }

    public static boolean closeUsbConnection(@NonNull Context context, @NonNull UsbDevice device) {
        return closeUsbConnection(AndroidUtils.usbManager(context), device);
    }


    /**
     * Request permission to user for use the USB device connected
     *
     * @param context   NonNull context
     * @param usbDevice NonNull usbDevice
     * @return true if request need and are sent, false request not needed
     */
    public static boolean requestPermissionForUsbDevice(@NonNull Context context, @NonNull UsbDevice usbDevice) {
        UsbManager manager = AndroidUtils.usbManager(context);
        if (null != manager && !manager.hasPermission(usbDevice)) {
            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
            manager.requestPermission(usbDevice, mPermissionIntent);
            return true;
        }
        return false;
    }

    public interface UsbDeviceFilter {
        boolean isValid(@Nullable UsbDevice usbDevice);
    }

    @Nullable
    public static UsbDevice scanUsbDevices(@NonNull Context context, @NonNull UsbDeviceFilter filter) {
        UsbManager usbManager = AndroidUtils.usbManager(context);
        if (null != usbManager) {
            return scanUsbDevices(usbManager, filter);
        }
        return null;
    }

    @Nullable
    public static boolean verifyUsbDevice(@NonNull Context context, @NonNull UsbDevice usbDevice) {
        UsbManager usbManager = AndroidUtils.usbManager(context);
        if (null != usbManager) {
            return verifyUsbDevice(usbManager, usbDevice);
        }
        throw new NullPointerException("usbManager");
    }

    @Nullable
    public static boolean verifyUsbDevice(@NonNull UsbManager usbManager, @NonNull UsbDevice usbDevice) {
        return null != usbManager.openDevice(usbDevice);
    }

    @Nullable
    public static UsbDevice scanUsbDevices(@NonNull UsbManager usbManager, @NonNull UsbDeviceFilter filter) {
        UsbDevice device = null;
        if (null != usbManager) {
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            boolean iterate = deviceIterator.hasNext();
            while (iterate) {
                device = deviceIterator.next();
                if (filter.isValid(device)) {
                    iterate = false;
                } else {
                    iterate = deviceIterator.hasNext();
                }
            }
        }
        return device;
    }

    public static boolean isReqToActionPermission(Intent intent) {
        return ACTION_USB_PERMISSION.equalsIgnoreCase(intent.getAction());
    }

    @NonNull
    public static IntentFilter getIntenFilter() {
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        return filter;
    }

    @Nullable
    public static UsbDevice getUsbDeviceByIntent(Intent intent) {
        return (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
    }


}
