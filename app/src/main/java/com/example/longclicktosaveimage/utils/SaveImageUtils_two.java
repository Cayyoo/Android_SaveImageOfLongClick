package com.example.longclicktosaveimage.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 使用RxJava实现异步保存图片
 */
public class SaveImageUtils_two {
    private Context context;

    public SaveImageUtils_two(Context context) {
        this.context = context;
    }

    /**
     * 步骤1
     */
    public void saveImageView(Bitmap drawingCache) {
        Observable.create(new SaveObservable(drawingCache))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SaveSubscriber());
    }

    /**
     * 步骤2
     *
     * 某些机型直接获取会为null,在这里处理一下防止国内某些机型返回null
     */
    public Bitmap getViewBitmap(View view) {
        if (view == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    /**
     * 步骤3
     */
    public class SaveObservable implements Observable.OnSubscribe<String> {
        private Bitmap drawingCache = null;

        public SaveObservable(Bitmap drawingCache) {
            this.drawingCache = drawingCache;
        }

        @Override
        public void call(Subscriber<? super String> subscriber) {
            if (drawingCache == null) {
                subscriber.onError(new NullPointerException("imageview的bitmap获取为null,请确认imageview显示图片了"));
            } else {
                try {
                    File imageFile = new File(Environment.getExternalStorageDirectory(), new Date().getTime()+"_save.jpg");
                    FileOutputStream outStream;
                    outStream = new FileOutputStream(imageFile);

                    drawingCache.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

                    subscriber.onNext(Environment.getExternalStorageDirectory().getPath());
                    subscriber.onCompleted();

                    outStream.flush();
                    outStream.close();

                    //发送广播更新图库，进入相册时可预览保存的图片
                    UpdateGalleryUtils.updateAlbums(context,imageFile);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }

    }

    /**
     * 步骤4
     */
    public class SaveSubscriber extends Subscriber<String> {

        @Override
        public void onCompleted() {
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Throwable e) {
            Log.i(getClass().getSimpleName(), e.toString());
            Toast.makeText(context, "保存失败——> " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(String s) {
            Toast.makeText(context, "保存路径为：-->  " + s, Toast.LENGTH_SHORT).show();
        }

    }

}
