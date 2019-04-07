package com.dotcode.duoline.axdlockers.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class FinalPostActivity extends AppCompatActivity implements SetRequests.GetDataResponse {
    RetroLocker currentLocker;
    RetroFilteredResident currentResident;
    private TextView infoMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_post);
        infoMessage = (TextView) findViewById(R.id.infoMessage);
        int lastInsertedLockerBuildingResidentID = SaveSharedPreferences.getlastInsertedLBRID(getApplicationContext());
        int lastInsertedLockerHistoryID = SaveSharedPreferences.getlastInsertedLHID(getApplicationContext());
        if (lastInsertedLockerBuildingResidentID != 0){
            Map<String, String> param = new HashMap<String, String>();
            param.put("ID", String.valueOf(lastInsertedLockerBuildingResidentID));
            new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_DELETE_LOCKER_BUILDING_RESIDENT, param, null);
        } else if (lastInsertedLockerHistoryID != 0){
            Map<String, String> param = new HashMap<String, String>();
            param.put("ID", String.valueOf(lastInsertedLockerHistoryID));
            new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_DELETE_LOCKER_HISTORY, param, null);
        } else {
            Map<String, String> param = new HashMap<String, String>();
            param.put("lockerId", ""+currentLocker.getId());
            param.put("buildingResidentId", ""+currentResident.getId());
            new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_LOCKER_BUILDING_RESIDENT, param, null);
        }
    }

    @Override
    public void onResponse(int currentRequestId, Object result) {
        if (currentRequestId == Helper.REQUEST_DELETE_LOCKER_BUILDING_RESIDENT){
            SaveSharedPreferences.setLBRNull(getApplicationContext());
            int lastInsertedLockerHistoryId = SaveSharedPreferences.getlastInsertedLHID(getApplicationContext());
            if (lastInsertedLockerHistoryId != 0){
                Map<String, String> param = new HashMap<String, String>();
                param.put("ID", String.valueOf(lastInsertedLockerHistoryId));
                new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_DELETE_LOCKER_HISTORY, param, null);
            }
        }

        if (currentRequestId == Helper.REQUEST_DELETE_LOCKER_HISTORY){
            SaveSharedPreferences.setLHNull(getApplicationContext());
            Map<String, String> param = new HashMap<String, String>();
            param.put("lockerId", ""+currentLocker.getId());
            param.put("buildingResidentId", ""+currentResident.getId());
            new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_LOCKER_BUILDING_RESIDENT, param, null);
        }

        if (currentRequestId == Helper.REQUEST_LOCKER_BUILDING_RESIDENT){
            if(result != null && result instanceof RetroLockerBuildingResidentsList) {
                if (((RetroLockerBuildingResidentsList) result).getLBRs().size() > 0) {
                    int lastInsertedLockerBuildingResidentID = ((RetroLockerBuildingResidentsList) result).getLBRs().get(0).getId();
                    SaveSharedPreferences.setlastInsertedLBRID(getApplicationContext(), lastInsertedLockerBuildingResidentID);
                    infoMessage.setText("Creating locker history...");
                    String lockerAddress = currentLocker.getAddress().getStreetName() + ", " + currentLocker.getAddress().getCity().getName() + ", " +
                            currentLocker.getAddress().getCity().getState().getName() + ", " + currentLocker.getAddress().getZipCode();
                    String buildingAddress = currentResident.getBuilding().getName() + ", " + currentResident.getBuilding().getAddress().getStreetName() + ", " +
                            currentResident.getBuilding().getAddress().getCity().getName() + ", " + currentResident.getBuilding().getAddress().getCity().getState().getName() +", " +
                            currentResident.getBuilding().getAddress().getZipCode();
                    RetroLockerHistory body = new RetroLockerHistory(0, currentLocker.getQrCode(), currentLocker.getNumber(), currentLocker.getSize(),
                            lockerAddress, currentResident.getResident().getFirstName(), currentResident.getResident().getLastName(), currentResident.getResident().getEmail(),
                            currentResident.getResident().getPhoneNumber(), currentResident.getResident().getSecurityCode(), currentResident.getSuiteNumber(),
                            currentResident.getBuilding().getName(), buildingAddress, buildingAddress, currentResident.getBuilding().getBuildingUniqueNumber(),
                            SaveSharedPreferences.getEmail(getApplicationContext()));

                    new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_INSERT_LOCKER_HISTORY, null, body);
                } else {
                    infoMessage.setText("Creating locker - bulding - resident association...");
                    RetroLockerBuildingResident body = new RetroLockerBuildingResident(0, currentLocker.getId(), currentResident.getId());
                    new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_INSERT_LOCKER_BUILDING_RESIDENT, null, body);
                }
            }
        }

        if (currentRequestId == Helper.REQUEST_INSERT_LOCKER_BUILDING_RESIDENT){
            if(result != null && result instanceof RetroLockerBuildingResident) {
                int lastInsertedLockerBuildingResidentID = ((RetroLockerBuildingResident) result).getId();
                SaveSharedPreferences.setlastInsertedLBRID(getApplicationContext(), lastInsertedLockerBuildingResidentID);
                infoMessage.setText("Creating locker history...");
                String lockerAddress = currentLocker.getAddress().getStreetName() + ", " + currentLocker.getAddress().getCity().getName() + ", " +
                        currentLocker.getAddress().getCity().getState().getName() + ", " + currentLocker.getAddress().getZipCode();
                String buildingAddress = currentResident.getBuilding().getName() + ", " + currentResident.getBuilding().getAddress().getStreetName() + ", " +
                        currentResident.getBuilding().getAddress().getCity().getName() + ", " + currentResident.getBuilding().getAddress().getCity().getState().getName() + ", " +
                        currentResident.getBuilding().getAddress().getZipCode();
                RetroLockerHistory body = new RetroLockerHistory(0, currentLocker.getQrCode(), currentLocker.getNumber(), currentLocker.getSize(),
                        lockerAddress, currentResident.getResident().getFirstName(), currentResident.getResident().getLastName(), currentResident.getResident().getEmail(),
                        currentResident.getResident().getPhoneNumber(), currentResident.getResident().getSecurityCode(), currentResident.getSuiteNumber(),
                        currentResident.getBuilding().getName(), buildingAddress, buildingAddress, currentResident.getBuilding().getBuildingUniqueNumber(),
                        SaveSharedPreferences.getEmail(getApplicationContext()));
                new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_INSERT_LOCKER_HISTORY, null, body);
            }
        }
        if (currentRequestId == Helper.REQUEST_INSERT_LOCKER_HISTORY){
            if(result != null && result instanceof RetroLockerHistory) {
                int lastInsertedLockerHistoryID = ((RetroLockerHistory) result).getId();
                SaveSharedPreferences.setlastInsertedLHID(getApplicationContext(),lastInsertedLockerHistoryID);
                infoMessage.setText("Sending notification to resident...");
                RetroLockerBuildingResidentID lbr = new RetroLockerBuildingResidentID(SaveSharedPreferences.getlastInsertedLBRID(getApplicationContext()));
                new SetRequests(getApplicationContext(), FinalPostActivity.this, Helper.REQUEST_INSER_NOTIFICATION, null, lbr);
            }
        }
        if(currentRequestId == Helper.REQUEST_INSER_NOTIFICATION) {
            //
            infoMessage.setText("");
            SaveSharedPreferences.setLBRNull(getApplicationContext());
            SaveSharedPreferences.setLHNull(getApplicationContext());
        }
    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {

    }
}
