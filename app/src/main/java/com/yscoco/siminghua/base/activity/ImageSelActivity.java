package com.yscoco.siminghua.base.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import com.ys.module.log.LogUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2016/1/28 0028.
 */
public abstract class ImageSelActivity extends BaseActivity {

    /**
     * 不需要裁剪
     */
    public static final int PHOTO_REQUEST_TAKEPHOTO = 1;

    /***
     * 需要裁剪
     */
    public static final int PHOTO_REQUEST_TAKEPHOTO_CLIP = 2;

    /**
     * 裁剪结果
     */
    public static final int PHOTO_REQUEST_CUT = 3;

    /**
     * 裁剪临时图片
     */
    public static final String TEMPLE_FILE = "crop.png";

    private String mTempleFile;

    /**
     * @param isClip 是否裁剪
     */
    protected void showSelectImage(final boolean isClip) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                if (resultCode == RESULT_OK) {
                    takePhone(data, false);
                }
                break;

            case PHOTO_REQUEST_TAKEPHOTO_CLIP:
                if (resultCode == RESULT_OK) {
                    takePhone(data, true);
                }
                break;
            case PHOTO_REQUEST_CUT:
                if (data == null) {
                    return;
                }
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bmp = extras.getParcelable("data");
                    String fileName = saveBitmap(bmp);
                    updateImage(fileName);
                }
                break;
        }
    }

    private String saveBitmap(Bitmap bmp) {
        try {
            File fileDir = new File(Environment.getExternalStorageDirectory() + "/image");
            if (!fileDir.exists()) {
                fileDir.mkdir();
            }
            File file = new File(fileDir, TEMPLE_FILE);
            FileOutputStream out;
            out = new FileOutputStream(file);
            if (bmp.compress(Bitmap.CompressFormat.PNG, 70, out)) {
                out.flush();
                out.close();
            }
            LogUtils.e(file.getAbsolutePath() + ", " + Environment.getExternalStorageDirectory() + "/image/" + TEMPLE_FILE);
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param data
     * @param isClip 是否裁剪
     */
    private void takePhone(Intent data, boolean isClip) {
        LogUtils.e(mTempleFile);
        Uri uri;
        if (data == null || data.getData() == null) {
            uri = Uri.fromFile(new File(mTempleFile));
        } else {
            uri = data.getData();
        }

        if (isClip) {
            startPhotoZoom(uri, 300, this);
        } else {
            if (data == null || data.getData() == null) {
                updateImage(mTempleFile);
                return;
            }
//            updateImage(mTempleFile);
            String[] proj = {MediaStore.Images.Media.DATA};
            //好像是android多媒体数据库的封装接口，具体的看Android文档
            Cursor cursor = managedQuery(data.getData(), proj, null, null, null);
            //按我个人理解 这个是获得用户选择的图片的索引值
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            //最后根据索引值获取图片路径
            String path = cursor.getString(column_index);
            LogUtils.e("uri" + path);
            updateImage(path);
        }
    }

    public String getPath(Uri uri) {
        String path = "";
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            path = cursor.getString(columnIndex);
            cursor.close();
        }
        return path;
    }

    /**
     * @param isPicture
     * @param isClip    是否裁剪
     */
    public void selImage(boolean isPicture, boolean isClip) {
        int requestCode;
        File fileDir = new File(Environment.getExternalStorageDirectory() + "/small");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        mTempleFile = new File(fileDir, getPhotoFile()).getAbsolutePath();
        if (isClip) {
            requestCode = PHOTO_REQUEST_TAKEPHOTO_CLIP;
        } else {
            requestCode = PHOTO_REQUEST_TAKEPHOTO;
        }
        if (isPicture) {
            // 调用系统的拍照功能
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 指定调用相机拍照后照片的储存路径
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mTempleFile)));
            startActivityForResult(intent, requestCode);
        } else {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, requestCode);
        }
    }

    public void startPhotoZoom(Uri uri, int size, Activity activity) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 10);
        intent.putExtra("aspectY", 10);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    public void deleteTempfile() {
        File file = getTempleFile();
        if (file.exists()) {
            file.delete();
        }
    }

    public File getTempleFile() {
        return new File(Environment.getExternalStorageDirectory() + "/image", TEMPLE_FILE);
    }

    /**
     * 使用系统当前日期加以调整作为照片的名称
     *
     * @return
     */
    private String getPhotoFile() {
        long time = System.currentTimeMillis();
        return "bags_" + time + ".jpg";
    }

    /**
     * 选择图片后处理
     */
    public void updateImage(String filePath) {
    }

    /**
     * 图片上传成功
     *
     * @param url 图片url
     */
    public void updataSuccess(String url) {
        LogUtils.e("url" + url);
    }

}
