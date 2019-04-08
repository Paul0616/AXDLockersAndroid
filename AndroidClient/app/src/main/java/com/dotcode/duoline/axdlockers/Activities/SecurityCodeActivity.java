package com.dotcode.duoline.axdlockers.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dotcode.duoline.axdlockers.Models.RetroBuilding;
import com.dotcode.duoline.axdlockers.Models.RetroFilteredResident;
import com.dotcode.duoline.axdlockers.Models.RetroLocker;
import com.dotcode.duoline.axdlockers.Models.RetroResident;
import com.dotcode.duoline.axdlockers.R;
import com.google.gson.Gson;

public class SecurityCodeActivity extends AppCompatActivity {
    RetroLocker currentLocker;
    RetroFilteredResident currentResident;
    private TextView lockerNumber, lockerAddress, lockerSize;
    private TextView residentName, suiteNumber, phone, email;
    private TextView securityCode;
    private Button buttonConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_code);
        setTitle("");
        String json = getIntent().getStringExtra("JSON_LOCKER");
        Gson gson = new Gson();
        currentLocker = gson.fromJson(json, RetroLocker.class);
//        json = getIntent().getStringExtra("JSON_BUILDING");
//        gson = new Gson();
//        currentBuilding = gson.fromJson(json, RetroBuilding.class);
        json = getIntent().getStringExtra("JSON_RESIDENT");
        gson = new Gson();
        currentResident = gson.fromJson(json, RetroFilteredResident.class);
        lockerNumber = (TextView) findViewById(R.id. number);
        lockerAddress = (TextView) findViewById(R.id.address);
        lockerSize = (TextView) findViewById(R.id.size);
        residentName = (TextView) findViewById(R.id.residentName);
        suiteNumber = (TextView) findViewById(R.id.suiteNumber);
        phone = (TextView) findViewById(R.id.phone);
        email = (TextView) findViewById(R.id.email);
        securityCode = (TextView) findViewById(R.id.securityCode);
        buttonConfirm = (Button) findViewById(R.id.buttonConfirm);

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SecurityCodeActivity.this, FinalPostActivity.class);
                Gson gson = new Gson();
                String json = gson.toJson(currentLocker);
                i.putExtra("JSON_LOCKER", json);
                gson = new Gson();
                json = gson.toJson(currentResident);
                i.putExtra("JSON_RESIDENT", json);
                startActivity(i);
            }
        });

        lockerNumber.setText("#"+currentLocker.getNumber());
        lockerAddress.setText(currentLocker.getAddress().getStreetName() +", " + currentLocker.getAddress().getCity().getName() +", " +
                currentLocker.getAddress().getCity().getState().getName() +", " + currentLocker.getAddress().getZipCode());
        lockerSize.setText(currentLocker.getSize());

        residentName.setText(currentResident.getResident().getFirstName() + " " + currentResident.getResident().getLastName());
        suiteNumber.setText(currentResident.getSuiteNumber());
        phone.setText(currentResident.getResident().getPhoneNumber());
        email.setText(currentResident.getResident().getEmail());
        securityCode.setText(currentResident.getResident().getSecurityCode());
    }
}
