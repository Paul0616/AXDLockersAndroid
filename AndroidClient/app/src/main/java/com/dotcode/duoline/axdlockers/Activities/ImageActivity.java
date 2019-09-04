package com.dotcode.duoline.axdlockers.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.exifinterface.media.ExifInterface;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.TextRectangle;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.List;

public class ImageActivity extends AppCompatActivity {

    private ImageView iv, topLeftCircle,topRightCircle, bottomLeftCircle, bottomRightCircle, croppingRectImageView;
    private TextView wTextView, hTextView;
    private Bitmap rotatedBitmap;
    int containerWidth, containerHeight, circleWidth, dotWidth, gap;
    private Button detectButton;
    private String detectedString;
    private ConstraintLayout constraintLayout;
    //Dot d1;


    private float mLastTouchX;
    private float mLastTouchY;
    private List<Text> lines;



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

       // topLeftDot = (ImageView) findViewById(R.id.topLeftDot);
        topLeftCircle = (ImageView) findViewById(R.id.topLeftCircle);

        topRightCircle = (ImageView) findViewById(R.id.topRightCircle);

        bottomLeftCircle = (ImageView) findViewById(R.id.bottomLeftCircle);

        bottomRightCircle = (ImageView) findViewById(R.id.bottomRightCircle);

        wTextView = (TextView) findViewById(R.id.textView14);
        hTextView = (TextView) findViewById(R.id.textView15);
        croppingRectImageView = (ImageView) findViewById(R.id.croppingRect);
        constraintLayout = (ConstraintLayout) findViewById(R.id.contraintLayoutBase);


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
                        if (items.size() > 0)
                            detectedString = "";
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
                                    elB.left = elB.left + croppingRectImageView.getLeft();
                                    elB.top = elB.top + croppingRectImageView.getTop();
                                    elB.bottom = elB.bottom + croppingRectImageView.getTop();
                                    elB.right = elB.right + croppingRectImageView.getLeft();
                                    constraintLayout.addView(new TextRectangle(getApplicationContext(), elB));
                                    String elements = element.getValue();
                                }
                            }
                        }
                        detectedString = strBuilder.toString().substring(0, strBuilder.toString().length() - 1);
                        Toast.makeText(ImageActivity.this, detectedString, Toast.LENGTH_LONG).show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(ImageActivity.this, OCRResultActivity.class);

                            }
                        }, 1000);
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
//                        wTextView.setText(""+mLastTouchX);
//                        hTextView.setText(""+mLastTouchY);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        int dx = (int) (event.getX() - mLastTouchX);
                        int dy = (int) (event.getY() - mLastTouchY);

                        if ((layoutParams.leftMargin + dx) >= dotWidth/2 &&
                                ((layoutParams.leftMargin + dx) <= (containerWidth - layoutParams.width - dotWidth/2))){
                            layoutParams.leftMargin += dx;
                        }

                        if ((layoutParams.topMargin + dy) >= dotWidth/2 &&
                                ((layoutParams.topMargin + dy) <= (containerHeight - layoutParams.height - dotWidth/2))){
                            layoutParams.topMargin += dy;
                        }

                        //croppingRectImageView.setLayoutParams(layoutParams);
                        croppingRectImageView.requestLayout();
                        updateDots();
                        showRectCoord();
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

                        if (x_cord > containerWidth- circleWidth +25) {
                            x_cord = containerWidth- circleWidth +25;
                        }
                        if (y_cord > containerHeight- circleWidth +75) {
                            y_cord = containerHeight- circleWidth +75;
                        }

                        topLeftCircleLayoutParams.leftMargin = x_cord-25;
                        topLeftCircleLayoutParams.topMargin = y_cord-75;
                        //topLeftCircle.setLayoutParams(topLeftCircleLayoutParams);
                        topLeftCircle.requestLayout();

                        topRightCirclelayoutParams.topMargin = topLeftCircleLayoutParams.topMargin;
                        //topRightCircle.setLayoutParams(topRightCirclelayoutParams);
                        topLeftCircle.requestLayout();

                        bottomLeftCirclelayoutParams.leftMargin = topLeftCircleLayoutParams.leftMargin;
                        //bottomLeftCircle.setLayoutParams(bottomLeftCirclelayoutParams);
                        bottomLeftCircle.requestLayout();

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

                        if (x_cord > containerWidth- circleWidth +25) {
                            x_cord = containerWidth- circleWidth +25;
                        }
                        if (y_cord > containerHeight- circleWidth +75) {
                            y_cord = containerHeight- circleWidth +75;
                        }

                        topRightCircleLayoutParams.leftMargin = x_cord-25;
                        topRightCircleLayoutParams.topMargin = y_cord-75;
                        //topRightCircle.setLayoutParams(topRightCircleLayoutParams);
                        topRightCircle.requestLayout();

                        topLeftCircleLayoutParams.topMargin = topRightCircleLayoutParams.topMargin;
                        //topLeftCircle.setLayoutParams(topLeftCircleLayoutParams);
                        topLeftCircle.requestLayout();
                        bottomRightCircleLayoutParams.leftMargin = topRightCircleLayoutParams.leftMargin;
//                        bottomRightCircle.setLayoutParams(bottomRightCircleLayoutParams);
                        bottomRightCircle.requestLayout();


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

                        if (x_cord > containerWidth- circleWidth +25) {
                            x_cord = containerWidth- circleWidth +25;
                        }
                        if (y_cord > containerHeight- circleWidth +75) {
                            y_cord = containerHeight- circleWidth +75;
                        }

                        bottomRightCircleLayoutParams.leftMargin = x_cord-25;
                        bottomRightCircleLayoutParams.topMargin = y_cord-75;
//                        bottomRightCircle.setLayoutParams(bottomRightCircleLayoutParams);
                        bottomRightCircle.requestLayout();

                        topRightCircleLayoutParams.leftMargin = bottomRightCircleLayoutParams.leftMargin;
//                        topRightCircle.setLayoutParams(topRightCircleLayoutParams);
                        topRightCircle.requestLayout();
                        bottomLeftCircleLayoutParams.topMargin = bottomRightCircleLayoutParams.topMargin;
//                        bottomLeftCircle.setLayoutParams(bottomLeftCircleLayoutParams);
                        bottomLeftCircle.requestLayout();

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

                        if (x_cord > containerWidth- circleWidth +25) {
                            x_cord = containerWidth- circleWidth +25;
                        }
                        if (y_cord > containerHeight- circleWidth +75) {
                            y_cord = containerHeight- circleWidth +75;
                        }

                        bottomLeftCircleLayoutParams.leftMargin = x_cord-25;
                        bottomLeftCircleLayoutParams.topMargin = y_cord-75;
//                        bottomLeftCircle.setLayoutParams(bottomLeftCircleLayoutParams);
                        bottomLeftCircle.requestLayout();

                        topLeftCircleLayoutParams.leftMargin = bottomLeftCircleLayoutParams.leftMargin;
//                        topLeftCircle.setLayoutParams(topLeftCircleLayoutParams);
                        topLeftCircle.requestLayout();
                        bottomRightCircleLayoutParams.topMargin = bottomLeftCircleLayoutParams.topMargin;
//                        bottomRightCircle.setLayoutParams(bottomRightCircleLayoutParams);
                        bottomRightCircle.requestLayout();

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

        int minX = Math.min(topRightCircleLayoutParams.leftMargin, topLeftCircleLayoutParams.leftMargin);
        int minY = Math.min(bottomLeftCircleLayoutParams.topMargin, topLeftCircleLayoutParams.topMargin);

        croppingRectLayoutParams.topMargin = minY + dotWidth /2;
        croppingRectLayoutParams.leftMargin = minX + dotWidth /2;
        croppingRectLayoutParams.width = Math.abs(topRightCircleLayoutParams.leftMargin - topLeftCircleLayoutParams.leftMargin) + gap;//int) (topRightCircle.getX() - topLeftCircle.getX());
        croppingRectLayoutParams.height = Math.abs(bottomLeftCircleLayoutParams.topMargin - topLeftCircleLayoutParams.topMargin) + gap;//(int) (bottomRightCircle.getY() - topRightCircle.getY());
        //croppingRectImageView.setLayoutParams(croppingRectLayoutParams);
        croppingRectImageView.requestLayout();
    }

    private void updateDots() {

        ConstraintLayout.LayoutParams croppingRectLayoutParams = (ConstraintLayout.LayoutParams) croppingRectImageView.getLayoutParams();
        ConstraintLayout.LayoutParams topLeftCircleLayoutParams = (ConstraintLayout.LayoutParams) topLeftCircle.getLayoutParams();
        ConstraintLayout.LayoutParams topRightCircleLayoutParams = (ConstraintLayout.LayoutParams) topRightCircle.getLayoutParams();
        ConstraintLayout.LayoutParams bottomLeftCircleLayoutParams = (ConstraintLayout.LayoutParams) bottomLeftCircle.getLayoutParams();
        ConstraintLayout.LayoutParams bottomRightCircleLayoutParams = (ConstraintLayout.LayoutParams) bottomRightCircle.getLayoutParams();

        topLeftCircleLayoutParams.topMargin = croppingRectLayoutParams.topMargin - dotWidth /2;
        topLeftCircleLayoutParams.leftMargin = croppingRectLayoutParams.leftMargin - dotWidth /2;
//        topLeftCircle.setLayoutParams(topLeftCircleLayoutParams);
        topLeftCircle.requestLayout();

        topRightCircleLayoutParams.topMargin = croppingRectLayoutParams.topMargin - dotWidth/2;
        topRightCircleLayoutParams.leftMargin = topLeftCircleLayoutParams.leftMargin + croppingRectLayoutParams.width - gap;
//        topRightCircle.setLayoutParams(topRightCircleLayoutParams);
        topRightCircle.requestLayout();

        bottomLeftCircleLayoutParams.topMargin = topLeftCircleLayoutParams.topMargin + croppingRectLayoutParams.height - gap;
        bottomLeftCircleLayoutParams.leftMargin = croppingRectLayoutParams.leftMargin - dotWidth/2;
//        bottomLeftCircle.setLayoutParams(bottomLeftCircleLayoutParams);
        bottomLeftCircle.requestLayout();

        bottomRightCircleLayoutParams.topMargin = topLeftCircleLayoutParams.topMargin + croppingRectLayoutParams.height - gap;
        bottomRightCircleLayoutParams.leftMargin = topLeftCircleLayoutParams.leftMargin + croppingRectLayoutParams.width - gap;
//        bottomRightCircle.setLayoutParams(bottomRightCircleLayoutParams);
        bottomRightCircle.requestLayout();
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

        containerWidth = iv.getWidth();
        containerHeight = iv.getHeight();

        //marginOffset is how much cropping rect must approach to margin of screen
       // marginOffset = 0;
        dotWidth = 0;//topLeftDot.getWidth();
        circleWidth = topLeftCircle.getWidth();
        gap = topLeftCircle.getWidth();// - topLeftDot.getWidth();
        super.onWindowFocusChanged(hasFocus);
    }


}
