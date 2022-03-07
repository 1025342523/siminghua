package com.yscoco.siminghua.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.ys.module.log.LogUtils;
import com.ys.module.utils.DisplayUtils;
import com.yscoco.siminghua.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ResourceAsColor")
public class NewLineView extends View {

    private Paint paint;
    private Paint paintCircle;
    private float screenW = 0, screenH = 0;
    private boolean isRefrsh = true;
    List<Integer> timeValue = new ArrayList<>();
    Context context;
    float px_5 = 5;
    int maxPoint = 800;/*最多点*/
    float unitW = 10;
    float unitH = 100;
    private int paintColor = 0;

    public NewLineView(Context context) {
        this(context, null);
    }

    public NewLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);//空心矩形框
        paint.setStrokeJoin(Paint.Join.ROUND);
//        paint.setPathEffect(new CornerPathEffect(90));
        paint.setPathEffect(new CornerPathEffect(0));
        px_5 = DisplayUtils.dp2px(getResources().getDimension(R.dimen.DIMEN_5PX), (Activity) context) / 4;
        paint.setStrokeWidth(px_5 * 2);

        paintCircle = new Paint();
        paintCircle.setColor(Color.WHITE);
        paintCircle.setStyle(Paint.Style.FILL);//空心矩形框
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (screenW == 0) {
            screenW = getWidth();
            screenH = getHeight();
            unitW = (float) ((screenW * 0.95) / maxPoint);
            unitH = screenH / 10;
        }
        isRefrsh = false;
        drawChart(canvas);/*画新线*/
        isRefrsh = true;
    }

    //画值
    public void drawChart(Canvas canvas) {
        //new Object
        Path path = new Path();
        float startx = 20 - unitW;
        float startH = unitH * 11 - unitH / 2;
        int startPoint = (maxPoint >= timeValue.size()) ? 0 : timeValue.size() - maxPoint;
        for (int i = startPoint; i < timeValue.size(); i++) {
            int value = timeValue.get(i);
            if (value > 10) {
                value = 10;
            } else {
                if (value < 1) {
                    value = 1;
                }
            }
            startx += unitW;
            startH = unitH * (11 - value) - unitH / 2;
            if (i == startPoint) {
                path.moveTo(startx, startH);
            } else {
                path.lineTo(startx, startH);
            }
            if (i == (timeValue.size() - 1)) {
                if (paintColor != 0) {
                    paintCircle.setColor(paintColor);
                } else {
                    paintCircle.setColor(Color.WHITE);
                }
                canvas.drawCircle(startx, startH, (float) (px_5 * 3), paintCircle);
                paintCircle.setColor(Color.argb(60, 255, 255, 255));
                canvas.drawCircle(startx, startH, px_5 * 3, paintCircle);
            }
            LogUtils.e("startx:" + startx + "startH" + startH);
        }
        if (paintColor != 0) {
            paint.setColor(paintColor);
        } else {
            paint.setColor(Color.WHITE);
        }
        canvas.drawPath(path, paint);
    }

    /*设置值*/
    public void setValue(int efforts) {
        timeValue.add(efforts);
        invalidate();
    }

    /*清空历史数据*/
    public synchronized void clear() {
        timeValue.clear();
        invalidate();
    }

    public void setColor(int color) {
        paintColor = color;
    }

    public void refrshView() {
        if (isRefrsh) {
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
