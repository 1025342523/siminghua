package com.ys.module.utils;/**
 * Created by Administrator on 2017/9/19 0019.
 */

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.ys.module.R;

/**
 * 作者：karl.wei
 * 创建日期： 2017/9/19 0019 16:03
 * 邮箱：karl.wei@yscoco.com
 * 类介绍：图片加载
 */
public class ImageUtils {
    public static void loadHead(Context context,String url,ImageView iv_head){
//        RequestOptions options = RequestOptions.bitmapTransform(new CircleCrop())
//                .placeholder(R.mipmap.ico_head_def)
//                .diskCacheStrategy(DiskCacheStrategy.ALL);
//        Glide.with(context).load(url+"").apply(options).into(iv_head);
        loadHead(context,url,iv_head, R.mipmap.ico_head_def);
    }
    public static void loadHead(Context context,String url,ImageView iv_head,int defHead){
        RequestOptions options = RequestOptions.bitmapTransform(new CircleCrop())
                .placeholder(defHead)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context).load(url+"").apply(options).into(iv_head);
    }
    public static void showImage(Context context, ImageView iv, String url,int defId){
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(defId)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(iv);
    }

}
