package com.dotcode.duoline.axdlockers.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;

import java.io.File;
import java.io.IOException;

public class OCRResultActivity extends AppCompatActivity {

    public static final int REQUEST_CAMERA = 120;
    public static final int PIC_CROP = 2;
    private Uri cameraUri;
    File file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocrresult);
        setTitle("Finding Resident");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan_with_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_find) {
            Toast.makeText(this, "FIND", Toast.LENGTH_SHORT).show();
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
