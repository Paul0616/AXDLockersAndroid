package com.dotcode.duoline.axdlockers.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Rational;
import android.util.Size;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ScanLabelActivity extends AppCompatActivity {

    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"}; //, "android.permission.WRITE_EXTERNAL_STORAGE"
    TextureView textureView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_label);

        textureView = findViewById(R.id.view_finder);


    }

    @Override
    protected void onResume() {
        super.onResume();
//         OrientationEventListener orientationEventListener = new OrientationEventListener(getApplicationContext(), SensorManager.SENSOR_DELAY_NORMAL) {
//            @Override
//            public void onOrientationChanged(int orientation) {
//                Toast.makeText(ScanLabelActivity.this, ""+orientation, Toast.LENGTH_LONG).show();
//                //startCamera();
//            }
//        };
//        orientationEventListener.enable();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //Here you can get the size!
        if(allPermissionsGranted()){
            //rotation = Display.getRotation();

            startCamera(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void startCamera() {

        CameraX.unbindAll();

        Rational aspectRatio = new Rational (textureView.getWidth(), textureView.getHeight());
        Size screen = new Size(textureView.getWidth(), textureView.getHeight()); //size of the screen


        PreviewConfig pConfig = new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio).build();
        Preview preview = new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    //to update the surface texture we  have to destroy it first then re-add it
                    @Override
                    public void onUpdated(Preview.PreviewOutput output){

                        ViewGroup parent = (ViewGroup) textureView.getParent();
                        parent.removeView(textureView);
                        parent.addView(textureView, 0);

                        textureView.setSurfaceTexture(output.getSurfaceTexture());
                        updateTransform();

                    }
                });


        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).setLensFacing(CameraX.LensFacing.BACK).setTargetAspectRatio(aspectRatio).setTargetResolution(screen).build();
        final ImageCapture imgCap = new ImageCapture(imageCaptureConfig);
        // findViewById(R.id.imgCapture)

       textureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = null;
                Helper.deleteCache(getApplicationContext());
                try {
                    File outputDir = getCacheDir(); // context being the Activity pointer
                    file = File.createTempFile("CAMERA_TEMP", ".JPG", outputDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                       // Uri cameraUri = FileProvider.getUriForFile(getApplicationContext(), "com.dotcode.duoline.axdlockers", file);
//                        String msg = "Pic captured at " + file.getAbsolutePath();
//                       Bitmap myBitmap = BitmapFactory.decodeFile(file.get AbsolutePath());
//                        Bitmap bitmapScaled = Bitmap.createScaledBitmap(myBitmap, textureView.getWidth(),textureView.getHeight(), true);
//
//                        msg = "-"+ bitmapScaled.getWidth()+"-"+bitmapScaled.getHeight();
//                        Toast.makeText(getBaseContext(), msg,Toast.LENGTH_LONG).show();
                        Intent i = new Intent(ScanLabelActivity.this, ImageActivity.class);
                        i.putExtra("absolutePath", file.getAbsolutePath());
                        startActivity(i);
                    }


                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        String msg = "Pic capture failed : " + message;
                        Toast.makeText(getBaseContext(), msg,Toast.LENGTH_LONG).show();
                        if(cause != null){
                            cause.printStackTrace();
                        }
                    }
                });
            }
        });


        //bind to lifecycle:
        CameraX.bindToLifecycle((LifecycleOwner)this, preview, imgCap);
    }

    private void updateTransform(){
        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = textureView.getDisplay().getRotation();

        switch(rotation){
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        mx.postRotate(-(float)rotationDgr, cX, cY);
        textureView.setTransform(mx);

        float scaledWidth, scaledHeight;
        if (w > h) {
            scaledHeight = w;
            scaledWidth = w;
        } else {
            scaledHeight = h;
            scaledWidth = w;
        }
        float xScale = scaledWidth / w;
        float yScale = scaledHeight / h;
        mx.preScale(xScale,yScale,cX,cY);
        textureView.setTransform(mx);
        textureView.getSurfaceTexture().setDefaultBufferSize(textureView.getWidth(),textureView.getHeight());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}
