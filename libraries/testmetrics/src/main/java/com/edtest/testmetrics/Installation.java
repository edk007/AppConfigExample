package com.edtest.testmetrics;

import android.content.Context;
import android.content.RestrictionsManager;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.Set;

public class Installation {
    private static String sID = null;

    private static final String INSTALLATION = "INSTALLATION2";

    public static final String KEY_SERIAL_NUMBER = "SERIAL_NUMBER";
    public static final String KEY_SERIAL_NUMBER_2 = "SERIAL_NUMBER2";
    private static Context c;

    public synchronized static String id(Context context) {
        c = context;
        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists()) {
                    writeInstallationFile(installation);
                }
                sID = readInstallationFile(installation);
                Log.w(INSTALLATION,"SID:" + sID);
            } catch (Exception e) {
                Log.w(INSTALLATION,e.toString());
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        String serialNumber = new String(bytes);
        Log.w(INSTALLATION,"READ_ID:" + serialNumber);
        return serialNumber;
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        //get a serial number from 1 of 2 places here
        //first - get the ril.serialnumber if it exists
        String rilSerialNumber = "";
        rilSerialNumber = loadRilSerialNumber();
        Log.w(INSTALLATION, "WRITE_ID: RIL_SERIAL: " + rilSerialNumber);

        //second get serial number from app restrictions
        String appRestrictionsSerialNumber = "";
        appRestrictionsSerialNumber = loadAppRestrictionSerialNumber();
        Log.w(INSTALLATION, "WRITE_ID: APP_CONFIG_SERIAL: " + appRestrictionsSerialNumber);

        if (appRestrictionsSerialNumber.matches("") && rilSerialNumber.matches("")) {
            //both serial numbers are still blank
            Log.w(INSTALLATION,"WRITE_ID: BOTH_BLANK");
        }

        String serialNumber = appRestrictionsSerialNumber;
        if (serialNumber.matches("")) {
            serialNumber = rilSerialNumber;
            if (serialNumber.matches("")) {
                serialNumber = "BLANK";
            }
        }

        Log.w(INSTALLATION,"WRITE_ID:" + serialNumber);
        out.write(serialNumber.getBytes());
        out.close();
    }

    private static String loadRilSerialNumber() {
        String rilSerialnumber = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Log.w("CLASS:", c.toString());

            Method get = c.getMethod("get", String.class);

            rilSerialnumber = (String) get.invoke(c, "ril.serialnumber");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rilSerialnumber;
    }

    private static String loadAppRestrictionSerialNumber() {
        RestrictionsManager manager = (RestrictionsManager) c.getSystemService(Context.RESTRICTIONS_SERVICE);
        Bundle restrictions = manager.getApplicationRestrictions();

        Set<String> keys = restrictions.keySet();

        String serialNumber = "";

        if (keys.isEmpty()) {
            //empty key set here - not a managed application
            Log.w(INSTALLATION,"LOAD_APP_RESTRICTIONS: " + "KEY_SET_EMPTY");
        } else {
            //we've got keys to process
            for (String k : keys) {
                Object value = restrictions.get(k);
                switch (k) {
                    case KEY_SERIAL_NUMBER:
                        //license key
                        serialNumber = value.toString();
                        Log.w(INSTALLATION,"LOAD_APP_RESTRICTIONS: " + "LOAD_RESTRICTIONS: KEY: " + KEY_SERIAL_NUMBER + " VALUE:" + serialNumber);
                        break;
                    case KEY_SERIAL_NUMBER_2:
                        //license key
                        serialNumber = value.toString();
                        Log.w(INSTALLATION,"LOAD_APP_RESTRICTIONS: " + "LOAD_RESTRICTIONS: KEY: " + KEY_SERIAL_NUMBER_2 + " VALUE:" + serialNumber);
                        break;
                    default:
                        //nothing???
                        Log.w(INSTALLATION,"LOAD_APP_RESTRICTIONS: " + "LOAD_RESTRICTIONS: NO KEY-" + "VALUE:" + value.toString());
                        break;
                } //switch
            } //for keys
        }//key.isEmpty()
        return serialNumber;
    }

}//Installation
