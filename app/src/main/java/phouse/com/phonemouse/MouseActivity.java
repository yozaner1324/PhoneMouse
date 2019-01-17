package phouse.com.phonemouse;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;

public class MouseActivity extends AppCompatActivity
{

    private MouseCommander commander;
    private static CameraManager cam;

    static
    {
        OpenCVLoader.initDebug();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        cam = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        setContentView(R.layout.activity_mouse);

        Connection conn = (Connection) getIntent().getExtras().get("conn");
        commander = new MouseCommander(conn);

        // init control fragment
        ControlFragment fragment = (ControlFragment) getSupportFragmentManager().findFragmentById(R.id.frag);
        fragment.setup(commander);

        // load openCV
//        if(!OpenCVLoader.initDebug())
//        {
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mOpenCVCallBack);
//        }
//        else
//        {
//            mOpenCVCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//        }
        commander.startMotionTracking();
    }

//    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
//
//
//        @Override
//        public void onManagerConnected(int status){
//            if(status == LoaderCallbackInterface.SUCCESS)
//            {
//                // starts the image processing for mouse movement
//                commander.startMotionTracking();
//            }
//        }
//    };

    @Override
    public void onBackPressed()
    {
        commander.stopMotionTracking();
        commander.disconnect();
        super.onBackPressed();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this,  mOpenCVCallBack);
//    }

    public static CameraManager getCameraMan()
    {
        return cam;
    }
}
