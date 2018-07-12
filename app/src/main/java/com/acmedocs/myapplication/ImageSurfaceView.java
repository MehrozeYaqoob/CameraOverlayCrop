package com.acmedocs.myapplication;

/**
 * Created by mehrozeyaqoob on 7/12/18.
 */

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;

import static android.content.Context.WINDOW_SERVICE;

public class ImageSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Camera mCamera;
    private SurfaceHolder surfaceHolder;
    Context context;
    boolean isPreviewRunning = false;

    public ImageSurfaceView(Context context, Camera camera) {
        super(context);
        this.mCamera = camera;
        this.context = context;
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            this.mCamera.setPreviewDisplay(holder);
            this.mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (isPreviewRunning) {
            mCamera.stopPreview();
        }

        Display display = ((WindowManager)context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0) {
            mCamera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_90) {
        }

        if(display.getRotation() == Surface.ROTATION_180) {
        }

        if(display.getRotation() == Surface.ROTATION_270) {

            mCamera.setDisplayOrientation(180);
        }

        previewCamera();
    }


    public void previewCamera() {
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            isPreviewRunning = true;
        } catch(Exception e) {
            Log.d("hsfhj", "Cannot start preview", e);
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.mCamera.stopPreview();
        this.mCamera.release();
    }
}
