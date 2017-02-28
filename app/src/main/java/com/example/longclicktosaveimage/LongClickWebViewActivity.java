package com.example.longclicktosaveimage;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.longclicktosaveimage.utils.UpdateGalleryUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * 保存WebView中的图片
 */
public class LongClickWebViewActivity extends ActionBarActivity {
    private String imgurl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_click_webview);

        WebView webView = (WebView) findViewById(R.id.w);
        WebSettings webSettings = webView.getSettings();

        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        //加载需要显示的网页
        webView.loadUrl("http://o.slwj365.com/images_login/slogin_08.gif");
        //设置Web视图
        webView.setWebViewClient(new webViewClient());
        // 为所有列表项注册上下文菜单
        LongClickWebViewActivity.this.registerForContextMenu(webView);
    }

    //Web视图
    private class webViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);

            return true;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == "保存到手机") {
                    new SaveImage().execute(); // Android 4.0以后要使用线程来访问网络
                } else {
                    return false;
                }

                return true;
            }
        };

        if (v instanceof WebView) {
            WebView.HitTestResult result = ((WebView) v).getHitTestResult();

            if (result != null) {
                int type = result.getType();

                if (type == WebView.HitTestResult.IMAGE_TYPE || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    imgurl = result.getExtra();
                    menu.setHeaderTitle("提示");
                    menu.add(0, v.getId(), 1, "保存到手机").setOnMenuItemClickListener(handler);
                }
            }
        }
    }

    /***
     * 功能：用线程异步保存
     */
    private class SaveImage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result ;

            try {
                String sdcard = Environment.getExternalStorageDirectory().toString();
                File file = new File(sdcard + "/Download");

                if (!file.exists()) {
                    file.mkdirs();
                }

                int idx = imgurl.lastIndexOf(".");
                String ext = imgurl.substring(idx);

                file = new File(sdcard + "/Download/" + new Date().getTime() + ext);
                InputStream inputStream = null;
                URL url = new URL(imgurl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(20000);

                if (conn.getResponseCode() == 200) {
                    inputStream = conn.getInputStream();
                }

                byte[] buffer = new byte[4096];
                int len = 0;

                FileOutputStream outStream = new FileOutputStream(file);

                while ((len = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }

                outStream.close();
                result = "图片已保存至：" + file.getAbsolutePath();

                //发送广播更新图库，进入相册时可预览保存的图片
                UpdateGalleryUtils.updateAlbums(LongClickWebViewActivity.this,file);
            } catch (Exception e) {
                result = "保存失败！" + e.getLocalizedMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            new AlertDialog.Builder(LongClickWebViewActivity.this)
                    .setTitle("提示")
                    .setMessage(result)
                    .setPositiveButton("确定",null)
                    .show();
        }
    }

}
