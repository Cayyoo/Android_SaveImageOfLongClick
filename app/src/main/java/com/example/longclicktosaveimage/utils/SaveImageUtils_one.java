package com.example.longclicktosaveimage.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.longclicktosaveimage.R;

import java.io.File;
import java.io.FileOutputStream;

/**
 *  异步保存
 */
public class SaveImageUtils_one extends AsyncTask<Bitmap, Void, String> {
    Activity mActivity;
    ImageView mImageView;

    public SaveImageUtils_one(Activity activity, ImageView imageView) {
        this.mImageView = imageView;
        this.mActivity = activity;
    }

    @Override
    protected String doInBackground(Bitmap... params) {
        String result = mActivity.getResources().getString(R.string.save_picture_failed);

        try {
            //获取文件保存目录
            String sdcard = Environment.getExternalStorageDirectory().toString();
            File file = new File(sdcard + "/我的图片");

            if (!file.exists()) {
                file.mkdirs();
            }

            //给文件命名
            File imageFile = new File(file.getAbsolutePath(), System.currentTimeMillis()+"命名" + ".jpg");
            FileOutputStream outStream ;
            outStream = new FileOutputStream(imageFile);

            Bitmap image = params[0];
            image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

            outStream.flush();
            outStream.close();

            //result = mActivity.getResources().getString(R.string.save_picture_success);
            result = mActivity.getResources().getString(R.string.save_picture_success, file.getAbsolutePath());

            //发送广播更新图库，进入相册时可预览保存的图片
            UpdateGalleryUtils.updateAlbums(mActivity,imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(mActivity, result, Toast.LENGTH_SHORT).show();

        mImageView.setDrawingCacheEnabled(false);
    }
}
