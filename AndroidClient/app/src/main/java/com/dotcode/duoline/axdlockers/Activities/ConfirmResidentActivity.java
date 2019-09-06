package com.dotcode.duoline.axdlockers.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dotcode.duoline.axdlockers.Models.RetroFilteredResident;
import com.dotcode.duoline.axdlockers.Models.RetroLocker;
import com.dotcode.duoline.axdlockers.R;
import com.google.gson.Gson;

public class ConfirmResidentActivity extends AppCompatActivity {
    private RetroFilteredResident currentResident;
    private TextView fullName, phone, email, building, address;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_resident);
        fullName = (TextView) findViewById(R.id.fullNameTextView);
        phone =  (TextView) findViewById(R.id.tvPhone);
        email = (TextView) findViewById(R.id.tvEmail);
        building = (TextView) findViewById(R.id.tvBuilding);
        address = (TextView) findViewById(R.id.tvAddress);
        confirmButton = (Button) findViewById(R.id.buttonConfirm);

        String json = getIntent().getStringExtra("JSON_RESIDENT");
        if(!json.equals("")) {
            Gson gson = new Gson();
            currentResident = gson.fromJson(json, RetroFilteredResident.class);
            fullName.setText(currentResident.getResident().getFirstName() + " " + currentResident.getResident().getLastName());
            if(!currentResident.getResident().getPhoneNumber().equals(""))
                phone.setText(currentResident.getResident().getPhoneNumber());
            email.setText(currentResident.getResident().getEmail());
            if(currentResident.getBuilding() != null) {
                building.setText(currentResident.getBuilding().getBuildingUniqueNumber() + "\n" + currentResident.getBuilding().getName());
                address.setText(currentResident.getSuiteNumber() + " - " + currentResident.getBuilding().getAddress().getStreetName() +
                        "\n" + currentResident.getBuilding().getAddress().getZipCode() + " " +
                        currentResident.getBuilding().getAddress().getCity().getName() + " " +
                        currentResident.getBuilding().getAddress().getCity().getState().getName() + " " +
                        currentResident.getBuilding().getAddress().getCity().getState().getCountry().getName());
            }

            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ConfirmResidentActivity.this, QRScanActivity.class);
                }
            });
        }


    }
}
