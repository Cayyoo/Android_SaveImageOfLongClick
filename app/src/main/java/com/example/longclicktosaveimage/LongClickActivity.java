package com.example.longclicktosaveimage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.longclicktosaveimage.utils.SaveImageUtils_one;
import com.example.longclicktosaveimage.utils.SaveImageUtils_two;

/**
 * Activity中点击按钮、长按图片实现保存
 */
public class LongClickActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_click);

        imageView = (ImageView) findViewById(R.id.imageview);
        btn_save = (Button) this.findViewById(R.id.btn_save);


        //方式一，点击Button保存
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveImageUtils_two siv=new SaveImageUtils_two(LongClickActivity.this);
                siv.saveImageView(siv.getViewBitmap(imageView));
            }
        });


        //方式二，长按图片保存
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LongClickActivity.this);

                builder.setItems(new String[]{getResources().getString(R.string.save_picture)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageView.setDrawingCacheEnabled(true);
                        Bitmap imageBitmap = imageView.getDrawingCache();

                        if (imageBitmap != null) {
                            new SaveImageUtils_one(LongClickActivity.this, imageView).execute(imageBitmap);
                        }
                    }
                });

                builder.show();
                return true;
            }
        });

    }

}