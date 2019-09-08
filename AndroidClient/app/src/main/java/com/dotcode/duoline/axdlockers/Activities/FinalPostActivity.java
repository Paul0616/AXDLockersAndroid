package com.dotcode.duoline.axdlockers.Activities;

import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dotcode.duoline.axdlockers.Models.RetroFilteredResident;
import com.dotcode.duoline.axdlockers.Models.RetroLocker;
import com.dotcode.duoline.axdlockers.Models.RetroParcel;
import com.dotcode.duoline.axdlockers.Models.RetroParcelID;
import com.dotcode.duoline.axdlockers.Models.RetroParcelsList;
import com.dotcode.duoline.axdlockers.Models.RetroLockerHistory;
import com.dotcode.duoline.axdlockers.Models.RetroVirtualParcel;
import com.dotcode.duoline.axdlockers.Models.RetroVirtualParcelID;
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
    private String securityCode;

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
        securityCode = getIntent().getStringExtra("securityCode");
        infoMessage = (TextView) findViewById(R.id.infoMessage);
        mainMessage = (TextView) findViewById(R.id.mainMessage);
        closeButton = (TextView) findViewById(R.id.closeButton);
        backButton = (Button) findViewById(R.id.backButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBarPost);
        progressBar.setVisibility(View.VISIBLE);
        closeButton.setEnabled(false);
        backButton.setVisibility(View.INVISIBLE);
        popupWindow = (ConstraintLayout) findViewById(R.id.view);

        int lastInsertedParcelID = SaveSharedPreferences.getlastInsertedParcelID(getApplicationContext());
        int lastInsertedLockerHistoryID = SaveSharedPreferences.getlastInsertedHistoryID(getApplicationContext());
        int lastInsertedVirtualParcelID = SaveSharedPreferences.getlastInsertedVirtualParcelID(getApplicationContext());


        if(currentLocker.getId() != 0) {
            SaveSharedPreferences.setVirtualParcelNull(getApplicationContext());
            if (lastInsertedParcelID != 0) {
                deletePreviousFailedParcelRecord(lastInsertedParcelID, false);
            } else if (lastInsertedLockerHistoryID != 0) {
                deletePreviousFailedLockerHistoryRecord(lastInsertedLockerHistoryID);
            } else {
                //checkLockerBuildingResident();
                insertParcel(false);
            }
        } else {
            SaveSharedPreferences.setParcelNull(getApplicationContext());
            if (lastInsertedVirtualParcelID != 0) {
                deletePreviousFailedParcelRecord(lastInsertedVirtualParcelID, true);
            } else if (lastInsertedLockerHistoryID != 0) {
                deletePreviousFailedLockerHistoryRecord(lastInsertedLockerHistoryID);
            } else {
                //checkLockerBuildingResident();
                insertParcel(true);
            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FinalPostActivity.this, MainMenuActivity.class));
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

    private void deletePreviousFailedParcelRecord(int id, boolean isVirtual){
        if (!isVirtual) {
            infoMessage.setText("Deleting last failed parcel...");
            Map<String, String> param = new HashMap<String, String>();
            param.put("ID", String.valueOf(id));
            new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_DELETE_PARCEL, param, null);
        } else {
            infoMessage.setText("Deleting last failed virtual parcel...");
            Map<String, String> param = new HashMap<String, String>();
            param.put("ID", String.valueOf(id));
            new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_DELETE_VIRTUAL_PARCEL, param, null);
        }
    }

    private void deletePreviousFailedLockerHistoryRecord(int id){
        infoMessage.setText("Deleting last failed locker history...");
        Map<String, String> param = new HashMap<String, String>();
        param.put("ID", String.valueOf(id));
        new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_DELETE_LOCKER_HISTORY, param, null);
    }

//    private void checkLockerBuildingResident(){
//        Map<String, String> param = new HashMap<String, String>();
//        param.put("filter[lockerId]", ""+currentLocker.getId());
//        param.put("filter[buildingResidentId]", ""+currentResident.getId());
//        new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_LOCKER_BUILDING_RESIDENT, param, null);
//    }

    private void insertLockerHistory(){
        infoMessage.setText(getText(R.string.creating_locker_history));
        String lockerAddress = currentLocker.getAddress().getStreetName() + ", " + currentLocker.getAddress().getCity().getName() + ", " +
                currentLocker.getAddress().getCity().getState().getName() + ", " + currentLocker.getAddress().getZipCode();
        String buildingAddress = currentResident.getBuilding().getName() + ", " + currentResident.getBuilding().getAddress().getStreetName() + ", " +
                currentResident.getBuilding().getAddress().getCity().getName() + ", " + currentResident.getBuilding().getAddress().getCity().getState().getName() +", " +
                currentResident.getBuilding().getAddress().getZipCode();
        RetroLockerHistory body = new RetroLockerHistory(0, currentLocker.getQrCode(), currentLocker.getNumber(), currentLocker.getSize(),
                lockerAddress, currentResident.getResident().getFirstName(), currentResident.getResident().getLastName(), currentResident.getResident().getEmail(),
                currentResident.getResident().getPhoneNumber(),
                securityCode, currentResident.getSuiteNumber(),
                currentResident.getBuilding().getName(), buildingAddress, buildingAddress, currentResident.getBuilding().getBuildingUniqueNumber(),
                SaveSharedPreferences.getEmail(getApplicationContext()), "STATUS_NOT_CONFIRMED", SaveSharedPreferences.getFirstName(getApplicationContext()),
                SaveSharedPreferences.getLasttName(getApplicationContext()));

        new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_INSERT_LOCKER_HISTORY, null, body);
    }

    private void insertParcel(boolean isVirtual){
        if(!isVirtual) {
            infoMessage.setText(getString(R.string.creating_locker_building_resident_association));
            RetroParcel body = new RetroParcel(0, currentLocker.getId(), currentResident.getId(), securityCode, Helper.STATUS_NOT_CONFIRMED);
            new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_INSERT_PARCEL, null, body);
        } else {
            infoMessage.setText(getString(R.string.creating_association_with_temporary_locker));
            RetroVirtualParcel body = new RetroVirtualParcel(0, currentLocker.getAddress().getId(), currentResident.getId(), securityCode,
                    currentLocker.getNumber(), currentLocker.getSize(), Helper.STATUS_NOT_CONFIRMED);
            if(currentLocker.getAddressDetail() != null){
                body.setAddressDetail(currentLocker.getAddressDetail());
            }
            new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_INSERT_VIRTUAL_PARCEL, null, body);
        }
    }

    private void insertNotification(boolean isVirtual){
        infoMessage.setText(R.string.sending_notification_to_resident);
        if(isVirtual){
            RetroVirtualParcelID lbr = new RetroVirtualParcelID(SaveSharedPreferences.getlastInsertedVirtualParcelID(getApplicationContext()));
            new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_INSERT_VIRTUAL_NOTIFICATION, null, lbr);
        } else {
            RetroParcelID lbr = new RetroParcelID(SaveSharedPreferences.getlastInsertedParcelID(getApplicationContext()));
            new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_INSERT_NOTIFICATION, null, lbr);
        }

    }

    @Override
    public void onResponse(int currentRequestId, Object result) {
        if (currentRequestId == Helper.REQUEST_DELETE_PARCEL){
            SaveSharedPreferences.setParcelNull(getApplicationContext());
            int lastInsertedLockerHistoryId = SaveSharedPreferences.getlastInsertedHistoryID(getApplicationContext());
            if (lastInsertedLockerHistoryId != 0){
                deletePreviousFailedLockerHistoryRecord(lastInsertedLockerHistoryId);
            } else {
                insertParcel(false);
            }
        }

        if (currentRequestId == Helper.REQUEST_DELETE_VIRTUAL_PARCEL){
            SaveSharedPreferences.setVirtualParcelNull(getApplicationContext());
            int lastInsertedLockerHistoryId = SaveSharedPreferences.getlastInsertedHistoryID(getApplicationContext());
            if (lastInsertedLockerHistoryId != 0){
                deletePreviousFailedLockerHistoryRecord(lastInsertedLockerHistoryId);
            } else {
                insertParcel(true);
            }
        }

        if (currentRequestId == Helper.REQUEST_DELETE_LOCKER_HISTORY){
            SaveSharedPreferences.setHistoryNull(getApplicationContext());
            if(currentLocker.getId() != 0){
                insertParcel(false);
            } else {
                insertParcel(true);
            }
        }

//        if (currentRequestId == Helper.REQUEST_LOCKER_BUILDING_RESIDENT){
//            if(result != null && result instanceof RetroParcelsList) {
//                if (((RetroParcelsList) result).getLBRs().size() > 0) {
//                    int lastInsertedLockerBuildingResidentID = ((RetroParcelsList) result).getLBRs().get(0).getId();
//                    SaveSharedPreferences.setlastInsertedParcelID(getApplicationContext(), lastInsertedLockerBuildingResidentID);
//                    infoMessage.setText(getString(R.string.creating_locker_history));
//                    insertLockerHistory();
//                } else {
//                    infoMessage.setText(getString(R.string.creating_locker_building_resident_association));
//                    insertParcel();
//                }
//            }
//        }

        if (currentRequestId == Helper.REQUEST_INSERT_PARCEL){
            if(result != null && result instanceof RetroParcel) {
                int lastInsertedLockerBuildingResidentID = ((RetroParcel) result).getId();
                SaveSharedPreferences.setlastInsertedParcelID(getApplicationContext(), lastInsertedLockerBuildingResidentID);
                //infoMessage.setText(getText(R.string.creating_locker_history));
                insertLockerHistory();
            }
        }
        if (currentRequestId == Helper.REQUEST_INSERT_VIRTUAL_PARCEL){
            if(result != null && result instanceof RetroVirtualParcel) {
                int lastInsertedVirtualParcelID = ((RetroVirtualParcel) result).getId();
                SaveSharedPreferences.setlastInsertedVirtualParcelID(getApplicationContext(), lastInsertedVirtualParcelID);
                //infoMessage.setText(getText(R.string.creating_locker_history));
                insertLockerHistory();
            }
        }

        if (currentRequestId == Helper.REQUEST_INSERT_LOCKER_HISTORY){
            if(result != null && result instanceof RetroLockerHistory) {
                int lastInsertedLockerHistoryID = ((RetroLockerHistory) result).getId();
                SaveSharedPreferences.setlastInsertedHistoryID(getApplicationContext(),lastInsertedLockerHistoryID);
                if(currentLocker.getId() != 0) {
                    insertNotification(false);
                } else {
                    insertNotification(true);
                }
            }
        }
        if(currentRequestId == Helper.REQUEST_INSERT_NOTIFICATION) {
            //
            handleSuccessAction();
        }

        if(currentRequestId == Helper.REQUEST_INSERT_VIRTUAL_NOTIFICATION) {
            //
            handleSuccessAction();
        }
    }

    private void handleSuccessAction() {
        progressBar.setVisibility(View.INVISIBLE);
        mainMessage.setText(getString(R.string.notification_was_sent));
        mainMessage.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        infoMessage.setText("");
        backButton.setVisibility(View.VISIBLE);
        popupWindow.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        SaveSharedPreferences.setParcelNull(getApplicationContext());
        SaveSharedPreferences.setHistoryNull(getApplicationContext());
        SaveSharedPreferences.setVirtualParcelNull(getApplicationContext());
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
