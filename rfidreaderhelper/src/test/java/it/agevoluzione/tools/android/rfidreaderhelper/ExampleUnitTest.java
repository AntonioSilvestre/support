package it.agevoluzione.tools.android.rfidreaderhelper;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testStat() {
        System.out.println("INITIALIZED= " + ReaderHelper.INITIALIZED);
        System.out.println("USB_DEVICE_ATTACHED_NOT_AUTHORIZED= " + ReaderHelper.USB_DEVICE_ATTACHED_NOT_AUTHORIZED);
        System.out.println("USB_DEVICE_AUTHORIZATION_GRANT= " + ReaderHelper.USB_DEVICE_AUTHORIZATION_GRANT);
        System.out.println("READER_CONNECTING= " + ReaderHelper.READER_CONNECTING);
        System.out.println("READER_CONNECTED= " + ReaderHelper.READER_CONNECTED);
        System.out.println("READER_CONFIGURED= " + ReaderHelper.READER_CONFIGURED);
        System.out.println("tot= " + (ReaderHelper.INITIALIZED |
                ReaderHelper.USB_DEVICE_ATTACHED_NOT_AUTHORIZED |
                ReaderHelper.USB_DEVICE_AUTHORIZATION_GRANT |
                ReaderHelper.READER_CONNECTING |
                ReaderHelper.READER_CONNECTED |
                ReaderHelper.READER_CONFIGURED)
        );
    }

    //    public final static int NOT_INITIALIZED = 0;
    public final static byte INITIALIZED = 1;

    //    public final static int DEVICE_DETACHED
    public final static byte DEVICE_ATTACHED = INITIALIZED << 1;

    //    public final static int DEVICE_AUTHORIZATION_NOT_GRANT,
    public final static byte DEVICE_AUTHORIZATION_GRANT = INITIALIZED << 2;

    //    public final static int READER_DISCONNECTED,
    public final static byte READER_CONNECTED = INITIALIZED << 3;

    //    public final static int READER_NOT_CONFIGURED,
    public final static byte READER_CONFIGURED = INITIALIZED << 4;

    public final static byte DEVIDE_READY = INITIALIZED | DEVICE_ATTACHED | DEVICE_AUTHORIZATION_GRANT | READER_CONNECTED | READER_CONFIGURED;
//    public final static int DEVICE_READING,

    @Test
    public void testStat2() {
        System.out.println("INITIALIZED= " + INITIALIZED);
        System.out.println("USB_DEVICE_ATTACHED_NOT_AUTHORIZED= " + DEVICE_ATTACHED);
        System.out.println("USB_DEVICE_AUTHORIZATION_GRANT= " + DEVICE_AUTHORIZATION_GRANT);
        System.out.println("READER_CONNECTING= " + READER_CONNECTED);
        System.out.println("READER_CONNECTED= " + READER_CONFIGURED);
        System.out.println("READER_CONFIGURED= " + DEVIDE_READY);
        System.out.println("tot= " + (INITIALIZED |
                DEVICE_ATTACHED |
                DEVICE_AUTHORIZATION_GRANT |
                READER_CONNECTED |
                READER_CONFIGURED)
        );
    }

    @Test
    public void testStat3() {
        testStat2();

        int status = 0;

        System.out.println(" -- Begin -- ");
        System.out.println("Add BEGIN=" + status);

        status = status | INITIALIZED;

        System.out.println("Add INIT=" + status);


        status = status | DEVICE_AUTHORIZATION_GRANT;

        System.out.println("Add USB_DEVICE_AUTHORIZATION_GRANT=" + status);

        status = 0;
        canOperate(status, INITIALIZED);

        status = INITIALIZED;
        canOperate(status, DEVICE_ATTACHED);

        status = DEVICE_ATTACHED;
        canOperate(status, DEVICE_AUTHORIZATION_GRANT);

        status = DEVICE_AUTHORIZATION_GRANT;
        canOperate(status, READER_CONNECTED);

        status = READER_CONNECTED;
        canOperate(status, READER_CONFIGURED);

    }

    @Test
    public void testStat4() {
        testStat2();

        int status = 0;

        System.out.println(" -- Begin -- ");
        System.out.println("Add BEGIN=" + status);

        status = status | INITIALIZED;

        System.out.println("Add INIT=" + status);


        status = status | DEVICE_AUTHORIZATION_GRANT;

        System.out.println("Add USB_DEVICE_AUTHORIZATION_GRANT=" + status);

        status = INITIALIZED;
        canOperate(status, DEVICE_ATTACHED);

        canOperate(status, DEVICE_AUTHORIZATION_GRANT);

        canOperate(status, READER_CONNECTED);

        canOperate(status, READER_CONFIGURED);

    }

    @Test
    public void testStat5() {
        testStat2();

        int status = 0;

        System.out.println(" -- Begin -- ");
        System.out.println("Add BEGIN=" + status);

        status = status | INITIALIZED;

        System.out.println("Add INIT=" + status);


        status = status | DEVICE_AUTHORIZATION_GRANT;

        System.out.println("Add USB_DEVICE_AUTHORIZATION_GRANT=" + status);

        status = 0;

        status = add(status, INITIALIZED);
        status = add(status, DEVICE_ATTACHED);
        status = add(status, DEVICE_AUTHORIZATION_GRANT);
        status = add(status, READER_CONNECTED);
        status = add(status, READER_CONFIGURED);


    }

    public int add(int now, int future) {
        if (canOperate(now, future)) {
            return future;
        }
        return now;
    }

    public boolean canOperate(int now, int future) {
        int p = future >> 1 & now;
        System.out.println("value: " + p + " stat:" + (p == now));
        return p == now;

    }


}