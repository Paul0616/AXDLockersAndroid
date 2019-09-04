package com.dotcode.duoline.axdlockers.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MotionEventCompat;
import androidx.exifinterface.media.ExifInterface;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Dot;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class ImageActivity extends AppCompatActivity {

    private ImageView iv, topLeftDot, topLeftCircle,topRightCircle, bottomLeftCircle, bottomRightCircle, croppingRectImageView;
    private TextView wTextView, hTextView;
    private Bitmap rotatedBitmap;
    int containerWidth, containerHeight;
    private Button detectButton;
    private String detectedString;
    //Dot d1;


    private float mLastTouchX;
    private float mLastTouchY;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        detectButton = (Button) findViewById(R.id.detectButton);

        String ap = getIntent().getStringExtra("absolutePath");
        Bitmap bitmap = BitmapFactory.decodeFile(ap);
        try {
            ExifInterface ei = new ExifInterface(ap);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);


            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        iv = (ImageView) findViewById(R.id.imageViewTaken);

        topLeftDot = (ImageView) findViewById(R.id.topLeftDot);
        topLeftCircle = (ImageView) findViewById(R.id.topLeftCircle);

        topRightCircle = (ImageView) findViewById(R.id.topRightCircle);

        bottomLeftCircle = (ImageView) findViewById(R.id.bottomLeftCircle);

        bottomRightCircle = (ImageView) findViewById(R.id.bottomRightCircle);

        wTextView = (TextView) findViewById(R.id.textView14);
        hTextView = (TextView) findViewById(R.id.textView15);
        croppingRectImageView = (ImageView) findViewById(R.id.croppingRect);


        iv.setImageBitmap(rotatedBitmap);
        //d1 = new Dot(this, constraintLayout);

        //Toast.makeText(this, "Final image size: w:" + rotatedBitmap.getWidth() + "x h:" + rotatedBitmap.getHeight(), Toast.LENGTH_LONG).show();

//        iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if (iv.getScaleType() == ImageView.ScaleType.FIT_CENTER )
////                     iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
////                else
////                    iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                float x = wTextView.getX();
//                float y = wTextView.getY();
//
//                wTextView.setX(iv.getX());
//            }
//        });
            detectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int startX = croppingRectImageView.getLeft();
                    int startY = croppingRectImageView.getTop();
                    int w = croppingRectImageView.getWidth();
                    int h = croppingRectImageView.getHeight();
                    Bitmap resizedbitmap = Bitmap.createBitmap(rotatedBitmap, startX,startY,w, h);
                    TextRecognizer txtRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                    if (!txtRecognizer.isOperational()) {
                        detectedString = "";
                        //txtView.setText(R.string.error_prompt);
                    } else {
                        Frame frame = new Frame.Builder().setBitmap(resizedbitmap).build();
                        SparseArray items = txtRecognizer.detect(frame);
                        StringBuilder strBuilder = new StringBuilder();
                        for (int i = 0; i < items.size(); i++) {
                            TextBlock item = (TextBlock) items.valueAt(i);
                            strBuilder.append(item.getValue());
                            strBuilder.append("/");
                            for (Text line : item.getComponents()) {
                                //extract scanned text lines here
                                Log.v("lines", line.getValue());
                                String lines = line.getValue();
                                for (Text element : line.getComponents()) {
                                    //extract scanned text words here
                                    Log.v("element", element.getValue());
                                    Rect elB = element.getBoundingBox();
                                    String elements = element.getValue();
                                }
                            }
                        }
                        detectedString = strBuilder.toString().substring(0, strBuilder.toString().length() - 1);

                    }
                }
            });



            croppingRectImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) croppingRectImageView.getLayoutParams();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastTouchX = event.getX();
                        mLastTouchY = event.getY();
                        wTextView.setText(""+mLastTouchX);
                        hTextView.setText(""+mLastTouchY);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        int dx = (int) (event.getX() - mLastTouchX);
                        int dy = (int) (event.getY() - mLastTouchY);

                        layoutParams.leftMargin += dx;
                        layoutParams.topMargin += dy;
                        croppingRectImageView.setLayoutParams(layoutParams);
                        updateDots();
                    default:
                        break;
                }
                return true;
            }
        });


        topLeftCircle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ConstraintLayout.LayoutParams topLeftCircleLayoutParams = (ConstraintLayout.LayoutParams) topLeftCircle.getLayoutParams();
                ConstraintLayout.LayoutParams topRightCirclelayoutParams = (ConstraintLayout.LayoutParams) topRightCircle.getLayoutParams();
                ConstraintLayout.LayoutParams bottomLeftCirclelayoutParams = (ConstraintLayout.LayoutParams) bottomLeftCircle.getLayoutParams();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x_cord = (int) event.getRawX();
                        int y_cord = (int) event.getRawY();

                        if (x_cord > containerWidth-topLeftCircle.getWidth()+25) {
                            x_cord = containerWidth-topLeftCircle.getWidth()+25;
                        }
                        if (y_cord > containerHeight-topLeftCircle.getHeight()+75) {
                            y_cord = containerHeight-topLeftCircle.getHeight()+75;
                        }

                        topLeftCircleLayoutParams.leftMargin = x_cord-25;
                        topLeftCircleLayoutParams.topMargin = y_cord-75;
                        topLeftCircle.setLayoutParams(topLeftCircleLayoutParams);

                        topRightCirclelayoutParams.topMargin = topLeftCircleLayoutParams.topMargin;
                        topRightCircle.setLayoutParams(topRightCirclelayoutParams);

                        bottomLeftCirclelayoutParams.leftMargin = topLeftCircleLayoutParams.leftMargin;
                        bottomLeftCircle.setLayoutParams(bottomLeftCirclelayoutParams);

                        updateCroppingRect();

                        showRectCoord();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        topRightCircle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ConstraintLayout.LayoutParams topRightCircleLayoutParams = (ConstraintLayout.LayoutParams) topRightCircle.getLayoutParams();
                ConstraintLayout.LayoutParams bottomRightCircleLayoutParams = (ConstraintLayout.LayoutParams) bottomRightCircle.getLayoutParams();
                ConstraintLayout.LayoutParams topLeftCircleLayoutParams = (ConstraintLayout.LayoutParams) topLeftCircle.getLayoutParams();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x_cord = (int) event.getRawX();
                        int y_cord = (int) event.getRawY();

                        if (x_cord > containerWidth-topRightCircle.getWidth()+25) {
                            x_cord = containerWidth-topRightCircle.getWidth()+25;
                        }
                        if (y_cord > containerHeight-topRightCircle.getHeight()+75) {
                            y_cord = containerHeight-topRightCircle.getHeight()+75;
                        }

                        topRightCircleLayoutParams.leftMargin = x_cord-25;
                        topRightCircleLayoutParams.topMargin = y_cord-75;
                        topRightCircle.setLayoutParams(topRightCircleLayoutParams);

                        topLeftCircleLayoutParams.topMargin = topRightCircleLayoutParams.topMargin;
                        topLeftCircle.setLayoutParams(topLeftCircleLayoutParams);
                        bottomRightCircleLayoutParams.leftMargin = topRightCircleLayoutParams.leftMargin;
                        bottomRightCircle.setLayoutParams(bottomRightCircleLayoutParams);


                        updateCroppingRect();

                        showRectCoord();

                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        bottomRightCircle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ConstraintLayout.LayoutParams topRightCircleLayoutParams = (ConstraintLayout.LayoutParams) topRightCircle.getLayoutParams();
                ConstraintLayout.LayoutParams bottomRightCircleLayoutParams = (ConstraintLayout.LayoutParams) bottomRightCircle.getLayoutParams();
                ConstraintLayout.LayoutParams bottomLeftCircleLayoutParams = (ConstraintLayout.LayoutParams) bottomLeftCircle.getLayoutParams();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x_cord = (int) event.getRawX();
                        int y_cord = (int) event.getRawY();

                        if (x_cord > containerWidth-bottomRightCircle.getWidth()+25) {
                            x_cord = containerWidth-bottomRightCircle.getWidth()+25;
                        }
                        if (y_cord > containerHeight-bottomRightCircle.getHeight()+75) {
                            y_cord = containerHeight-bottomRightCircle.getHeight()+75;
                        }

                        bottomRightCircleLayoutParams.leftMargin = x_cord-25;
                        bottomRightCircleLayoutParams.topMargin = y_cord-75;
                        bottomRightCircle.setLayoutParams(bottomRightCircleLayoutParams);

                        topRightCircleLayoutParams.leftMargin = bottomRightCircleLayoutParams.leftMargin;
                        topRightCircle.setLayoutParams(topRightCircleLayoutParams);
                        bottomLeftCircleLayoutParams.topMargin = bottomRightCircleLayoutParams.topMargin;
                        bottomLeftCircle.setLayoutParams(bottomLeftCircleLayoutParams);


                        updateCroppingRect();

                        showRectCoord();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        bottomLeftCircle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ConstraintLayout.LayoutParams topLeftCircleLayoutParams = (ConstraintLayout.LayoutParams) topLeftCircle.getLayoutParams();
                ConstraintLayout.LayoutParams bottomRightCircleLayoutParams = (ConstraintLayout.LayoutParams) bottomRightCircle.getLayoutParams();
                ConstraintLayout.LayoutParams bottomLeftCircleLayoutParams = (ConstraintLayout.LayoutParams) bottomLeftCircle.getLayoutParams();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x_cord = (int) event.getRawX();
                        int y_cord = (int) event.getRawY();

                        if (x_cord > containerWidth-bottomLeftCircle.getWidth()+25) {
                            x_cord = containerWidth-bottomLeftCircle.getWidth()+25;
                        }
                        if (y_cord > containerHeight-bottomLeftCircle.getHeight()+75) {
                            y_cord = containerHeight-bottomLeftCircle.getHeight()+75;
                        }

                        bottomLeftCircleLayoutParams.leftMargin = x_cord-25;
                        bottomLeftCircleLayoutParams.topMargin = y_cord-75;
                        bottomLeftCircle.setLayoutParams(bottomLeftCircleLayoutParams);

                        topLeftCircleLayoutParams.leftMargin = bottomLeftCircleLayoutParams.leftMargin;
                        topLeftCircle.setLayoutParams(topLeftCircleLayoutParams);
                        bottomRightCircleLayoutParams.topMargin = bottomLeftCircleLayoutParams.topMargin;
                        bottomRightCircle.setLayoutParams(bottomRightCircleLayoutParams);


                        updateCroppingRect();

                        showRectCoord();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void showRectCoord() {

        Rect cr = new Rect(croppingRectImageView.getLeft(), croppingRectImageView.getTop(), croppingRectImageView.getRight(), croppingRectImageView.getBottom());
//        croppingRectImageView.getDrawingRect(cr);
        wTextView.setText("LEFT:" + cr.left + " px\nTOP:" + cr.top+ " px");
        hTextView.setText("WIDTH:" + cr.width() + " px\nHEIGHT:" + cr.height()+ " px");

    }

    private void updateCroppingRect() {

        ConstraintLayout.LayoutParams croppingRectLayoutParams = (ConstraintLayout.LayoutParams) croppingRectImageView.getLayoutParams();
        ConstraintLayout.LayoutParams topLeftCircleLayoutParams = (ConstraintLayout.LayoutParams) topLeftCircle.getLayoutParams();
        ConstraintLayout.LayoutParams topRightCircleLayoutParams = (ConstraintLayout.LayoutParams) topRightCircle.getLayoutParams();
        ConstraintLayout.LayoutParams bottomLeftCircleLayoutParams = (ConstraintLayout.LayoutParams) bottomLeftCircle.getLayoutParams();

        croppingRectLayoutParams.topMargin = topLeftCircleLayoutParams.topMargin + topLeftCircle.getHeight() / 2;
        croppingRectLayoutParams.leftMargin = topLeftCircleLayoutParams.leftMargin + topLeftCircle.getWidth() / 2;
        croppingRectLayoutParams.width = topRightCircleLayoutParams.leftMargin - topLeftCircleLayoutParams.leftMargin;//int) (topRightCircle.getX() - topLeftCircle.getX());
        croppingRectLayoutParams.height = bottomLeftCircleLayoutParams.topMargin - topLeftCircleLayoutParams.topMargin;//(int) (bottomRightCircle.getY() - topRightCircle.getY());
        croppingRectImageView.setLayoutParams(croppingRectLayoutParams);
    }

    private void updateDots() {

        ConstraintLayout.LayoutParams croppingRectLayoutParams = (ConstraintLayout.LayoutParams) croppingRectImageView.getLayoutParams();
        ConstraintLayout.LayoutParams topLeftCircleLayoutParams = (ConstraintLayout.LayoutParams) topLeftCircle.getLayoutParams();
        ConstraintLayout.LayoutParams topRightCircleLayoutParams = (ConstraintLayout.LayoutParams) topRightCircle.getLayoutParams();
        ConstraintLayout.LayoutParams bottomLeftCircleLayoutParams = (ConstraintLayout.LayoutParams) bottomLeftCircle.getLayoutParams();
        ConstraintLayout.LayoutParams bottomRightCircleLayoutParams = (ConstraintLayout.LayoutParams) bottomRightCircle.getLayoutParams();

        topLeftCircleLayoutParams.topMargin = croppingRectLayoutParams.topMargin - topLeftCircle.getHeight() / 2;
        topLeftCircleLayoutParams.leftMargin = croppingRectLayoutParams.leftMargin - topLeftCircle.getWidth() / 2;
        topLeftCircle.setLayoutParams(topLeftCircleLayoutParams);

        topRightCircleLayoutParams.topMargin = croppingRectLayoutParams.topMargin - topRightCircle.getHeight() / 2;
        topRightCircleLayoutParams.leftMargin = topLeftCircleLayoutParams.leftMargin + croppingRectLayoutParams.width;
        topRightCircle.setLayoutParams(topRightCircleLayoutParams);

        bottomLeftCircleLayoutParams.topMargin = topLeftCircleLayoutParams.topMargin + croppingRectLayoutParams.height;
        bottomLeftCircleLayoutParams.leftMargin = croppingRectLayoutParams.leftMargin - bottomLeftCircle.getWidth() / 2;
        bottomLeftCircle.setLayoutParams(bottomLeftCircleLayoutParams);

        bottomRightCircleLayoutParams.topMargin = topLeftCircleLayoutParams.topMargin + croppingRectLayoutParams.height;
        bottomRightCircleLayoutParams.leftMargin = topLeftCircleLayoutParams.leftMargin + croppingRectLayoutParams.width;
        bottomRightCircle.setLayoutParams(bottomRightCircleLayoutParams);
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        wTextView.setText("W:"+rotatedBitmap.getWidth()+" px/"+iv.getWidth());
        hTextView.setText("H:"+rotatedBitmap.getHeight()+" px/"+iv.getHeight());
//        wTextView.setText("W:"+croppingRectImageView.getWidth()+" px/"+croppingRectImageView.getX());
//        hTextView.setText("H:"+croppingRectImageView.getHeight()+" px/"+croppingRectImageView.getY());
        containerWidth = iv.getWidth();
        containerHeight = iv.getHeight();
//        d1.setXPos(300);
//        d1.setYPos(100);
        super.onWindowFocusChanged(hasFocus);
    }


}
