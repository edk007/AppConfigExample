package com.edtest.testmetrics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class SilentActivity extends AppCompatActivity {
    public static final String TAG = "APP_CONFIG_EXAMPLE";
    public static final String TAG2 = "SILENT_ACTIVITY: ";

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silent);

        textView = findViewById(R.id.silentTextView);

        String serialNumber = Installation.id(this);

        textView.setText(serialNumber);
    }
}