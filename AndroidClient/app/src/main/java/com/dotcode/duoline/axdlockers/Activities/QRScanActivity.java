package com.dotcode.duoline.axdlockers.Activities;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dotcode.duoline.axdlockers.Models.RetroFilteredResident;
import com.dotcode.duoline.axdlockers.Models.RetroLocker;
import com.dotcode.duoline.axdlockers.Models.RetroLockerHistory;
import com.dotcode.duoline.axdlockers.Models.RetroLockerList;
import com.dotcode.duoline.axdlockers.Models.RetroUser;
import com.dotcode.duoline.axdlockers.Models.RetroUserXRight;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.barcode.BarcodeReader;

public class QRScanActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener, SetRequests.GetDataResponse {

    private static final String TAG = QRScanActivity.class.getSimpleName();

    private BarcodeReader barcodeReader;
    private TextView textView;
    private boolean codeWasDetected = false;
    private String detectedqrCode;
    private RetroFilteredResident resident;
    private RetroLocker locker;
    private boolean userCanCreateLockers, userCanViewAddresses, userCanCreateParcels, addLockerOnly;
    private Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        // getting barcode instance
        textView = (TextView) findViewById(R.id.textView);
        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_fragment);
        addLockerOnly = SaveSharedPreferences.getAddLockerOnly(getApplicationContext());
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        codeWasDetected = false;
        barcodeReader.resumeScanning();
        resident = SaveSharedPreferences.getResident(getApplicationContext());
        super.onResume();
    }

    @Override
    public void onResponse(int currentRequestId, Object result) {
        if (currentRequestId == Helper.REQUEST_CHECK_USER){
            List<RetroUserXRight> userXRights = ((RetroUser) result).getUserXRights();
            userCanCreateLockers = Helper.userHaveRight(userXRights, "CREATE_LOCKER");
            userCanViewAddresses = Helper.userHaveRight(userXRights, "READ_ADDRESS");
            userCanCreateParcels =Helper.userHaveRight(userXRights, "CREATE_PACKAGES");
            Map<String, String> param = new HashMap<String, String>();
            param.put("filter[qrCode]", detectedqrCode);
            param.put("expand", "address.city.state,lockerXBuildingXResidents.buildingResident.resident,lockerXBuildingXResidents.buildingResident.building.address.city.state");
            new SetRequests(getApplicationContext(), QRScanActivity.this, Helper.REQUEST_LOCKERS, param, null);
        }
        if (currentRequestId == Helper.REQUEST_LOCKERS){
            if(result != null && result instanceof RetroLockerList) {

                if (userCanCreateParcels){
                    locker = ((RetroLockerList)result).getLocker();
                    if(locker != null) {
                        if (addLockerOnly) {
                            showAlert1(QRScanActivity.this, getString(R.string.already_exist), getString(R.string.locker_already_added));
                        } else {
                            if(locker.isLockerFree()) {
                                Intent i = new Intent(QRScanActivity.this, SecurityCodeActivity.class);
                                SaveSharedPreferences.setLocker(getApplicationContext(), locker);
                                startActivity(i);
                            } else {
                                showAlertLockerOccupied(QRScanActivity.this, "Locker occupied", "This locker appears in the system as not being free. Are you sure you want to use it?\n(This action will force release the locker in the system)");
                            }
                        }
                    } else {
                        if (userCanCreateLockers) {
                            showAlert(QRScanActivity.this, getString(R.string.locker_not_found), getString(R.string.locker_not_found_message), false);
                        } else {
                            if (!userCanViewAddresses) {
                                showAlert1(QRScanActivity.this, getString(R.string.no_proper_rights), getString(R.string.no_create_locker));
                            } else {
                                showAlert(QRScanActivity.this, getString(R.string.orphan_parcel), getString(R.string.no_create_locker1), true);
                            }
                        }
                    }
                } else {
                    showAlert1(QRScanActivity.this, getString(R.string.no_proper_rights), getString(R.string.no_create_parcel));
                }
            }
        }

        if(currentRequestId == Helper.REQUEST_DELETE_PARCEL){
            RetroLockerHistory body = new RetroLockerHistory(0,
                    locker.getQrCode(),
                    locker.getNumber(),
                    locker.getSize(),
                    locker.getLockerAddress(),
                    locker.getParcels().get(0).getBuildingResident().getResident().getFirstName(),
                    locker.getParcels().get(0).getBuildingResident().getResident().getLastName(),
                    locker.getParcels().get(0).getBuildingResident().getResident().getEmail(),
                    locker.getParcels().get(0).getBuildingResident().getResident().getPhoneNumber(),
                    locker.getParcels().get(0).getSecurityCode(),
                    locker.getParcels().get(0).getBuildingResident().getUnitNumber(),
                    locker.getParcels().get(0).getBuildingResident().getBuilding().getName(),
                    locker.getParcels().get(0).getBuildingResident().getBuilding().getBuildingAddress(),
                    locker.getParcels().get(0).getBuildingResident().getBuilding().getBuildingAddress(),
                    locker.getParcels().get(0).getBuildingResident().getBuilding().getBuildingUniqueNumber(),
                    SaveSharedPreferences.getEmail(getApplicationContext()),
                    "FORCED FREE",
                    SaveSharedPreferences.getFirstName(getApplicationContext()),
                    SaveSharedPreferences.getLasttName(getApplicationContext()));

            new SetRequests(getApplicationContext(), QRScanActivity.this, Helper.REQUEST_INSERT_LOCKER_HISTORY, null, body);
        }

        if(currentRequestId == Helper.REQUEST_INSERT_LOCKER_HISTORY){
            showAlert1(QRScanActivity.this, "LOCKER FREE", "The locker was set to FREE. Scan the QRCode again to use it.");
        }
    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {

    }

    private void showAlertLockerOccupied(Context ctx, String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(msg);

        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                codeWasDetected = false;
                barcodeReader.resumeScanning();
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int parcelId = locker.getParcels().get(0).getId();
                Map<String, String> param = new HashMap<String, String>();
                param.put("ID", String.valueOf(parcelId));
                new SetRequests(getApplicationContext(), QRScanActivity.this, Helper.REQUEST_DELETE_PARCEL, param, null);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showAlert1(Context ctx, String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(msg);

        builder.setCancelable(false);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                codeWasDetected = false;
                barcodeReader.resumeScanning();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showAlert(Context ctx, String title, String msg, final boolean virtualLocker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(msg);
//        builder.setIcon(R.drawable.ic_error_outline_yellow_24dp);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                codeWasDetected = false;
                barcodeReader.resumeScanning();
                dialog.dismiss();

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(QRScanActivity.this, AddLockerActivity.class);
                i.putExtra("qrCode", detectedqrCode);
                if(virtualLocker){
                    i.putExtra("virtualLocker", true);
                }
                startActivity(i);
            }
        });
        builder.show();
    }

    @Override
    public void onScanned(final Barcode barcode) {

        if(!codeWasDetected) {
            barcodeReader.pauseScanning();
            final String qrCode = barcode.displayValue;

            Log.e(TAG, "onScanned: " + qrCode);
            barcodeReader.playBeep();
            codeWasDetected = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (barcode.format == Barcode.QR_CODE){
                        detectedqrCode = qrCode;
                        if (SaveSharedPreferences.getUserId(getApplicationContext()) != 0) {
                            new SetRequests(getApplicationContext(), QRScanActivity.this, Helper.REQUEST_CHECK_USER, null, null);
                        }
                    }
                }
            });
        }

    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {
        Log.e(TAG, "onScannedMultiple: " + barcodes.size());

        String codes = "";
        for (Barcode barcode : barcodes) {
            codes += barcode.displayValue + ", ";
        }

        final String finalCodes = codes;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Barcodes: " + finalCodes, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(getApplicationContext(), getString(R.string.camera_permission_denied), Toast.LENGTH_LONG).show();
        finish();
    }
}
