package com.dotcode.duoline.axdlockers.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.dotcode.duoline.axdlockers.R;


public class TextRectangle extends View {
    private Rect rectangle;
    private Paint paint;

    public TextRectangle(Context context, Rect rect) {
        super(context);
        rectangle = rect;

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.colorBleuOverlay));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(rectangle, paint);
    }
}
