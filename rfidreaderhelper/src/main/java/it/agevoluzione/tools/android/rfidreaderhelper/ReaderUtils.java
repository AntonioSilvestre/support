package it.agevoluzione.tools.android.rfidreaderhelper;


import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.thingmagic.AndroidUsbReflection;
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.SimpleReadPlan;
import com.thingmagic.TMConstants;
import com.thingmagic.TagProtocol;

import it.agevoluzione.tools.android.usbconnectorhelper.UsbUtils;

public class ReaderUtils {
    private static final String TAG = "ReaderUtils";
    private static final String URI_AUTO = "tmr";
    private static final String URI_SERIAL = "eapi";
    private static final String URI_RQL = "rqp";
    private static final String URI_LLPR = "llpr";


    public static UsbUtils.UsbDeviceFilter getFTDIDeviceFilter() {
        return new UsbUtils.UsbDeviceFilter() {
            @Override
            public boolean isValid(@Nullable UsbDevice usbDevice) {
                return null != usbDevice
                        && usbDevice.getVendorId() == 1027
                        && usbDevice.getProductId() == 24577;
            }
        };
    }

    public static void setTrace(Reader reader, String args[]) {
        if (args[0].toLowerCase().equals("on")) {
            reader.addTransportListener(reader.simpleTransportListener);
        }
    }

    public static String infoReader(@NonNull Reader reader) throws ReaderException {
        String model = reader.paramGet(TMConstants.TMR_PARAM_VERSION_MODEL).toString();
        String serial = reader.paramGet(TMConstants.TMR_PARAM_VERSION_SERIAL).toString();
        Boolean checkPort = (Boolean)reader.paramGet(TMConstants.TMR_PARAM_ANTENNA_CHECKPORT);
        String swVersion = (String) reader.paramGet(TMConstants.TMR_PARAM_VERSION_SOFTWARE);
        return "Reader: "+model+" serial: "+serial+" swVersion: "+swVersion+" checkport: "+checkPort;
    }

    public static Reader connect(UsbDevice usbDevice) throws Exception {
        try {
            Reader reader = create(usbDevice);
            reader.connect();
            return reader;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @NonNull
    public static String getUri(@NonNull UsbDevice usbDevice) {
        String deviceName = usbDevice.getDeviceName();
        String url = URI_AUTO+"://"+deviceName;
        Log.v("ReaderURI", "generate uri: "+ url);
        return url;
    }

    public static Reader create(@NonNull UsbDevice usbDevice) throws Exception {
        String url = getUri(usbDevice);
        return Reader.create(url);
    }

    public static void setAntennas(@NonNull Reader connectedReader, @NonNull int... antennas) throws ReaderException {
        SimpleReadPlan simplePlan = new SimpleReadPlan(antennas, TagProtocol.GEN2);
        connectedReader.paramSet("/reader/read/plan", simplePlan);
    }

    public static void setRegion(@NonNull Reader connectedReader, @Nullable Reader.Region region) throws Exception {
        Reader.Region[] supportedRegions = (Reader.Region[]) connectedReader
                .paramGet(TMConstants.TMR_PARAM_REGION_SUPPORTEDREGIONS);
        if (supportedRegions.length < 1) {
            throw new Exception("Reader doesn't support any regions");
        }
        Reader.Region regionSetted = (Reader.Region) connectedReader.paramGet(TMConstants.TMR_PARAM_REGION_ID);
        if (region == null) {
            region = supportedRegions[0];
        }
        if (regionSetted != region) {
            connectedReader.paramSet(TMConstants.TMR_PARAM_REGION_ID, region);
        }
    }

    public static void close(Reader reader) {
        if (null != reader) {
            reader.stopReading();
            reader.destroy();
        }
    }

    @NonNull
    public static AndroidUsbReflection setupUsbManagerWithFtdi(@NonNull Context context, @NonNull UsbDevice usbDevice) throws Exception {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        int deviceClass = usbDevice.getDeviceClass();
        if (deviceClass == 0) {
            FT_Device ftDev = getFTDI(context, usbDevice);
            return new AndroidUsbReflection(usbManager, ftDev, usbDevice, deviceClass);
        } else {
            throw new Exception("No valid AndroidUsbReflection device for "+usbDevice);
        }
    }

    @NonNull
    public static FT_Device getFTDI(Context context, UsbDevice usbDevice) throws Exception {
        Context appContext = context.getApplicationContext();
        UsbUtils.requestPermissionForUsbDevice(context, usbDevice);

        D2xxManager ftD2xx = D2xxManager.getInstance(appContext);
        int tempDevCount = ftD2xx.createDeviceInfoList(appContext);
        if (0 < tempDevCount){
            return ftD2xx.openByUsbDevice(context, usbDevice);
        } else {
            if (ftD2xx.isFtDevice(usbDevice)) {
                ftD2xx.addUsbDevice(usbDevice);
                return ftD2xx.openByUsbDevice(context, usbDevice);
            } else {
                throw new Exception("No valid FT_Device device for "+usbDevice);
            }
        }

    }
}
