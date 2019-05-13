package com.dotcode.duoline.axdlockers.Activities;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dotcode.duoline.axdlockers.Models.RetroFilteredResident;
import com.dotcode.duoline.axdlockers.Models.RetroLocker;
import com.dotcode.duoline.axdlockers.Models.RetroLockerBuildingResident;
import com.dotcode.duoline.axdlockers.Models.RetroLockerBuildingResidentID;
import com.dotcode.duoline.axdlockers.Models.RetroLockerBuildingResidentsList;
import com.dotcode.duoline.axdlockers.Models.RetroLockerHistory;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class FinalPostActivity extends AppCompatActivity implements SetRequests.GetDataResponse {
    RetroLocker currentLocker;
    RetroFilteredResident currentResident;
    private TextView infoMessage, mainMessage, closeButton;
    private ProgressBar progressBar;
    private Button backButton;
    private ConstraintLayout popupWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_post);

        String json = getIntent().getStringExtra("JSON_LOCKER");
        Gson gson = new Gson();
        currentLocker = gson.fromJson(json, RetroLocker.class);

        json = getIntent().getStringExtra("JSON_RESIDENT");
        gson = new Gson();
        currentResident = gson.fromJson(json, RetroFilteredResident.class);

        infoMessage = (TextView) findViewById(R.id.infoMessage);
        mainMessage = (TextView) findViewById(R.id.mainMessage);
        closeButton = (TextView) findViewById(R.id.closeButton);
        backButton = (Button) findViewById(R.id.backButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBarPost);
        progressBar.setVisibility(View.VISIBLE);
        closeButton.setEnabled(false);
        backButton.setVisibility(View.INVISIBLE);
        popupWindow = (ConstraintLayout) findViewById(R.id.view);

        int lastInsertedLockerBuildingResidentID = SaveSharedPreferences.getlastInsertedLBRID(getApplicationContext());
        int lastInsertedLockerHistoryID = SaveSharedPreferences.getlastInsertedLHID(getApplicationContext());

        if (lastInsertedLockerBuildingResidentID != 0){
            deletePreviousFailedLockerBuildingResidentRecord(lastInsertedLockerBuildingResidentID);
        } else if (lastInsertedLockerHistoryID != 0){
            deletePreviousFailedLockerHistoryRecord(lastInsertedLockerHistoryID);
        } else {
            checkLockerBuildingResident();
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FinalPostActivity.this, MainActivity.class));
                finish();
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void deletePreviousFailedLockerBuildingResidentRecord(int id){
        Map<String, String> param = new HashMap<String, String>();
        param.put("ID", String.valueOf(id));
        new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_DELETE_LOCKER_BUILDING_RESIDENT, param, null);
    }

    private void deletePreviousFailedLockerHistoryRecord(int id){
        Map<String, String> param = new HashMap<String, String>();
        param.put("ID", String.valueOf(id));
        new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_DELETE_LOCKER_HISTORY, param, null);
    }

    private void checkLockerBuildingResident(){
        Map<String, String> param = new HashMap<String, String>();
        param.put("filter[lockerId]", ""+currentLocker.getId());
        param.put("filter[buildingResidentId]", ""+currentResident.getId());
        new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_LOCKER_BUILDING_RESIDENT, param, null);
    }

    private void insertLockerHistory(){
        String lockerAddress = currentLocker.getAddress().getStreetName() + ", " + currentLocker.getAddress().getCity().getName() + ", " +
                currentLocker.getAddress().getCity().getState().getName() + ", " + currentLocker.getAddress().getZipCode();
        String buildingAddress = currentResident.getBuilding().getName() + ", " + currentResident.getBuilding().getAddress().getStreetName() + ", " +
                currentResident.getBuilding().getAddress().getCity().getName() + ", " + currentResident.getBuilding().getAddress().getCity().getState().getName() +", " +
                currentResident.getBuilding().getAddress().getZipCode();
        RetroLockerHistory body = new RetroLockerHistory(0, currentLocker.getQrCode(), currentLocker.getNumber(), currentLocker.getSize(),
                lockerAddress, currentResident.getResident().getFirstName(), currentResident.getResident().getLastName(), currentResident.getResident().getEmail(),
                currentResident.getResident().getPhoneNumber(), currentResident.getResident().getSecurityCode(), currentResident.getSuiteNumber(),
                currentResident.getBuilding().getName(), buildingAddress, buildingAddress, currentResident.getBuilding().getBuildingUniqueNumber(),
                SaveSharedPreferences.getEmail(getApplicationContext()), "STATUS_NOT_CONFIRMED", SaveSharedPreferences.getFirstName(getApplicationContext()),
                SaveSharedPreferences.getLasttName(getApplicationContext()));

        new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_INSERT_LOCKER_HISTORY, null, body);
    }

    private void insertLockerBuildingResident(){
        RetroLockerBuildingResident body = new RetroLockerBuildingResident(0, currentLocker.getId(), currentResident.getId(), Helper.STATUS_NOT_CONFIRMED);
        new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_INSERT_LOCKER_BUILDING_RESIDENT, null, body);
    }

    private void insertNotification(){
        RetroLockerBuildingResidentID lbr = new RetroLockerBuildingResidentID(SaveSharedPreferences.getlastInsertedLBRID(getApplicationContext()));
        new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_INSERT_NOTIFICATION, null, lbr);
    }

    @Override
    public void onResponse(int currentRequestId, Object result) {
        if (currentRequestId == Helper.REQUEST_DELETE_LOCKER_BUILDING_RESIDENT){
            SaveSharedPreferences.setLBRNull(getApplicationContext());
            int lastInsertedLockerHistoryId = SaveSharedPreferences.getlastInsertedLHID(getApplicationContext());
            if (lastInsertedLockerHistoryId != 0){
                deletePreviousFailedLockerHistoryRecord(lastInsertedLockerHistoryId);
            } else {
                checkLockerBuildingResident();
            }
        }

        if (currentRequestId == Helper.REQUEST_DELETE_LOCKER_HISTORY){
            SaveSharedPreferences.setLHNull(getApplicationContext());
            checkLockerBuildingResident();
        }

        if (currentRequestId == Helper.REQUEST_LOCKER_BUILDING_RESIDENT){
            if(result != null && result instanceof RetroLockerBuildingResidentsList) {
                if (((RetroLockerBuildingResidentsList) result).getLBRs().size() > 0) {
                    int lastInsertedLockerBuildingResidentID = ((RetroLockerBuildingResidentsList) result).getLBRs().get(0).getId();
                    SaveSharedPreferences.setlastInsertedLBRID(getApplicationContext(), lastInsertedLockerBuildingResidentID);
                    infoMessage.setText(getString(R.string.creating_locker_history));
                    insertLockerHistory();
                } else {
                    infoMessage.setText(getString(R.string.creating_locker_building_resident_association));
                    insertLockerBuildingResident();
                }
            }
        }

        if (currentRequestId == Helper.REQUEST_INSERT_LOCKER_BUILDING_RESIDENT){
            if(result != null && result instanceof RetroLockerBuildingResident) {
                int lastInsertedLockerBuildingResidentID = ((RetroLockerBuildingResident) result).getId();
                SaveSharedPreferences.setlastInsertedLBRID(getApplicationContext(), lastInsertedLockerBuildingResidentID);
                infoMessage.setText(getText(R.string.creating_locker_history));
                insertLockerHistory();
            }
        }
        if (currentRequestId == Helper.REQUEST_INSERT_LOCKER_HISTORY){
            if(result != null && result instanceof RetroLockerHistory) {
                int lastInsertedLockerHistoryID = ((RetroLockerHistory) result).getId();
                SaveSharedPreferences.setlastInsertedLHID(getApplicationContext(),lastInsertedLockerHistoryID);
                infoMessage.setText(R.string.sending_notification_to_resident);
                insertNotification();
            }
        }
        if(currentRequestId == Helper.REQUEST_INSERT_NOTIFICATION) {
            //
            progressBar.setVisibility(View.INVISIBLE);
            mainMessage.setText(getString(R.string.notification_was_sent));
            mainMessage.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            infoMessage.setText("");
            backButton.setVisibility(View.VISIBLE);
            popupWindow.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
            SaveSharedPreferences.setLBRNull(getApplicationContext());
            SaveSharedPreferences.setLHNull(getApplicationContext());
        }
    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {
        progressBar.setVisibility(View.INVISIBLE);
        closeButton.setEnabled(true);
        infoMessage.setText("");
        mainMessage.setText(getString(R.string.sending_notification_failed));
        mainMessage.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        popupWindow.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
    }
}
