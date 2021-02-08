package com.ai_dev.microscope_image_malaria;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageView_malaria extends Activity implements OnTouchListener {
	private static final String    TAG = "OCVSample::Activity";
	private ImageView imageview;
	private Bitmap bitmap = null;
	private String filename;
	
	private Mat mDummy;
	double point_x1 = 0,point_x2 = 0, point_y1=0, point_y2=0;
	float touch_x, touch_y;
	Vector<Double> x1_points = new Vector<Double>(); 
	Vector<Double> y1_points = new Vector<Double>(); 
	Vector<Double> x2_points = new Vector<Double>(); 
	Vector<Double> y2_points = new Vector<Double>();
	Vector<Double> prob = new Vector<Double>();
	
	double xx;
	int detections = 0;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_view_malaria);
		
		imageview = (ImageView) findViewById(R.id.image_view);
		Intent intent = getIntent();
		filename = intent.getStringExtra(MainActivity_malaria.EXTRA_MESSAGE);
	}
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	    @Override
	    public void onManagerConnected(int status) {
	        switch (status) {
	            case LoaderCallbackInterface.SUCCESS:
	            {
	                Log.i(TAG, "OpenCV loaded successfully");
	                //mOpenCvCameraView.enableView();
	                DisplayImage();
	                
	            } break;
	            default:
	            {
	                super.onManagerConnected(status);
	            } break;
	        }
	    }
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_view_malaria, menu);
		return true;
	}
	
	public void DisplayImage(){ //function to display image and bounding boxes
		
		//http://cs.ucsb.edu/~efujimoto/mobile/Other/MainActivity.java
		bitmap = BitmapFactory.decodeFile(filename);
		mDummy = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);
    	Utils.bitmapToMat(bitmap, mDummy);
    	
    	
    	
        BufferedReader fileReader = null;
        try{
     	   	String line = "";//Imgproc.circle(mRgba, new Point(150,150), 50, new Scalar(0,255,0));
     	   	//fileReader = new BufferedReader(new FileReader("/storage/emulated/0/CSVFolder/Test.csv"));
        	fileReader = new BufferedReader(new FileReader(MainActivity_malaria.CSVfilename));
        	while((line = fileReader.readLine())!=null){
        		String[] row = line.split(",");
        		System.out.println("x="+row[0] +" and y="+row[1]);
        		point_x1 = Double.parseDouble(row[0]); point_y1 = Double.parseDouble(row[1]); x1_points.addElement(point_x1); y1_points.addElement(point_y1);
        		point_x2 = Double.parseDouble(row[2]); point_y2 = Double.parseDouble(row[3]); x2_points.addElement(point_x2); y2_points.addElement(point_y2);
        		prob.addElement(Double.parseDouble(row[4]));
        		Imgproc.rectangle(mDummy, new Point(point_x1/1.5625, point_y1/0.9375), new Point(point_x2/1.5625, point_y2/0.9375), new Scalar(255,0,0));
        		detections++;
        		
        	}
        	
               	
        }catch(Exception e){
        	
        }finally{
        	try {
        		 fileReader.close();
        		 } catch (IOException e) {
        		 System.out.println("Error while closing fileReader !!!");
        		 e.printStackTrace();
        		 }
        	
        }
        //http://stackoverflow.com/questions/34252784/puttext-does-not-work-in-opencv3-0
        xx = prob.elementAt(1);
        //Imgproc.putText(mDummy,"#det: " + Double.toString(xx) , new Point(bitmap.getWidth() - 200, bitmap.getHeight()-10) , Core.FONT_ITALIC, 1.0, new Scalar(0,0,255));
        Imgproc.putText(mDummy,"#det: " + Integer.toString(detections) , new Point(bitmap.getWidth() - 200, bitmap.getHeight()-10) , Core.FONT_ITALIC, 1.0, new Scalar(0,0,255));
        //....end of CSV read debug code... */
    	
		//Imgproc.rectangle(mDummy, new Point(50, 50), new Point(100, 100), new Scalar(255,0,0));
		Utils.matToBitmap(mDummy, bitmap);
		imageview.setImageBitmap(bitmap);
		imageview.setOnTouchListener(this);
		
		//....save bounded image............//
		//http://stackoverflow.com/questions/649154/save-bitmap-to-location
		File mediaStorage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "microscope_bounded_images");
		if (!mediaStorage.exists()) {
            if (!mediaStorage.mkdirs()) {
                Log.e(TAG, "failed to create directory");
                return;
            }
        }
		
		File bounded_mediaFile;
		bounded_mediaFile = new File(mediaStorage.getPath() + File.separator + MainActivity_malaria.file_id + ".jpg");
		String bounded_image_file = bounded_mediaFile.toString();
		
		FileOutputStream out = null; //outputstream for writing file
		try {
		    out = new FileOutputStream(bounded_image_file);
		    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
		    // PNG is a lossless format, the compression factor (100) is ignored
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (out != null) {
		            out.close();
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}

		//....end of save bounded image....//
	}
	
	@Override
	public void onResume()
	{
	    super.onResume();
	    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		//int action
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			touch_x = event.getX();
			touch_y = event.getY();
			for(int i=0; i<detections;i++){
				if(((touch_x > x1_points.elementAt(i))&&(touch_x < x2_points.elementAt(i)))&&((touch_y < y1_points.elementAt(i))&&(touch_y > y2_points.elementAt(i)))){
					//xx++;
					Toast.makeText(this, Double.toString(prob.elementAt(i)), Toast.LENGTH_LONG).show();
					
					break;
				}
			}
			
			
		}
		return false;
	}

}
