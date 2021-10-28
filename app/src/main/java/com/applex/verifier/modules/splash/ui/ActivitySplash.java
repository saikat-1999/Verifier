package com.applex.verifier.modules.splash.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.applex.verifier.R;
import com.applex.verifier.modules.main.ui.MainActivity;

public class ActivitySplash extends AppCompatActivity {

    private static final long Splash_time_out = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(ActivitySplash.this, MainActivity.class));
            finish();
        },Splash_time_out);
    }
}