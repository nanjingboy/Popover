package me.tom.popover;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class ArrowView extends View {

    public enum ArrowOrientation {
        DOWN,
        UP
    }

    private Paint mPaint;
    private Path mPath;
    private ArrowOrientation mOrientation;

    public ArrowView(Context context) {
        this(context, null);
    }

    public ArrowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);
        mPath = new Path();
    }

    public void reload(int bgColor, ArrowOrientation orientation) {
        mPaint.setColor(bgColor);
        mOrientation = orientation;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        mPath.reset();
        if (mOrientation == ArrowOrientation.DOWN) {
            mPath.moveTo(0, 0);
            mPath.lineTo(width, 0);
            mPath.lineTo(width / 2, height);
            mPath.lineTo(0, 0);
        } else {
            mPath.moveTo(width / 2, 0);
            mPath.lineTo(0, height);
            mPath.lineTo(width, height);
            mPath.lineTo(width / 2, 0);
        }
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }
}
