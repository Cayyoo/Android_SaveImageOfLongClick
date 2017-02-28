package com.example.longclicktosaveimage.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * 发送广播更新图库，进入相册时可预览保存的图片
 */
public class UpdateGalleryUtils {

    public static void updateAlbums(Context context,File imageFile){//此处传递需要更新的图片地址imageFile
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(imageFile);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

}
