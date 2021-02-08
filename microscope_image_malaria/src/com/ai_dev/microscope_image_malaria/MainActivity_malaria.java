package com.ai_dev.microscope_image_malaria;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
//import org.opencv.samples.tutorial3.R;
//import org.opencv.samples.tutorial3.Tutorial3View;
//import org.opencv.samples.tutorial3.Tutorial3View;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity_malaria extends Activity implements CvCameraViewListener2 {
	
	private static final String    TAG = "OCVSample::Activity";
	private CameraBridgeViewBase   mOpenCvCameraView;
	//private Tutorial3View mOpenCvCameraView;
	
	private static final int       VIEW_MODE_RESET     = 0;
    private static final int       VIEW_MODE_ANALYSE     = 1;
    private static final int       VIEW_MODE_CANNY    = 2;
    private static final int       VIEW_MODE_FEATURES = 5;
	
    private int                    mViewMode;
    private Mat                    mRgba;
    private Mat                    mIntermediateMat;
    private Mat                    mGray;
    private Mat					   mDummy;
    private Mat					   mRgbaT;
    
    private MenuItem               mItemPreviewRGBA;
    private MenuItem               mItemPreviewGray;
    private MenuItem               mItemPreviewCanny;
    private MenuItem               mItemPreviewFeatures;
    
    private Socket client;
    private FileInputStream fileInputStream;
    private BufferedInputStream bufferedInputStream;
    private OutputStream outputStream;
    
    private FileOutputStream fileOutputStream;
    private BufferedOutputStream bufferedOutputStream;
    private InputStream inputStream;
    private static int filesize = 10000; // filesize temporary hardcoded 
    private static int bytesRead;
    private static int current = 0;
    
    boolean done = false;
	boolean plot = false;
	int reset_counter = 0;
	
	static String CSVfilename;
	static String IP_address = "54.67.71.5";//"54.67.71.5"; //52.2.206.81
	static String local_IP_address = "192.168.1.114";
	static int Port_number = 8000;
	
	public final static String EXTRA_MESSAGE = "com.ai_dev.microscope_image_malaria.MESSAGE";
	private static final VideoCapture mCamera1 = null;
	public String testImage;
	public String testCSV;
	public static String file_id;
	
	Button btn_capture;
	Button btn_options;
	
	private List<Size> mResolutionList;
	//VideoCapture camera = mCamera;
	private Camera mCamera;
	//Camera.Parameters params = mCamera.getParameters();
	//List<Camera.Size> sizes = params.getSupportedPictureSizes();
	
	int mFrameWidth;
	int mFrameHeight;
	
	//************That staf****************//
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	    @Override
	    public void onManagerConnected(int status) {
	        switch (status) {
	            case LoaderCallbackInterface.SUCCESS:
	            {
	                Log.i(TAG, "OpenCV loaded successfully");
	                mOpenCvCameraView.enableView();
	            } break;
	            default:
	            {
	                super.onManagerConnected(status);
	            } break;
	        }
	    }
	};
	
	//*****************that other staff*************//
	
	/* public MainActivity_malaria() {
	        Log.i(TAG, "Instantiated new " + this.getClass());
	    }*/
		// private CameraBridgeViewBase mOpenCvCameraView;
	

	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	     Log.i(TAG, "called onCreate");
	     super.onCreate(savedInstanceState);
	     getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	     getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	             WindowManager.LayoutParams.FLAG_FULLSCREEN);
	     setContentView(R.layout.activity_main_malaria);
	     
	     /*disconnectCamera();
	     mFrameWidth = (int) sizes.get(1).width;
	     mFrameHeight = (int) sizes.get(1).height;*/

	     //arams.setPreviewSize(mFrameWidth, mFrameHeight);
	     //mCamera.setParameters(params); // mCamera is a Camera object
	     
	     mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView); //camerabridgeview staf
	     //mOpenCvCameraView = (Tutorial3View) findViewById(R.id.HelloOpenCvView);
	     mOpenCvCameraView.setMaxFrameSize(1000, 1000); //(3264, 2448)
	     mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
	     mOpenCvCameraView.setCvCameraViewListener(this);
	     
	     //mOpenCvCameraView.SetCaptureFormat(1); //test
	     
	     //mResolutionList = mOpenCvCameraView.getResolutionList();
	     
	     btn_options = (Button) findViewById(R.id.Button_options);
	     btn_capture = (Button) findViewById(R.id.Button_capture);
	     //IP_address = "54.67.71.5"; //52.2.206.81
	     //local_IP_address = "192.168.1.109";
	     
	     //Size resolution = new Size(3264, 2448);
	    // mOpenCvCameraView.setMaxFrameSize(3264, 2448);
	    /////// Size resolution = mResolutionList.get();
	     //Size resolution = new Size(3264, 2448);
         //////mOpenCvCameraView.setResolution(resolution);
         int mwidth = mOpenCvCameraView.getMeasuredWidth();
         int mheight = mOpenCvCameraView.getMeasuredHeight();
         String caption = Integer.valueOf(mwidth).toString() + "x" + Integer.valueOf(mheight).toString();
         Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();
	     
	     
	 }
	 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemPreviewRGBA = menu.add("RESET");
        mItemPreviewGray = menu.add("ANALYSE");
        mItemPreviewCanny = menu.add("VCanny");
      //  mItemPreviewFeatures = menu.add("Find features");
        return true;
    }

	 @Override
	 public void onPause()
	 {
	     super.onPause();
	     if (mOpenCvCameraView != null)
	         mOpenCvCameraView.disableView();
	 }

	 public void onDestroy() {
	     super.onDestroy();
	     if (mOpenCvCameraView != null)
	         mOpenCvCameraView.disableView();
	 }

	 public void onCameraViewStarted(int width, int height) {
		 mRgba = new Mat(height, width, CvType.CV_8UC4);
	     mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
	     mGray = new Mat(height, width, CvType.CV_8UC1);
	 }

	 public void onCameraViewStopped() {
		 mRgba.release();
	     mGray.release();
	     mIntermediateMat.release();
	     //mRgbaT.release();
	 }

	 public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		 final int viewMode = mViewMode; mViewMode=VIEW_MODE_ANALYSE; //delete extra line after desire test
		 //Mat mRgbaT = mRgba.t();
		 
		 //Mat mRgbaT_t = mRgba.t();
		 //Imgproc.cvtColor(inputFrame.gray(), mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
		 switch (viewMode) {
	        case VIEW_MODE_ANALYSE:
	        	done = false;
	        	mRgbaT = mRgba.t();
	        	
	        	if(!done){
	        		//*****flip image*** source http://answers.opencv.org/question/20325/how-can-i-change-orientation-without-ruin-camera-settings///
		    		 //Mat mRgbaT = mRgba.t(); //uncomment to get gray scale
		    		 //Mat mRgbaT = mRgba.t();
		    	    
		    		 //return mRgbaT;
		            // input frame has gray scale format
		            //Imgproc.cvtColor(inputFrame.gray(), mRgba, Imgproc.COLOR_GRAY2RGBA, 4); //change to black and white from here
		            Imgproc.cvtColor(inputFrame.rgba(), mRgba, Imgproc.COLOR_BGR2RGB, 4); //COLOR_GRAY2RGBA
		            ///////
		            //Core.flip(mRgba, mRgba, 1);
		    	     //Imgproc.resize(mRgbaT, mRgbaT, mRgba.size());
		            Core.flip(mRgba.t(), mRgbaT,1);
		            ///////
		            //N:B color distortion in image is due to file writing, not fliping nor transpose
		            ///
		           File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Microscope_images");

		            if (!mediaStorageDir.exists()) {
		                if (!mediaStorageDir.mkdirs()) {
		                    Log.e(TAG, "failed to create directory");
		                    return null;
		                }
		            }

		            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		            File mediaFile, dummyFile;
		            file_id = "testimage_" + timeStamp;
		            mediaFile = new File(mediaStorageDir.getPath() + File.separator + file_id + ".jpg");
		            dummyFile = new File(mediaStorageDir.getPath() + File.separator + "dummy" + ".jpg");
		            String temp_file = mediaFile.toString();
		            String temp_dummy = dummyFile.toString();
		            
		         		            
		            //Imgcodecs.imwrite(temp_file, mRgbaT);
		            Imgcodecs.imwrite(temp_file, mRgbaT);
		            Imgproc.cvtColor(mRgbaT, mRgbaT, Imgproc.COLOR_BGR2RGB);
		            
		         
		            
		            //mDummy = new Mat();
		            //mDummy =  Imgcodecs.imread(temp_dummy); // test: go hand in hand with -> File file = dummyFile;
		            //Imgproc.cvtColor(mDummy, mDummy, Imgproc.COLOR_BGR2RGB);
		            //Imgcodecs.imwrite(temp_file, mRgba); //color image
		            //done = true;
		            //mViewMode = VIEW_MODE_RESET;
		            
		            ///loading image to server
		            // http://stackoverflow.com/questions/4126625/how-to-send-a-file-in-android-from-mobile-to-server-using-http
		            //http://lakjeewa.blogspot.ug/2012/03/simple-android-application-to-send-file.html
		            //File file = new File("/mnt/sdcard/input.jpg"); //create file instance
		            File file = mediaFile;//uncomment after testing line below
		            //File file = dummyFile;//test: load dummy file instead. goes hand with changing mRgbaT source for viewmode reset. for ImageView, change intent to use temp_dummy 

	                try {

	                    //client = new Socket(IP_address, Port_number); //actual server. port 8000
	                    client = new Socket(local_IP_address, 4444); //local server 4444

	                    byte[] mybytearray = new byte[(int) file.length()]; //create a byte array to file

	                    fileInputStream = new FileInputStream(file);
	                    bufferedInputStream = new BufferedInputStream(fileInputStream);

	                    bufferedInputStream.read(mybytearray, 0, mybytearray.length); //read the file

	                    outputStream = client.getOutputStream();
	                    //inputStream = client.getInputStream();//take away if fail

	                    outputStream.write(mybytearray, 0, mybytearray.length); //write file to the output stream byte by byte
	                    outputStream.flush();//place
	                    bufferedInputStream.close();//these
	                    outputStream.close();Imgproc.circle(mRgba, new Point(150,150), 50, new Scalar(0,255,255));//lines//3 
	                    //client.close();//after csv receiving code
	                    
	                    //..............csv receiving code
	                    File folder = new File(Environment.getExternalStorageDirectory()
	                            + "/CSVFolder");

	                    boolean var = false;
	                    if (!folder.exists())
	                        var = folder.mkdir();

	                    System.out.println("" + var);
	                    
	                    try{
	                    	//Imgproc.circle(mRgba, new Point(150,150), 50, new Scalar(0,255,255));
	                    	final String CSVfilename = folder.toString() + "/" + file_id + ".csv";
	                    	com.ai_dev.microscope_image_malaria.MainActivity_malaria.CSVfilename = CSVfilename;
		                    	                    	
		                    byte[] CSVbytearray = new byte[filesize];    //create byte array to buffer the file
		                    
		                    File CSfile = new File(CSVfilename); Imgproc.circle(mRgba, new Point(150,150), 50, new Scalar(0,255,255));Imgproc.circle(mRgba, new Point(150,150), 50, new Scalar(0,255,255));

		                    client = new Socket(IP_address, Port_number);//port =8000 //Imgproc.circle(mRgba, new Point(150,150), 50, new Scalar(0,0,255)); //4444
		                    //client = new Socket(local_IP_address, 4444); //local server
		                    inputStream = client.getInputStream(); //2
		    		        fileOutputStream = new FileOutputStream(CSVfilename);
		    		        bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

		    		        System.out.println("Receiving...");

		    		        //following lines read the input slide file byte by byte
		    		        bytesRead = inputStream.read(CSVbytearray, 0, CSVbytearray.length);done = true;
		    		        current = bytesRead;

		    		        do {
		    		            bytesRead = inputStream.read(CSVbytearray, current, (CSVbytearray.length - current));
		    		            if (bytesRead >= 0) {
		    		                current += bytesRead; 
		    		            }
		    		        } while (bytesRead > -1);
		    		        
		    		        bufferedOutputStream.write(CSVbytearray, 0, current);
		    		        bufferedOutputStream.flush();
		    		        bufferedOutputStream.close();
		    		        inputStream.close();
		    		        
		    		       
		    		        //bufferedInputStream.close();//these
		                    //outputStream.close();//lines //1
		    		        //client.close();
		                    
		                    
		                    //......CSVfilename end of csv receiving code

	                    	
	                    }catch(IOException e){
	                    	e.printStackTrace();
	                    }
	                    //outputStream.close();//1.1
	                    client.close();
	                    
	                 	                    
	                    //text.setText("File Sent");
	                    //done = true;
	                    
	                } catch (UnknownHostException e) {
	                    e.printStackTrace();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                
	                done = true;
	                plot=true;
	                reset_counter = 0;
	              //*****Imageview test code************//// block off this section to do screen cast
                	/*uncomment_me Intent intent = new Intent(this, ImageView_malaria.class);
                	//intent.putExtra(EXTRA_MESSAGE, temp_file); // uncomment after testing line below
                	intent.putExtra(EXTRA_MESSAGE, temp_dummy); //test: sending dummy filename to ImageView
                	startActivity(intent);uncomment_me*/
                //*****end of imageview test code****////
	                
		            mViewMode = VIEW_MODE_CANNY; //uncomment
		            ///end of loading image 
		            
	        	}
	        		            
	            break;
	        case VIEW_MODE_RESET:
	            // input frame has RBGA format
	        	//reset_counter++;
	        	
	            mRgba = inputFrame.rgba();
	            
	           if(done){
	        	   //Core.flip(mDummy.t(), mRgba, 0);
	        	   Core.flip(mRgbaT.t(), mRgba, 0);
	        	   //Imgproc.resize(mDummy, mRgba, mRgba.size());
	        	   //mRgba = mDummy; //plot on dummy image
	        	   //Imgproc.circle(mRgba, new Point(150,150), 50, new Scalar(255,0,0));
	        	 //....CSV read debug code...
	        	   double point_x1 = 0,point_x2 = 0, point_y1=0, point_y2=0;
                   BufferedReader fileReader = null;
                   try{
                	   //Imgproc.circle(mRgba, new Point(150,150), 50, new Scalar(255,0,0));
                   	String line = "";//Imgproc.circle(mRgba, new Point(150,150), 50, new Scalar(0,255,0));
                   	fileReader = new BufferedReader(new FileReader(CSVfilename));Imgproc.circle(mRgba, new Point(150,150), 50, new Scalar(0,255,0));
                   	while((line = fileReader.readLine())!=null){
                   		Imgproc.circle(mRgba, new Point(150,200), 50, new Scalar(255,0,0));
                   		String[] row = line.split(",");Imgproc.circle(mRgba, new Point(200,200), 50, new Scalar(0,0,255));
                   		System.out.println("x="+row[0] +" and y="+row[1]);
                   		//point_x = Integer.parseInt(row[0]); point_y = Integer.parseInt(row[1]); Imgproc.circle(mRgba, new Point(250,200), 50, new Scalar(255,0,255));
                   		point_x1 = Double.parseDouble(row[0]); point_y1 = Double.parseDouble(row[1]); Imgproc.circle(mRgba, new Point(250,200), 50, new Scalar(255,0,255));
                   		point_x2 = Double.parseDouble(row[2]); point_y2 = Double.parseDouble(row[3]); //Imgproc.circle(mRgba, new Point(250,200), 50, new Scalar(255,0,255));
                   		//Imgproc.circle(mRgba, new Point(point_x,point_y), 50, new Scalar(0,255,255)); //Imgproc.circle(mRgba, new Point(150,200), 50, new Scalar(255,0,0));
                   		Imgproc.rectangle(mRgba, new Point(point_x1, point_y1), new Point(point_x2, point_y2), new Scalar(255,0,0));
                   		
                   	}
                   	
                   // mRgba = mRgbaT;
                   	//return mDummy;
                   	
                   }catch(Exception e){
                   	
                   }finally{
                   	try {
                   		 fileReader.close();
                   		 } catch (IOException e) {
                   		 System.out.println("Error while closing fileReader !!!");
                   		 e.printStackTrace();
                   		 }
                   	
                   }
                   //....end of CSV read debug code...
                   
	        		//Imgproc.circle(mRgba, new Point(150,150), 50, new Scalar(0,255,255)); // test lines for plotting circles
	        		//Imgproc.circle(mRgba, new Point(60,250), 50, new Scalar(0,255,255));
	            	//Imgproc.cir
	        		//Toast.makeText(getApplicationContext(), "plotting", Toast.LENGTH_LONG).show();
	           }
	           //Toast.makeText(getApplicationContext(), "out", Toast.LENGTH_LONG).show();
	            //done = false;
	            break;
	            
	        case VIEW_MODE_CANNY:
	        	mRgba = inputFrame.rgba();
	        	break;
	        }
		
		//*****flip image*** source http://answers.opencv.org/question/20325/how-can-i-change-orientation-without-ruin-camera-settings///
		/* Mat mRgbaT = mRgba.t();
	     Core.flip(mRgba.t(), mRgbaT, 1);
	     Imgproc.resize(mRgbaT, mRgbaT, mRgba.size());
		 return mRgbaT;*/
		 //return mRgbaT;
		 return mRgba; //main return. its the Gold version. so uncomment if all else fails
	     //return inputFrame.rgba();
	 }
	//*****************that other staff*************//
	 
	 public boolean onOptionsItemSelected(MenuItem item) {
	        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

	        if (item == mItemPreviewRGBA) {
	            mViewMode = VIEW_MODE_RESET;
	        } else if (item == mItemPreviewGray) {
	            mViewMode = VIEW_MODE_ANALYSE;
	        } else if (item == mItemPreviewCanny) {
	            mViewMode = VIEW_MODE_CANNY;
	        } else if (item == mItemPreviewFeatures) {
	            mViewMode = VIEW_MODE_FEATURES;
	        }

	        return true;
	    }

	
	 public void OptionsButton(View view){
	//*****Imageview test code************////
     	Intent intent1 = new Intent(this, OptionsActivity.class);
     	//intent.putExtra(EXTRA_MESSAGE, temp_file); // replace with temp_file for live tests
     	startActivity(intent1);
     //*****end of imageview test code****//// 
	 }
	 
	 public void CaptureButton(View view){
		 mViewMode = VIEW_MODE_ANALYSE;
	 }


	@Override
	public void onResume()
	{
	    super.onResume();
	    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
	}
	//****************That staf********************//
	
	
	/*@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_malaria);
	}*/

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_malaria, menu);
		return true;
	}*/

}
