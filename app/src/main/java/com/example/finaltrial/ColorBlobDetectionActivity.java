package com.example.finaltrial;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;
import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.SurfaceView;
import android.widget.Toast;

public class ColorBlobDetectionActivity extends RosActivity implements OnTouchListener, CvCameraViewListener2 {



    final double RESIZE_X = 10/128.0;
    final double RESIZE_Y = 10/72.0;

    static List<Integer> center;
    static List<Integer> center1;
    static List<Integer> center2;

    static List<Integer> ycenter;
    static List<Integer> ycenter1;
    static List<Integer> ycenter2;


    static List<Double> area;
    static List<Double> area1;
    static List<Double> area2;

    static List<Integer> ccenter;
    static List<Integer> ccenter1;
    static List<Integer> ccenter2;

    static List<Integer> yccenter;
    static List<Integer> yccenter1;
    static List<Integer> yccenter2;

    static List<Double> carea;
    static List<Double> carea1;
    static List<Double> carea2;


    int count;
    int counter;

    private static final String  TAG              = "OCVSample::Activity";

    private boolean              mIsColorSelected = false;
    private Mat                  mRgba;
    private Scalar               mBlobColorRgba;
    private Scalar               mBlobColorHsv;
    private ColorBlobDetector    mDetector;
    private Mat                  mSpectrum;
    private Size                 SPECTRUM_SIZE;
    private Scalar               CONTOUR_COLOR;

    private JavaCameraView mOpenCvCameraView;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public ColorBlobDetectionActivity() {
        super("with opencv and rosjava", "Image Processor");


        center = new ArrayList<>();
        center1 = new ArrayList<>();
        center2 = new ArrayList<>();

        ycenter = new ArrayList<>();
        ycenter1 = new ArrayList<>();
        ycenter2 = new ArrayList<>();

        area = new ArrayList<>();
        area1 = new ArrayList<>();
        area2 = new ArrayList<>();

        ccenter = new ArrayList<>();
        ccenter1 = new ArrayList<>();
        ccenter2 = new ArrayList<>();

        yccenter = new ArrayList<>();
        yccenter1 = new ArrayList<>();
        yccenter2 = new ArrayList<>();

        carea = new ArrayList<>();
        carea1 = new ArrayList<>();
        carea2 = new ArrayList<>();

        center.add(0);
        center.add(0);
        center.add(0);
        center1.add(0);
        center1.add(0);
        center1.add(0);
        center1.add(0);
        center2.add(0);
        center2.add(0);
        center2.add(0);
        ycenter.add(1);
        ycenter1.add(1);
        ycenter2.add(1);
        ycenter.add(1);
        ycenter1.add(1);
        ycenter2.add(1);
        ycenter.add(1);
        ycenter1.add(1);
        ycenter2.add(1);

        area.add(0.0);
        area.add(0.0);
        area2.add(0.0);
        area2.add(0.0);
        area1.add(0.0);
        area1.add(0.0);
        area1.add(0.0);
        area2.add(0.0);
        area.add(0.0);


        ccenter.add(0);
        ccenter.add(0);
        ccenter.add(0);
        ccenter1.add(0);
        ccenter1.add(0);
        ccenter1.add(0);
        ccenter1.add(0);
        ccenter2.add(0);
        ccenter2.add(0);
        ccenter2.add(0);
        yccenter.add(1);
        yccenter1.add(1);
        yccenter2.add(1);
        yccenter.add(1);
        yccenter1.add(1);
        yccenter2.add(1);
        yccenter.add(1);
        yccenter1.add(1);
        yccenter2.add(1);

        carea.add(0.0);
        carea.add(0.0);
        carea2.add(0.0);
        carea2.add(0.0);
        carea1.add(0.0);
        carea1.add(0.0);
        carea1.add(0.0);
        carea2.add(0.0);
        carea.add(0.0);
        Log.i(TAG, "Instantiated new " + this.getClass());
    }


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        mOpenCvCameraView = findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        counter = 0;
        count = 0;

    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255,0,0,255);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }



    public boolean onTouch(View v, MotionEvent event) {
        int cols = mRgba.cols();
        int rows = mRgba.rows();

        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        Rect touchedRect = new Rect();

        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;

        touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

        mDetector.setHsvColor(mBlobColorHsv, count);
        if(count < 9)
            count++;
        Toast.makeText(getApplicationContext(), "" +count, Toast.LENGTH_SHORT).show();
        //Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE, 0, 0, Imgproc.INTER_LINEAR_EXACT);

        mIsColorSelected = true;

        touchedRegionRgba.release();
        touchedRegionHsv.release();

        if(count >= 2)
            counter = 1;
        if(count >= 5)
            counter = 2;
        if(count >= 8)
            counter = 3;

        return false; // don't need subsequent touch events
    }
    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        NodeMain node = new PublishingNode();
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(node, nodeConfiguration);
    }

    // the order will be purple,yellow,cyan

    static int width;
    static int height;
    Point point = new Point();
    float r[] = new float[10];
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        width = mRgba.width();
        height = mRgba.height();
        for(int j = 0; j < counter; j++){
            if (mIsColorSelected) {
                mDetector.process(mRgba, counter);
                List<MatOfPoint> contours = mDetector.getContours(j);
//                Log.e(TAG, "Contours count: " + contours.size());
                Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR, 2);
                //this is code added by myself on the original
                if (!contours.isEmpty()) {
                    for (int i = 0; i < contours.size(); i++) {
                        //        Log.e("=================", "" + contours.get(i).size().height);
                        double contour_area = Imgproc.contourArea(contours.get(i));
                        if (contour_area > 100) {

                            Rect rect = Imgproc.boundingRect(contours.get(i));
                            if(j == 0 && i < 3){
                                MatOfPoint2f NewMtx = new MatOfPoint2f();
                                contours.get(i).convertTo(NewMtx, CvType.CV_32FC2);
                                Imgproc.minEnclosingCircle(NewMtx,point, r);


                                Imgproc.circle(mRgba,point,(int)r[0],new Scalar(0,255,0));

                                area.set(0, (double)r[0]);
                                center.set(0, (int)(point.y * RESIZE_Y));
                                ycenter.set(0, (int)(point.x * RESIZE_X));

                            }else if(j == 1 && i < 3){
                                ycenter1.set(i, (int)((rect.x + rect.width/2)*RESIZE_X));
                                center1.set(i,(int)((rect.y + rect.height/2)* RESIZE_Y));
                                area1.set(i, rect.area());
                            }else if(j == 2 && i < 3){
                                ycenter2.set(i, (int)((rect.x + rect.width/2)* RESIZE_X));
                                center2.set(i,(int)((rect.y + rect.height/2)*RESIZE_Y));
                                area2.set(i, rect.area());
                            }

                        }
                    }
                }
            }

        }
        for(int i = 0; i < 3; i++ ){
            yccenter.set(i,ycenter.get(i));
            yccenter1.set(i,ycenter1.get(i));
            yccenter2.set(i,ycenter2.get(i));
            ccenter.set(i,center.get(i));
            ccenter1.set(i,center1.get(i));
            ccenter2.set(i, center2.get(i));


            carea.set(i, area.get(i));
            carea1.set(i, area1.get(i));
            carea2.set(i, area2.get(i));

        }
        //this is the stupidest algorithm ever found in the history
        //after this the detection was working

        for(int i = 0; i < 3; i++){
            area.set(i, 0.0);
            area1.set(i, 0.0);
            area2.set(i, 0.0);

            center.set(i, 0);
            center1.set(i, 0);
            center2.set(i, 0);

            ycenter.set(i, 0);
            ycenter1.set(i, 0);
            ycenter2.set(i, 0);

        }
        getTile();

        return mRgba;
    }
    static String getTile(){
        String purple =  carea.get(0) + " "+ ccenter.get(0) + " "+ yccenter.get(0) +" ";
        String yellow =  carea1.get(0) + " "+ ccenter1.get(0) + " "+ yccenter1.get(0) + " " + carea1.get(1) + " " + ccenter1.get(1)+ " "+ yccenter1.get(1) + " " + carea1.get(2) + " " + ccenter1.get(2)+ " "+ yccenter1.get(2)+ " ";
        String cyan =  carea2.get(0) + " "+ ccenter2.get(0) + " "+ yccenter2.get(0)+ " " + carea2.get(1) + " " + ccenter2.get(1)+ " "+ yccenter2.get(1) + " " + carea2.get(2) + " " + ccenter2.get(2)+ " "+ yccenter2.get(2);
        Log.e("tttt", "height " + height +" width "+ width);

        return purple+yellow+cyan;
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }
}