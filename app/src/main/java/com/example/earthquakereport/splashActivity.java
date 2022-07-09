package com.example.earthquakereport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class splashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Thread td=new Thread() {
            public void run() {
                try {
                    sleep(5000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    Intent intent =new Intent(splashActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };td.start();
    }
}