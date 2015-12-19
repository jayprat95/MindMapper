package edu.engagement.application.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class SemiCircleDrawable extends Drawable {

    private Paint paintLeft;
    private Paint paintRight;
    private RectF rectF;
    private int colorLeft;
    private int colorRight;
    private Direction angle;

    public enum Direction
    {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    public SemiCircleDrawable() {

        colorLeft = Color.CYAN;
        colorRight = Color.GREEN;
        this.angle = Direction.LEFT;


        paintLeft = new Paint();
        paintLeft.setColor(colorLeft);
        paintLeft.setStyle(Paint.Style.FILL);
        paintLeft.setAlpha(80);


        paintRight = new Paint();
        paintRight.setColor(colorRight);

        paintLeft.setStyle(Paint.Style.FILL_AND_STROKE);
        paintLeft.setStrokeWidth(3);
        rectF = new RectF();
    }

//    public SemiCircleDrawable(int color, Direction angle) {
//        this.colorLeft = color;
//        this.angle = angle;
//        paint = new Paint();
//        paint.setColor(color);
//        paint.setStyle(Paint.Style.FILL);
//        rectF = new RectF();
//    }

    public int getColorLeft() {
        return colorLeft;
    }

    public int getColorRight() {
        return colorRight;
    }

    /**
     * A 32bit color not a color resources.
     * @param color
     */
    public void setColorLeft(int color) {
        this.colorLeft = color;
        paintLeft.setColor(color);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();


        Rect bounds = getBounds();
        canvas.scale(2, 1);

        rectF.set(bounds);

        canvas.drawArc(rectF, 90, 180, true, paintLeft);
        canvas.drawArc(rectF, 270, 180, true, paintRight);
//
//
//        canvas.drawArc(rectF, 90, 180, false, paintLeft);
//        RectF rectF = new RectF(50, 50, 150, 150);
//        canvas.drawOval(rectF, paintLeft);


//        canvas.drawArc(oval, 180, 180, false, paintRight);
        //canvas.drawArc(rectF, 90, 180, true, paintRight);

//        if(angle == Direction.LEFT)
//            canvas.drawArc(rectF, 90, 180, true, paint);
//        else if(angle == Direction.TOP)
//            canvas.drawArc(rectF, -180, 180, true, paint);
//        else if(angle == Direction.RIGHT)
//            canvas.drawArc(rectF, 270, 180, true, paint);
//        else if(angle == Direction.BOTTOM)
//            canvas.drawArc(rectF, 0, 180, true, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        // Has no effect
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // Has no effect
    }

    @Override
    public int getOpacity() {
        // Not Implemented
        return 0;
    }

}