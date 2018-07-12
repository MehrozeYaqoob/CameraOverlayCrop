package com.acmedocs.myapplication;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    private ImageSurfaceView mImageSurfaceView;
    private Camera camera;
    RelativeLayout rl,cameraLayout;
    LinearLayout saveLayout;
    private FrameLayout cameraPreviewLayout;
    private ImageView capturedImageHolder,saveImage;
    int imageL,imageT,imageW,imageH,width,height,dpsize;

    Bitmap resizedBitmap;
    Camera mCamera = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cameraPreviewLayout = (FrameLayout)findViewById(R.id.camera_preview);
        capturedImageHolder = (ImageView)findViewById(R.id.captured_image);

        rl = findViewById(R.id.rr2);

        cameraLayout = findViewById(R.id.cameraLayout);
        saveLayout = findViewById(R.id.saveLayout);

        saveImage = findViewById(R.id.saveImage);

        width= getWindowManager().getDefaultDisplay().getWidth();
        height= getWindowManager().getDefaultDisplay().getHeight();

        camera = checkDeviceCamera();

        mImageSurfaceView = new ImageSurfaceView(MainActivity.this, camera);
        cameraPreviewLayout.addView(mImageSurfaceView);

        dpsize = (int) (getResources().getDimension(R.dimen._150sdp));
        capturedImageHolder.setX((width-dpsize)/2);
        capturedImageHolder.setY((height -dpsize)/2);

        imageL= (int) capturedImageHolder.getX();
        imageT= (int) capturedImageHolder.getY();
        capturedImageHolder.setOnTouchListener(new MoveViewTouchListener(capturedImageHolder));
        Button captureButton = (Button)findViewById(R.id.button);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, pictureCallback);
            }
        });
    }

    private Camera checkDeviceCamera(){

        try {
            mCamera = Camera.open();

        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }
        return mCamera;
    }

    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void resetCamera()
    {
        if(mCamera!=null) {
            mCamera.release();

        }
    }

    PictureCallback pictureCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            final int REQUIRED_SIZE = 512;
            int scale = 1;
            int wd= b.getWidth();
            while (wd >=( REQUIRED_SIZE)) {
                wd= wd/2;
                scale *= 2;
            }
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);

            if(bitmap==null){
                Toast.makeText(MainActivity.this, "Captured image is empty", Toast.LENGTH_LONG).show();
                return;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap= Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
            int bh= bitmap.getHeight();
            int bw= bitmap.getWidth();
            width= rl.getWidth();
            height= rl.getHeight();
            int l = imageL*bw/width;
            int t = imageT*bh/height;
            int w = capturedImageHolder.getWidth()*bw/width;
            int h = capturedImageHolder.getHeight()*bh/height;

            cameraPreviewLayout.setVisibility(View.GONE);
            capturedImageHolder.setVisibility(View.VISIBLE);
            resizedBitmap= Bitmap.createBitmap(bitmap,l,t,w,h);

            if(resizedBitmap!=null) {

                cameraLayout.setVisibility(View.GONE);
                saveLayout.setVisibility(View.VISIBLE);
                saveImage.setImageBitmap(resizedBitmap);
            }
        }
    };

    public class MoveViewTouchListener
            implements View.OnTouchListener
    {
        private GestureDetector mGestureDetector;
        private View mView;


        public MoveViewTouchListener(View view)
        {
            mGestureDetector = new GestureDetector(view.getContext(), mGestureListener);
            mView = view;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            return mGestureDetector.onTouchEvent(event);
        }

        private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener()
        {
            private float mMotionDownX, mMotionDownY;

            @Override
            public boolean onDown(MotionEvent e)
            {
                mMotionDownX = e.getRawX() - mView.getTranslationX();
                mMotionDownY = e.getRawY() - mView.getTranslationY();
                imageL= (int) mView.getX();
                imageT= (int) mView.getY();
                Log.d("imageview"," down");
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
            {
                mView.setTranslationX(e2.getRawX() - mMotionDownX);
                mView.setTranslationY(e2.getRawY() - mMotionDownY);
                imageL= (int) mView.getX();
                imageT= (int) mView.getY();
                if((distanceX==0)&&(distanceY==0))
                {
                    Log.d("imageview"," zoomed");
                }

                return true;
            }
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d("imageview"," tapped");
                return true;
            }

        };
    }
}