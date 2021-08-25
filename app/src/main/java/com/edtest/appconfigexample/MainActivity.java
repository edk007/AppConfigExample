package com.edtest.appconfigexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.RestrictionsManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "APP_CONFIG_EXAMPLE";
    public static final String TAG2 = "MAIN_ACTIVITY: ";

    public static final String KEY_ID_NUMBER = "ID_NUMBER";

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        RestrictionsManager manager = (RestrictionsManager) getSystemService(Context.RESTRICTIONS_SERVICE);
        Bundle restrictions = manager.getApplicationRestrictions();

        Set<String> keys = restrictions.keySet();

        if (keys.isEmpty()) {
            //empty key set here - not a managed application
            Log.w(TAG, TAG2 + "KEY_SET_EMPTY");
            textView.setText("KEY_SET_EMPTY");
        } else {
            //we've got keys to process
            for (String k : keys) {
                Object value = restrictions.get(k);
                switch (k) {
                    case KEY_ID_NUMBER:
                        //license key
                        String serialNumber = value.toString();
                        Log.w(TAG, TAG2 + "LOAD_RESTRICTIONS: KEY: " + KEY_ID_NUMBER + " VALUE:" + serialNumber);
                        textView.setText(serialNumber);
                        break;
                    default:
                        //nothing???
                        Log.w(TAG, TAG2 + "LOAD_RESTRICTIONS: NO KEY-" + "VALUE:" + value.toString());
                        break;
                } //switch
            } //for keys
        }//key.isEmpty()
    }
}