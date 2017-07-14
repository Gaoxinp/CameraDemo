package com.peng.camerademo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileInputStream;

public class CaptureActivity extends AppCompatActivity {
    private ImageView picImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        picImg = (ImageView) findViewById(R.id.cpatureImage);
        String path = getIntent().getStringExtra("picPath");
        Log.i("~~~~~~~~~~~~~~~~~~~", "onCreate: " + path);
        try {
//            ------------------------------拍照之后是横屏的，因此需要旋转90度-----------------------------
            FileInputStream fis = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            Matrix matrix = new Matrix();
            matrix.setRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            picImg.setImageBitmap(bitmap);
//            -------------------------------------------结束-------------------------------------------------

//            ------------------------------拍照之后是横屏的，不进行设置直接显示------------------------------
//            Bitmap bitmap = BitmapFactory.decodeFile(path);
//            picImg.setImageBitmap(bitmap);

//            ------------------------------------------结束---------------------------------------------------
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
