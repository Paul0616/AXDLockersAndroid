package com.dotcode.duoline.axdlockers.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dotcode.duoline.axdlockers.R;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class MainActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private BarcodeReader barcodeReader;
    private TextView textView;
    private boolean codeWasDetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        try
//        {
//            this.getSupportActionBar().hide();
//        }
//        catch (NullPointerException e){}
        setContentView(R.layout.activity_main);

        // getting barcode instance
        textView = (TextView) findViewById(R.id.textView);
        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_fragment);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  showAlert(MainActivity.this, "Test", "Mesaj de test");
            }
        });
    }


    private void showAlert(Context ctx, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(msg);
//        builder.setIcon(R.drawable.ic_error_outline_yellow_24dp);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                codeWasDetected = false;
                barcodeReader.resumeScanning();

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
                    showAlert(MainActivity.this, "Test", qrCode);
                }
            });
           // textView.setText(qrCode);
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
        Toast.makeText(getApplicationContext(), "Camera permission denied!", Toast.LENGTH_LONG).show();
        finish();
    }
}
