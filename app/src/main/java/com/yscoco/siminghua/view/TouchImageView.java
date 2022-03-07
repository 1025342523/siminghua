package com.yscoco.siminghua.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ys.module.utils.AppUtils;

/**
 * Created by ZhangZeZhi on 2018\10\29 0029.
 */

@SuppressLint("AppCompatCustomView")
public class TouchImageView extends ImageView {

    /**
     * 记录X轴最后位置
     */
    int lastX = 0;

    /**
     * 记录Y轴最后位置
     */
    int lastY = 0;

    /**
     * 记录Action_Down - X轴位置
     */
    int downX = 0;

    /**
     * 记录Action_Down - Y轴位置
     */
    int downY = 0;

    /**
     * 记录Action_Down 时间
     */
    long lastDownInMills;

    final int DEFAULT_LIMITLEFT = 0;
    final int DEFAULT_LIMITTOP = 0;

    int limitLeft;
    int limitTop;
    int limitRight;
    int limitBottom;

    int screenWidth;
    int screenHeight;

    private Context mContext;

    private OnClickListener l;

    public TouchImageView(Context context) {
        this(context, null);
    }

    public TouchImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = 950;
        mContext = context;
        setLimitAuto();
    }

    /**
     * 自适应边界
     */
    private void setLimitAuto() {
        setLimitAuto(null);
    }

    /**
     * 根据parentView自适应边界
     *
     * @param parentView
     */
    public void setLimitAuto(ViewGroup parentView) {
        if (parentView == null) {
            this.limitLeft = DEFAULT_LIMITLEFT;
            this.limitTop = DEFAULT_LIMITTOP;
            this.limitRight = screenWidth;
            this.limitBottom = screenHeight;
        } else {
            this.limitLeft = parentView.getLeft();
            this.limitTop = parentView.getTop();
            this.limitRight = parentView.getRight();
            this.limitBottom = parentView.getBottom();
        }
    }

    /**
     * 设定自定义边界
     *
     * @param limitLeft
     * @param limitTop
     * @param limitRight
     * @param limitBottom
     */
    public void setLimitParams(int limitLeft, int limitTop, int limitRight, int limitBottom) {
        this.limitLeft = limitLeft;
        this.limitTop = limitTop;
        this.limitRight = limitRight;
        this.limitBottom = limitBottom;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        this.l = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // System.out.println(event.getRawX());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastDownInMills = System.currentTimeMillis();
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                downX = (int) event.getRawX();
                downY = (int) event.getRawY();
                if (mOnMoveYListener != null) {
                    mOnMoveYListener.onDown();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                int left = getLeft() + dx;
                int top = getTop() + dy;
                int right = getRight() + dx;
                int bottom = getBottom() + dy;

                if (left < limitLeft) {
                    left = limitLeft;
                    right = left + getWidth();
                }

                if (top < limitTop) {
                    top = limitTop;
                    bottom = top + getHeight();
                }

                if (right > limitRight) {
                    right = limitRight;
                    left = limitRight - getWidth();
                }

                if (bottom > limitBottom) {
                    bottom = limitBottom;
                    top = limitBottom - getHeight();
                }

                if (mOnMoveYListener != null) {
                    int sreenHeight = AppUtils.getSreenHeight(mContext);
                    Log.e("screenHeight_24", (int) ((sreenHeight - event.getRawY())) / 24 + "");
                    Log.e("screenHeight", (sreenHeight - event.getRawY()) + "");
                    mOnMoveYListener.onMoveY((int) ((sreenHeight - event.getRawY()) - 86) / 100);
                }

                layout(left, top, right, bottom);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                dx = (int) event.getRawX() - downX;
                dy = (int) event.getRawY() - downY;
                if (dx + dy == 0 && System.currentTimeMillis() - lastDownInMills < 500) {
                    //TODO x + y移动距离小于2px 且 触碰时间小于500ms 触发点击事件
                    if (l != null) {
                        l.onClick(this);
                    }
                }
                if (onLastMovePosition != null) {
                    onLastMovePosition.onLastMovePosition((int) event.getRawX(), (int) event.getRawY());
                }
                if (mOnMoveYListener != null) {
                    mOnMoveYListener.onUp();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setOnLastMovePosition(OnMovePositionListener onLastMovePosition) {
        this.onLastMovePosition = onLastMovePosition;
    }

    public void setOnMoveYListener(OnMoveYListener l) {
        this.mOnMoveYListener = l;
    }

    private OnMovePositionListener onLastMovePosition;

    private OnMoveYListener mOnMoveYListener;

    public interface OnMovePositionListener {
        void onLastMovePosition(int x, int y);
    }

    public interface OnMoveYListener {

        void onMoveY(int y);

        void onUp();

        void onDown();
    }


}
