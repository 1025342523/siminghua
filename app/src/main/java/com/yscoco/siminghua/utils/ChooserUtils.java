package com.yscoco.siminghua.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.ys.module.select.OnPickerOptionsClickListener;
import com.ys.module.select.SingleOptionsPicker;

import java.util.List;

/**
 * 作者：karl.wei
 * 创建日期： 2017/7/31 0031 19:23
 * 邮箱：karl.wei@yscoco.com
 * QQ:2736764338
 * 类介绍：下拉选择
 */
public class ChooserUtils {
    /**
     * 修改性别
     *
     * @param context
     * @param unit
     * @param what
     * @param handler
     * @param list
     */
    public static void showSingleDialog(Activity context, final String unit, final int what, final Handler handler, final String selectValue, final List<String> list) {
        new SingleOptionsPicker(context, selectValue == null ? list.get(0) : selectValue, list, new OnPickerOptionsClickListener() {
            @Override
            public void onSelect(int position, View view) {
                Message msg = handler.obtainMessage();
                msg.what = what;
                msg.obj = list.get(position);
                handler.sendMessage(msg);
            }
        }, false).show();
    }
}
