package com.example.finaltrial;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ColorBlobDetector {

    static double  areaMax;
    static double areaMax1;
    static double areaMax2;

    // Lower and Upper bounds for range checking in HSV color space

    // this is for purple
    ArrayList<Scalar> mLowerBound = new ArrayList<>();
    ArrayList<Scalar> mUpperBound = new ArrayList<>();


    // this is for yellow
    ArrayList<Scalar> mLowerBound1 = new ArrayList<>();
    ArrayList<Scalar> mUpperBound1 = new ArrayList<>();


    // this is for cyan
    ArrayList<Scalar> mLowerBound2 = new ArrayList<>();
    ArrayList<Scalar> mUpperBound2 = new ArrayList<>();

    // Minimum contour area in percent for contours filtering
    private static double mMinContourArea = 0.5;
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(10,10,30,0);
    private Mat mSpectrum = new Mat();
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
    private List<MatOfPoint> mContours1 = new ArrayList<MatOfPoint>();
    private List<MatOfPoint> mContours2 = new ArrayList<MatOfPoint>();

    // Cache
    Mat mPyrDownMat = new Mat();
    Mat mHsvMat = new Mat();
    Mat mMask = new Mat();
    Mat mMask1 = new Mat();
    Mat mMask2 = new Mat();
    Mat mDilatedMask = new Mat();
    Mat mHierarchy = new Mat();
    Mat blurredImage = new Mat();

    Size s = new Size(3,3);

    int count = 0;

    public void setColorRadius(Scalar radius) {
        mColorRadius = radius;
    }


    public void setHsvColor(Scalar hsvColor, int count) {

        this.count = count;

        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ?   hsvColor.val[0]-mColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0]+mColorRadius.val[0] <= 255) ? hsvColor.val[0]+mColorRadius.val[0] : 255;

        Scalar minScalar = new Scalar(0);
        Scalar maxScalar = new Scalar(0);

        minScalar.val[0] = minH;
        maxScalar.val[0] = maxH;

        minScalar.val[1] =  hsvColor.val[1] - mColorRadius.val[1];
        maxScalar.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        minScalar.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        maxScalar.val[2] = hsvColor.val[2] + mColorRadius.val[2];

        minScalar.val[3] = 0;
        maxScalar.val[3] = 255;

        // ensures that the value of minH and maxH are positive



        if(count < 3){

            mLowerBound.add(count, minScalar);
            mUpperBound.add(count, maxScalar);


        }else if(count < 6){
            mLowerBound1.add(count - 3, minScalar);
            mUpperBound1.add(count - 3, maxScalar);

        } else if(count < 9){
            minScalar.val[2] = minScalar.val[2] + 20;
            maxScalar.val[2] = maxScalar.val[2] - 20;
            mLowerBound2.add(count - 6, minScalar);
            mUpperBound2.add(count - 6, maxScalar);
        }


    }

    public Mat getSpectrum() {
        return mSpectrum;
    }

    public void setMinContourArea(double area) {
        mMinContourArea = area;
    }


    public void process(Mat rgbaImage, int count) {

        ArrayList<Mat> masks= new ArrayList<>();
        Mat mMask1 = new Mat();
        Mat mMask2 = new Mat();
        Mat mMask3 = new Mat();

        Mat intermidiate = new Mat();
        masks.add(0, mMask1);
        masks.add(1, mMask2);
        masks.add(2, mMask3);

        // for all of them
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);
        //Imgproc.blur(mPyrDownMat,blurredImage, s);
        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

        if(count >= 0 && this.count >= 2){
            for(int i = 0; i < 3; i++ ){
                Core.inRange(mHsvMat, mLowerBound.get(i), mUpperBound.get(i), masks.get(i));
            }

            Core.bitwise_or(masks.get(0), masks.get(1), intermidiate);
            Core.bitwise_or(intermidiate, masks.get(2), mMask);


            Imgproc.dilate(mMask, mDilatedMask, new Mat());
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            double maxArea = 0;
            Iterator<MatOfPoint> each = contours.iterator();
            while (each.hasNext()) {
                MatOfPoint wrapper = each.next();
                double area = Imgproc.contourArea(wrapper);
                if (area > maxArea)
                    maxArea = area;
            }
            areaMax  = maxArea;
            // Filter contours by area and resize to fit the original image size
            mContours.clear();
            each = contours.iterator();
            while (each.hasNext()) {
                MatOfPoint contour = each.next();
                if (Imgproc.contourArea(contour) >= maxArea) {
                    Core.multiply(contour, new Scalar(4,4), contour);
                    mContours.add(contour);

                }
            }
        }if(count >= 1 && this.count >= 5){

            for(int i = 0; i < 3; i++ ){
                Core.inRange(mHsvMat, mLowerBound1.get(i), mUpperBound1.get(i), masks.get(i));
            }

            Core.bitwise_or(masks.get(0), masks.get(1), intermidiate);
            Core.bitwise_or(intermidiate, masks.get(2), mMask);

            Imgproc.dilate(mMask, mDilatedMask, new Mat());
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            double maxArea = 0;
            Iterator<MatOfPoint> each = contours.iterator();
            while (each.hasNext()) {
                MatOfPoint wrapper = each.next();
                double area = Imgproc.contourArea(wrapper);
                if (area > maxArea)
                    maxArea = area;
            }
            areaMax1  = maxArea;
            // Filter contours by area and resize to fit the original image size
            mContours1.clear();
            each = contours.iterator();
            while (each.hasNext()) {
                MatOfPoint contour = each.next();
                if (Imgproc.contourArea(contour) >= mMinContourArea*maxArea) {
                    Core.multiply(contour, new Scalar(4,4), contour);
                    mContours1.add(contour);

                }
            }
        }if(count >= 2 && this.count >= 8){
            for(int i = 0; i < 3; i++ ){
                Core.inRange(mHsvMat, mLowerBound2.get(i), mUpperBound2.get(i), masks.get(i));
            }

            Core.bitwise_or(masks.get(0), masks.get(1), intermidiate);
            Core.bitwise_or(intermidiate, masks.get(2), mMask);

            Imgproc.dilate(mMask, mDilatedMask, new Mat());
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            double maxArea = 0;
            Iterator<MatOfPoint> each = contours.iterator();
            while (each.hasNext()) {
                MatOfPoint wrapper = each.next();
                double area = Imgproc.contourArea(wrapper);
                if (area > maxArea)
                    maxArea = area;
            }
            areaMax2  = maxArea;
            // Filter contours by area and resize to fit the original image size
            mContours2.clear();
            each = contours.iterator();
            while (each.hasNext()) {
                MatOfPoint contour = each.next();
                if (Imgproc.contourArea(contour) >= mMinContourArea*maxArea) {
                    Core.multiply(contour, new Scalar(4,4), contour);
                    mContours2.add(contour);
                }
            }
        }
    }
    public List<MatOfPoint> getContours(int choose){
        if(choose ==0){
            return mContours;
        }else if(choose  == 1){
            return  mContours1;
        }else if(choose == 2){
            return mContours2;
        }
        return null;
    }


}