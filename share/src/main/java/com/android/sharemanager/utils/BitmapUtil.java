package com.android.sharemanager.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;

/**
 * 项目名称：a-okwei
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2015/12/21 18:03
 * 修改人：Administrator
 * 修改时间：2015/12/21 18:03
 * 修改备注：
 */
public class BitmapUtil {
    public static Bitmap getBitmapFromUri(Context activity, Uri uri) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
