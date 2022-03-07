package com.yscoco.siminghua.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.yscoco.siminghua.MyApplication;


/**
 * Created by ZhangZeZhi
 * <p>
 * 资源工具类-加载资源文件
 */

public class ResourcesUtils {
    /**
     * 获取strings.xml资源文件字符串
     *
     * @param id 资源文件id
     * @return 资源文件对应字符串
     */
    public static String getString(int id) {
        return MyApplication.getApplication().getResources().getString(id);
    }

    /**
     * 获取strings.xml资源文件字符串数组
     *
     * @param id 资源文件id
     * @return 资源文件对应字符串数组
     */
    public static String[] getStringArray(int id) {
        return MyApplication.getApplication().getResources().getStringArray(id);
    }

    /**
     * 获取drawable资源文件图片
     *
     * @param id 资源文件id
     * @return 资源文件对应图片
     */
    public static Drawable getDrawable(int id) {
        return MyApplication.getApplication().getResources().getDrawable(id);
    }

    /**
     * 获取colors.xml资源文件颜色
     *
     * @param id 资源文件id
     * @return 资源文件对应颜色值
     */
    public static int getColor(int id) {
        return MyApplication.getApplication().getResources().getColor(id);
    }

    /**
     * 获取颜色的状态选择器
     *
     * @param id 资源文件id
     * @return 资源文件对应颜色状态
     */
    public static ColorStateList getColorStateList(int id) {
        return MyApplication.getApplication().getResources().getColorStateList(id);
    }

    /**
     * 获取dimens资源文件中具体像素值
     *
     * @param id 资源文件id
     * @return 资源文件对应像素值
     */
    public static int getDimen(int id) {
        return MyApplication.getApplication().getResources().getDimensionPixelSize(id);// 返回具体像素值
    }

    /**
     * 加载布局文件
     *
     * @param id 布局文件id
     * @return 布局view
     */
    public static View inflate(int id) {
        return View.inflate(MyApplication.getApplication(), id, null);
    }
}
