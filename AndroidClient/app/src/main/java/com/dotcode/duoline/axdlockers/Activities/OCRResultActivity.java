package com.dotcode.duoline.axdlockers.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OCRResultActivity extends AppCompatActivity {

//    public static final int REQUEST_CAMERA = 120;
//    public static final int PIC_CROP = 2;
//    private Uri cameraUri;
    private ArrayList<String> first4Strings = new ArrayList<String>();
    private AutoCompleteTextView fullName, unitNumber;
    private Menu menu;
    private boolean menuSearchDisabled = true;
//    File file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocrresult);
        setTitle("Finding Resident");
        fullName = (AutoCompleteTextView) findViewById(R.id.fullName);
        unitNumber = (AutoCompleteTextView) findViewById(R.id.unitNumber);
        ArrayList<String> lines = getIntent().getStringArrayListExtra("lines");
        if (lines != null) {
            first4Strings = getFirst3Lines(lines);
            if (first4Strings.size() > 0) {
                fullName.setText(first4Strings.get(0));
                if (first4Strings.size() > 1){
                    unitNumber.setText(first4Strings.get(1));
                }
            }
        }


        unitNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setSearchButtonStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setSearchButtonStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setSearchButtonStatus();
    }

    private void setSearchButtonStatus(){
        if(menu != null){
            boolean searchEnabled = !fullName.getText().toString().trim().equals("") || !unitNumber.getText().toString().trim().equals("");
            if (searchEnabled) {
                menu.getItem(1).setEnabled(true);
                menu.getItem(1).getIcon().setAlpha(255);
            } else {
                menu.getItem(1).setEnabled(false);
                menu.getItem(1).getIcon().setAlpha(130);
            }
        }
    }


    private ArrayList<String> getFirst3Lines(ArrayList<String> lines){
        //0 - name; 1 - unit; 2 - street; 3 - address
        ArrayList<String> l = new ArrayList<String>();
        if (lines.size() > 0) {
            l.add(lines.get(0).trim()); //0
        }
        if (lines.size() > 1) {
            String[] separatedByMinus = lines.get(1).split("-");
            String[] separatedByUnit = lines.get(1).split("(?i)UNIT");

            if (separatedByMinus.length > 1) {
                l.add(separatedByMinus[0].trim()); // 1
                if(separatedByMinus.length > 2){
                    String street = "";
                    for (int i = 1; i<separatedByMinus.length; i++){
                        street += separatedByMinus[i];
                    }
                    l.add(street.trim()); //2
                } else {
                    l.add(separatedByMinus[1].trim()); //2
                }
            }
            if (separatedByUnit.length > 1) {
                String unit = separatedByUnit[1].trim();
                l.add(unit.replaceAll("[^0-9]", "")); //1
                l.add(separatedByUnit[0].trim()); //2
            }
            if (l.size() == 1){
                l.add("");
                l.add("");
            }
        }
        if (lines.size() > 2) {
            l.add(lines.get(2).trim()); //3
        }
        return l;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan_with_search, menu);
        this.menu = menu;
        setSearchButtonStatus();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_find) {

            if (first4Strings.size() != 0){
                first4Strings.set(0, fullName.getText().toString().trim());
                first4Strings.set(1, unitNumber.getText().toString().trim());
            } else {
                first4Strings.add(fullName.getText().toString().trim());
                first4Strings.add(unitNumber.getText().toString().trim());
                first4Strings.add("");
                first4Strings.add("");
//                Toast.makeText(this, "No FULL NAME or UNIT NUMBER detected.", Toast.LENGTH_SHORT).show();
            }
            Intent i = new Intent(OCRResultActivity.this, ResidentsFilteredActivity.class);
            i.putStringArrayListExtra("first4lines", first4Strings);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_scan) {
//            cameraIntent();
            startActivity(new Intent(OCRResultActivity.this, ScanLabelActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    //    private void cameraIntent() {
//        Helper.deleteCache(getApplicationContext());
//        try {
//            File outputDir = getCacheDir(); // context being the Activity pointer
//            file = File.createTempFile("CAMERA_TEMP", ".JPG", outputDir);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        cameraUri = FileProvider.getUriForFile(getApplicationContext(), "com.dotcode.duoline.axdlockers", file);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
//        startActivityForResult(intent, REQUEST_CAMERA);
//
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        //super.onActivityResult(requestCode, resultCode, data);
//
//        //super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == AppCompatActivity.RESULT_OK) {
//            if (requestCode == REQUEST_CAMERA) {
//                Toast.makeText(this, cameraUri.getPath(), Toast.LENGTH_SHORT).show();
//                Intent cropIntent = new Intent("com.android.camera.action.CROP");
//                //indicate image type and Uri
//                cropIntent.setDataAndType(cameraUri, "image/*");
//                //set crop properties
//                cropIntent.putExtra("crop", "true");
//                //indicate aspect of desired crop
//                cropIntent.putExtra("aspectX", 1);
//                cropIntent.putExtra("aspectY", 1);
//                //indicate output X and Y
//                cropIntent.putExtra("outputX", 256);
//                cropIntent.putExtra("outputY", 256);
//                //retrieve data on return
//                cropIntent.putExtra("return-data", true);
//                //start the activity - we handle returning in onActivityResult
//                startActivityForResult(cropIntent, PIC_CROP);
//
//            }
//            if(requestCode == PIC_CROP){
//
//            }
//        }
//    }
}
