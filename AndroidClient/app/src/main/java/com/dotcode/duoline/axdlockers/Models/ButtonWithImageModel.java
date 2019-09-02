package com.dotcode.duoline.axdlockers.Models;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;
import android.widget.TextView;

import com.dotcode.duoline.axdlockers.R;

public class ButtonWithImageModel {
    private ConstraintLayout button;
    private ImageView image;
    private TextView text;
    private Context ctx;

    public ButtonWithImageModel(ConstraintLayout button, ImageView image, TextView text, Context ctx){
        this.button = button;
        this.image = image;
        this.text = text;
        this.ctx = ctx;
    }

    public ImageView getImage() {
        return image;
    }

    public TextView getText() {
        return text;
    }

    public ConstraintLayout getButton() {
        return button;
    }



    public void setButtonEnabled(boolean isEnabled) {
        button.setEnabled(isEnabled);
        if(isEnabled) {
            text.setTextColor(ContextCompat.getColor(ctx, R.color.white));
            DrawableCompat.setTint(image.getDrawable(), ContextCompat.getColor(ctx, R.color.white));
        } else {
            text.setTextColor(ContextCompat.getColor(ctx, R.color.white_overlay));
            DrawableCompat.setTint(image.getDrawable(), ContextCompat.getColor(ctx, R.color.white_overlay));
        }
    }
}
