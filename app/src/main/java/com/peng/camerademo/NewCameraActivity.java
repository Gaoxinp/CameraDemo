package com.peng.camerademo;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class NewCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        //        data里面保存了拍摄后的照片数据，是原图数据
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
//            File tempFile = new File("/sdcard/temp.png");
            File tempFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "temp.png");

            try {
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(data);
                fos.close();

                Intent intent = new Intent(NewCameraActivity.this, CaptureActivity.class);
                intent.putExtra("picPath", tempFile.getAbsolutePath());
                startActivity(intent);
//                NewCameraActivity.this.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_camera);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(null);
            }
        });
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);

    }

    /**
     * 摄像操作
     *
     * @param view
     */
    public void captureOK(View view) {
//        首先要对拍摄参数进行调整
        Camera.Parameters parameters = mCamera.getParameters();
//        设置拍照格式
        parameters.setPictureFormat(ImageFormat.JPEG);
//        设置预览的大小，可以随便设置
        parameters.setPictureSize(800, 400);
//        设置对焦功能，通常自动对焦就可以了，前提是相机支持自动对焦
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

//        需要对焦完成后再进行拍照
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            //            success标志是否已经对焦完成
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
//                     第三个参数是回调，当拍摄完成后将要执行
                    mCamera.takePicture(null, null, pictureCallback);
                }
            }
        });
    }

    /**
     * 获取Camera对象
     *
     * @return
     */
    private Camera getCamera() {
        Camera camera;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            camera = null;
            e.printStackTrace();
        }
        return camera;
    }

    /**
     * 开始预览相机内容,将Camera和SurfaceView进行绑定，因此必须传入两个对象
     */
    private void setStartPreview(Camera camera, SurfaceHolder holder) {
        try {
//            将Camera和SurfaceView进行绑定
            camera.setPreviewDisplay(holder);
//            将预览图转换成竖屏的，系统默认为横屏
            camera.setDisplayOrientation(90);
//            开始预览图像，注意：系统默认的图像是横屏的，因此需要改变成竖屏的,所以就有了上面的camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放相机占用的资源，Camera很像流，用完要释放，同时设备的Camera是共享的，不释放会出错误
     * 还要释放SurfaceHolder
     */
    private void releaseCamera() {
        if (mCamera != null) {
//            现将Camera与SurfaceView取消掉关联
            mCamera.setPreviewCallback(null);
//            停止预览相机内容，取消取景操作
            mCamera.stopPreview();
//            释放掉相机资源
            mCamera.release();
//            将相机置空
            mCamera = null;
        }
    }

    /**
     * 当activity被激活时，创建Camera对象
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera();
            if (mHolder != null) {
                setStartPreview(mCamera, mHolder);
            }
        }
    }

    /**
     * 当activity被暂停时（有时候是销毁activity），将Camera释放掉
     */
    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
        Toast.makeText(NewCameraActivity.this, "onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setStartPreview(mCamera, mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        首先停止预览
        mCamera.stopPreview();
//        然后开始预览
        setStartPreview(mCamera, mHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }
}
