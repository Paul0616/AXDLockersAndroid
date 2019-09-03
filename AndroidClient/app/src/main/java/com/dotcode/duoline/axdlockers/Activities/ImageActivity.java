package com.dotcode.duoline.axdlockers.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MotionEventCompat;
import androidx.exifinterface.media.ExifInterface;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dotcode.duoline.axdlockers.R;

import java.io.IOException;

public class ImageActivity extends AppCompatActivity {

    private ImageView iv, topLeftDot, topLeftCircle,topRightCircle, bottomLeftCircle, bottomRightCircle, croppingRectImageView;
    private TextView wTextView, hTextView;
    private Bitmap rotatedBitmap;
    int containerWidth, containerHeight;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
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





        croppingRectImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) croppingRectImageView.getLayoutParams();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:


//                         Find the index of the active pointer and fetch its position

                        int x_cord = (int) event.getRawX();
                        int y_cord = (int) event.getRawY();

                        Matrix matrix = croppingRectImageView.getImageMatrix();
                        matrix.postTranslate(x_cord, y_cord);
                        croppingRectImageView.setImageMatrix(matrix);
                        croppingRectImageView.invalidate();


//                        if (x_cord > containerWidth-croppingRectImageView.getWidth()+25) {
//                            x_cord = containerWidth-croppingRectImageView.getWidth()+25;
//                        }
//                        if (y_cord > containerHeight-croppingRectImageView.getHeight()+75) {
//                            y_cord = containerHeight-croppingRectImageView.getHeight()+75;
//                        }
//
//                        layoutParams.leftMargin = x_cord-25;
//                        layoutParams.topMargin = y_cord-75;
//
//                        croppingRectImageView.setLayoutParams(layoutParams);
//                        wTextView.setText("W:"+rotatedBitmap.getWidth()+" px/"+croppingRectImageView.getX());
//                        hTextView.setText("H:"+rotatedBitmap.getHeight()+" px/"+croppingRectImageView.getY());
//                        break;
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
                ConstraintLayout.LayoutParams croppingRectLayoutParams = (ConstraintLayout.LayoutParams) croppingRectImageView.getLayoutParams();
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

                        topRightCirclelayoutParams.topMargin = y_cord-75;
                        bottomLeftCirclelayoutParams.leftMargin = x_cord-25;
                        croppingRectLayoutParams.leftMargin = x_cord-25+topLeftCircle.getWidth()/2;
                        croppingRectLayoutParams.topMargin = y_cord-75+topLeftCircle.getHeight()/2;

                        wTextView.setText("W:"+rotatedBitmap.getWidth()+" px/"+topLeftCircle.getX());
                        hTextView.setText("H:"+rotatedBitmap.getHeight()+" px/"+topLeftCircle.getY());
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
                ConstraintLayout.LayoutParams croppingRectLayoutParams = (ConstraintLayout.LayoutParams) croppingRectImageView.getLayoutParams();
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
                        bottomRightCircleLayoutParams.leftMargin = topRightCircleLayoutParams.leftMargin;

                        croppingRectLayoutParams.topMargin = topRightCircleLayoutParams.topMargin+topRightCircle.getHeight()/2;
                        croppingRectLayoutParams.width = (int) (topRightCircle.getX() - topLeftCircle.getX());
                        croppingRectLayoutParams.height = (int) (bottomRightCircle.getY() - topRightCircle.getY());
                        croppingRectImageView.requestLayout();

//                        wTextView.setText("W:"+rotatedBitmap.getWidth()+" px/"+topLeftCircle.getX());
//                        hTextView.setText("H:"+rotatedBitmap.getHeight()+" px/"+topLeftCircle.getY());
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
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
        super.onWindowFocusChanged(hasFocus);
    }


}
