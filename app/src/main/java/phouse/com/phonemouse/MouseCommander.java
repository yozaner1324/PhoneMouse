package phouse.com.phonemouse;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.view.Surface;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.nio.ByteBuffer;
import java.util.Collections;

import static org.opencv.video.Video.estimateRigidTransform;

public class MouseCommander
{
    private Connection conn;
    private Mat prevPic;
    private String cameraId;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private CameraDevice cameraDev;
    private android.hardware.camera2.CaptureRequest captureRequest;
    private CaptureRequest.Builder captureRequestBuilder;
    private ImageReader imageReader = ImageReader.newInstance(100, 100, ImageFormat.JPEG, 1);
    private CameraCaptureSession cameraCapSession;
    private CameraManager cameraManager = MouseActivity.getCameraMan();

    public MouseCommander(Connection connection)
    {
        conn = connection;
        prevPic = null;
    }

    public void leftClick()
    {
        conn.send("left");
    }

    public void releaseLeftClick()
    {
        conn.send("release");
    }

    public void rightClick()
    {
        conn.send("right");
    }

    public void moveCursor(float x, float y)
    {
        conn.send("move " + Math.round(x) + " " + Math.round(y));
    }

    public void scroll(float amount)
    {
        conn.send("scroll " + Math.round(amount));
    }

    public void startMotionTracking()
    {
        openBackgroundThread();
        setUpCamera();
        openCamera();

        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener()
        {
            @Override
            public void onImageAvailable(ImageReader reader)
            {
                Image image = reader.acquireLatestImage();
                if (image != null)
                {
                    // convert to matrix
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.capacity()];
                    buffer.get(bytes);
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
                    Bitmap bmp32 = bmp.copy(Bitmap.Config.ARGB_8888, true);
                    Mat pic = new Mat();
                    Utils.bitmapToMat(bmp32, pic);

                    if(prevPic != null)
                    {
                        Mat flow = new Mat();
                        pic.copyTo(flow);
                        estimateRigidTransform(prevPic, pic, false).copyTo(flow);

                        if(!flow.empty())
                        {
                            float x = (float) flow.get(1, 2)[0];
                            float y = (float) -flow.get(0, 2)[0];

                            if(Math.abs(x) < 1)
                            {
                                x = 0;
                            }
                            if(Math.abs(y) < 1)
                            {
                                y = 0;
                            }
                            moveCursor(x*10, y*10);
                        }
                    }

                    prevPic = new Mat();
                    // copy picture to prevPic
                    pic.copyTo(prevPic);

                    image.close();
                }
            }
        }, backgroundHandler);

    }

    public void stopMotionTracking()
    {
        try
        {
            cameraCapSession.abortCaptures();
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
        cameraCapSession.close();
        backgroundThread.quitSafely();
        cameraDev.close();
        imageReader.close();
    }

    public void disconnect()
    {
        conn.disconnect();
    }

    private void createPreviewSession()
    {
        try
        {
            Surface previewSurface = imageReader.getSurface();
            captureRequestBuilder = cameraDev.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //captureRequestBuilder.set(FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
            captureRequestBuilder.addTarget(previewSurface);

            cameraDev.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback()
                    {

                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession)
                        {
                            if (cameraDev != null)
                            {
                                try
                                {
                                    captureRequest = captureRequestBuilder.build();
                                    cameraCapSession = cameraCaptureSession;
                                    cameraCapSession.setRepeatingRequest(captureRequest, null, backgroundHandler);
                                }
                                catch (CameraAccessException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
                        {

                        }
                    }, backgroundHandler);
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    private void setUpCamera()
    {
        try
        {
            for (String cameraId : cameraManager.getCameraIdList())
            {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK)
                {
                    this.cameraId = cameraId;
                }
            }
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    private void openCamera()
    {
        try
        {
            if (ActivityCompat.checkSelfPermission(ConnectActivity.getAppContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            {
                cameraManager.openCamera(cameraId, new CameraDevice.StateCallback()
                {
                    @Override
                    public void onOpened(CameraDevice cameraDevice)
                    {
                        cameraDev = cameraDevice;
                        createPreviewSession();
                    }

                    @Override
                    public void onDisconnected(CameraDevice cameraDevice)
                    {
                        cameraDevice.close();
                        cameraDev = null;
                    }

                    @Override
                    public void onError(CameraDevice cameraDevice, int error)
                    {
                        cameraDevice.close();
                        cameraDev = null;
                    }
                }, backgroundHandler);
            }
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    private void openBackgroundThread()
    {
        backgroundThread = new HandlerThread("camera_thread");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }
}
