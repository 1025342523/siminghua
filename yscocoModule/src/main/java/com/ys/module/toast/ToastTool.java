package com.ys.module.toast;


import android.content.Context;
import android.widget.Toast;

/**
 * Toast
 *
 * @author karl.wei
 * @data 2016-05-12
 * @filename ToolToast.java
 */
public class ToastTool {
    public static Toast mToast;

    /**
     * @param msg 提示语
     */
    public static void showNormalShort(Context context, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }

    /**
     * @param msg 提示语
     */
    public static void showNormalShort(Context context, int msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }

    /**
     * @param msg 提示语
     */
    public static void showNormalLong(Context context, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_LONG);
        }
        mToast.setText(msg);
        mToast.show();
    }

    /**
     * @param msg 提示语
     */
    public static void showNormalLong(Context context, int msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_LONG);
        }
        mToast.setText(msg);
        mToast.show();
    }
}
