package com.dotcode.duoline.axdlockers.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.dotcode.duoline.axdlockers.R;

public class Dot extends ImageView {
    float dotWidth = 40F;
    float dotHeight = 40F;
    //ImageView middleDot;


    public Dot(Context context, ConstraintLayout constraintLayout) {
        super(context);
        constraintLayout.addView(this);
        ConstraintLayout.LayoutParams dotLayoutParams = (ConstraintLayout.LayoutParams) this.getLayoutParams();

        dotLayoutParams.width = (int) convertDpToPixel(dotWidth, context);
        dotLayoutParams.height =(int) convertDpToPixel(dotHeight, context);
        this.requestLayout();
        setBackgroundResource(R.drawable.round_corner_circle);
//        middleDot = new ImageView(context);
//        middleDot.getLayoutParams().width = (int) (dotWidth/4);
//        middleDot.getLayoutParams().height = (int) (dotHeight/4);
//        middleDot.requestLayout();
//

    }

    public int getMidX(){
        return (int) (this.getLayoutParams().width/2);
    }

    public void setXPos(int x){
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) this.getLayoutParams();
        lp.leftMargin = x - getMidX();
        setLayoutParams(lp);
    }

    public int getMidY(){
        return (int) (this.getLayoutParams().height/2);
    }

    public void setYPos(int y){
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) this.getLayoutParams();
        lp.topMargin = y - getMidY();
        setLayoutParams(lp);
    }

    public float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}
