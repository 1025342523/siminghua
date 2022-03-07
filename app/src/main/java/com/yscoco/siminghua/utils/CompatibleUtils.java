package com.yscoco.siminghua.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;

import java.io.File;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by ZhangZeZhi on 2018\11\27 0027.
 */

public class CompatibleUtils {

    public static void updateMedia(final Context context, String path) {

        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {//当大于等于Android 4.4时
            MediaScannerConnection.scanFile(context, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(uri);
                    context.sendBroadcast(mediaScanIntent);
                }
            });
        } else {//Andrtoid4.4以下版本
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile((new File(path).getParentFile()))));
        }
    }

}
