package com.example.datingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

public class BubbleTextView extends androidx.appcompat.widget.AppCompatTextView {

    private Paint paint;
    private RectF rect;

    public BubbleTextView(Context context) {
        super(context);
        init();
    }

    public BubbleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BubbleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.black));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        rect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the rounded rectangle
        rect.set(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(rect, getHeight() / 2, getHeight() / 2, paint);

        // Draw the text
        super.onDraw(canvas);
    }
}

