package com.peng.camerademo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int REQ_1 = 1;
    private final int REQ_2 = 2;
    private String picturePath;

    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);

//        picturePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        picturePath = Environment.getExternalStorageDirectory().getPath();
        picturePath = picturePath + "/" + "temp.png";
    }

    //    跳转到新的activity，在新activity中开启摄像头进行拍摄
    public void newCamera(View view) {
//        -------------------------------------------隐式Intent启动activity---------------------------------------
//        使用隐式Intent的方式启动新的activity
//        Intent intent = new Intent("newCamera");
//        startActivity(intent);
//        --------------------------------------------------结束-----------------------------------------------
//        ------------------------------------------显式Intent启动activity-------------------------------------
        Intent intent = new Intent(this, NewCameraActivity.class);
        startActivity(intent);
//        --------------------------------------------------结束-----------------------------------------------
    }


    //    打开系统相机
    public void startCamera(View view) {
//        ---------------------------------------调用系统相机，并返回缩略图----------------------------------
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(intent, REQ_1);
//        -------------------------------------------------结束----------------------------------------------

//        -----------------------------调用系统相机，并通过读取文件获取原图--------------------------
        /*
        * 此方法为了获取到拍摄的原图，修改了拍摄的图片保存的路径，然后从该路径读取图片
        * 需要添加操作sd卡的权限
        * */
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Intent intent = new Intent("android.media.action.STILL_IMAGE_CAMERA");    //使用这种action去打开相机时，拍摄完照片后不会返回自己的程序，仍然停留在相机界面，并且照片会保存在默认的位置
        //使用Uri定位到已经写好的路径
        Uri uri = Uri.fromFile(new File(picturePath));
        //使用MediaStore.EXTRA_OUTPUT参数可以对拍照后的图片保存的位置进行修改，将保存的位置指向了我们定义的这个Uri，也就是我们定义的sd卡的temp.png文件。
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQ_2);
//        ---------------------------------------------结束-------------------------------------------
    }

    //    测试内部存储空间、sd卡的目录和状态
    public void testTheCard(View view) {
        ArrayList<String> array = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        array.add("getExternalStorageState: " + Environment.getExternalStorageState());
        array.add("getExternalStorageDirectory: " + Environment.getExternalStorageDirectory().getAbsolutePath());
        array.add("getFilesDir: " + getFilesDir().getAbsolutePath());
        array.add("getDataDirectory: " + Environment.getDataDirectory().getAbsolutePath());
        array.add("getRootDirectory: " + Environment.getRootDirectory().getAbsolutePath());
        array.add("getDownloadCacheDirectory: " + Environment.getDownloadCacheDirectory().getAbsolutePath());
        for (String str : array) {
            Log.i("i:", str);
            sb.append(str + "\n");
        }
        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText(sb.toString());
    }

    public void reload(View v) {
        InputStream fis = null;
        try {
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/" + "temp.png"));
            byte[] by = new byte[2048];
            int len = 0;
            StringBuffer sb = new StringBuffer();
            try {
                while ((len = fis.read(by)) != -1) {
                    if (len <= 2048) {
                        break;
                    }
                }

                Bitmap bitmap = BitmapFactory.decodeByteArray(by, 0, by.length);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Bitmap bitmap = BitmapFactory.decodeStream(fis);
//            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                Log.i("i", "reload: 结束");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_1) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                imageView.setImageBitmap(bitmap);
            } else if (requestCode == REQ_2) {
                FileInputStream fis = null;
                Log.i("~~~~~~~~~~~~~~~~~~", "返回成功 ");
                Log.i("path", picturePath);
                try {
                    fis = new FileInputStream(picturePath);
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fis.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }
}
