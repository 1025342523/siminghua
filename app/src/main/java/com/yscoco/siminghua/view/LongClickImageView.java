package com.yscoco.siminghua.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by ZhangZeZhi on 2018\11\7 0007.
 */

@SuppressLint("AppCompatCustomView")
public class LongClickImageView extends ImageView {

    public LongClickImageView(Context context) {
        super(context);
    }

    public LongClickImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LongClickImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                if (mListener != null) {
                    mListener.onDown();
                }

                break;
            case MotionEvent.ACTION_UP:

                if (mListener != null) {
                    mListener.onUp();
                }

                break;
        }

        return true;
        
    }

    private OnImageViewClickedListener mListener;

    public void setOnImageViewClickedListener(OnImageViewClickedListener listener) {
        mListener = listener;
    }

    public interface OnImageViewClickedListener {

        void onDown();

        void onUp();

    }

}
