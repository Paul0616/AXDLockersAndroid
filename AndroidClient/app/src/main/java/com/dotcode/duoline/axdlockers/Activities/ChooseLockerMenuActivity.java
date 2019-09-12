package com.dotcode.duoline.axdlockers.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.dotcode.duoline.axdlockers.R;

public class ChooseLockerMenuActivity extends AppCompatActivity {

    private ConstraintLayout scanQRButton, searchLockerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_locker_menu);
        scanQRButton = (ConstraintLayout) findViewById(R.id.scanQRButton);
        searchLockerButton = (ConstraintLayout) findViewById(R.id.manualButton);
        scanQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseLockerMenuActivity.this, QRScanActivity.class));
            }
        });
        searchLockerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseLockerMenuActivity.this, LockerFilterActivity.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
