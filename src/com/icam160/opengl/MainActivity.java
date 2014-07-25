package com.icam160.opengl;

import com.icam160.R;
import com.icam160.util.GPSTracker;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.TrafficStats;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends Activity implements SensorEventListener, LocationListener {
	
	private GLSurfaceView glSurfaceView;
	private boolean rendererSet = false;
	
	Vibrator vib;
	
	private Camera mCamera;
    private CameraPreview mPreview;
    private TextView tv;
    
    private GPSTracker gps;
    float latitude = 0f;
    float longitude = 0f;
    float preLatitude = 0.1f;
    float preLongitude = 0.1f;
    
    private float netWorkReceive = 0;
	private float netWorkTransmit = 0;
	private float currNetWorkReceive = 0;
	private float currNetWorkTransmit = 0;

    private SensorManager sManager;
    
    //accelerometer
    private Sensor accelerometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    //accelerometer
    
    final MyGLRenderer mRenderer = new MyGLRenderer(this);
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		glSurfaceView = new GLSurfaceView(this);
		
		gps = new GPSTracker(this);
		
		netWorkReceive = TrafficStats.getTotalRxBytes();
		netWorkTransmit = TrafficStats.getTotalTxBytes();
		
		//get a hook to the sensor service  
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        vib = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        
      //accelerometer
        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
      //accelerometer
		
		setContentView(R.layout.fragment_main);
		tv = (TextView) findViewById(R.id.tv);
		
		//create an instance of Camera
      	mCamera = getCameraInstance();
      //create our Preview view and set it as the content of our activity
      	mPreview = new CameraPreview(this, mCamera); 	
      	
      	FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
      	preview.addView(glSurfaceView);
   		preview.addView(mPreview);
   		
   		setCameraDisplayOrientation(this, 0, mCamera);
		
		//the next two lines is how to set the background transparent
		glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		
		// Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(this.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        
 //       final MyGLRenderer mRenderer = new MyGLRenderer(this);
        
        if (supportsEs2)
        {
        	// Request an OpenGL ES 2.0 compatible context.
        	glSurfaceView.setEGLContextClientVersion(2);

        	// Assign our renderer. 
        	glSurfaceView.setRenderer(mRenderer);
        	rendererSet = true;
        }
        else
        {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
        	Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
        	Toast.LENGTH_LONG).show();
            return;
        }
        
        glSurfaceView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {           
                    // Convert touch coordinates into normalized device
                    // coordinates, keeping in mind that Android's Y
                    // coordinates are inverted.
                	dragX = event.getX();
                	dragY = event.getY();
                	dragupdateX = olddragX - dragX;
                	dragupdateY = olddragY - dragY;
                	
                    normalizedX = 
                        (dragX / (float) v.getWidth()) * 2 - 1;
                    normalizedY = 
                        -((dragY / (float) v.getHeight()) * 2 - 1);
                    
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                            	mRenderer.handleTouchPress(
                                    normalizedX, normalizedY);
                            }
                        });
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    //	vib.vibrate(30);
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                            	mRenderer.handleTouchDrag(
                                    //normalizedX, normalizedY);
                            			dragupdateX*0.1f, -dragupdateY*0.1f);
                            }
                        });
                    } 
                    else if (event.getAction() == MotionEvent.ACTION_UP) {
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                            	mRenderer.handleTouchUp(
                                    normalizedX, normalizedY);
                            }
                        });
                    }
                    
                    olddragX = dragX;
                    olddragY = dragY;

                    return true;                    
                } else {
                    return false;
                }
            }
        });
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
	//		mRenderer.addSmurfs(latitude, longitude);
	//		mRenderer.addSprites();
			
			if(latitude != preLatitude || longitude != preLongitude){
				vib.vibrate(30);
				mRenderer.addSprites(latitude, longitude);
				preLatitude = latitude;
				preLongitude = longitude;
			}
			
			return true;
		}
		if (id == R.id.switch_mode) {
			vib.vibrate(30);
			mRenderer.handleSwitchMode();
		//	eliX++;
		//	eliY++;
		//	mRenderer.currentGPScoord(eliX, eliY);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//float eliX = 0f;
	//float eliY = 0f;
	
	@Override
	protected void onPause() {
	    super.onPause();
	    if (rendererSet) { 
	    	glSurfaceView.onPause();
	    } 
	    
	    releaseCamera();
	    
	  //unregister the sensor listener  
        sManager.unregisterListener(this);
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    if (rendererSet) { 
	    	glSurfaceView.onResume();
	    } 
	    
	    if (mCamera == null){
        	mCamera = getCameraInstance();
        	mPreview = new CameraPreview(this, mCamera);
        	FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        	//preview.removeAllViews();
        	preview.addView(mPreview);
        	preview.addView(glSurfaceView);
        	setCameraDisplayOrientation(this, 0, mCamera);
        }
	    
	    /*register the sensor listener to listen to the gyroscope sensor, use the 
        callbacks defined in this class, and gather the sensor information as quick 
        as possible*/  
        sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
        		SensorManager.SENSOR_DELAY_FASTEST);
        
      //accelerometer
        sManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
      //accelerometer
	}
	
	//get an instance of the Camera object
  	public static Camera getCameraInstance() {
  		Camera c = null;
  		try {
  			c = Camera.open(0); //try to get the back camera
  		}
  		catch(Exception e){
  			//Camera is not available
  		}
  		return c; //returns null if camera is unavailable
  	}
  	
  	 private void releaseCamera(){
 	    if (mCamera != null){
 	        mCamera.release();        // release the camera for other applications
 	        mCamera = null;
 	    }
 	}
  	
  	public static void setCameraDisplayOrientation(Activity activity,
            int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
  	
  	float normalizedX;
  	float normalizedY;
  	float dragX;
  	float olddragX = 0;
  	float dragupdateX = 0;
  	float dragY;
  	float olddragY = 0;
  	float dragupdateY = 0;
    float x;
    float y;
    float z;
    float YawX;
    float PitchY;
    float PitchZ;
    float YawZ;
    float RollX;
    float RollY;
    float radius = 1f;
    float eyeX = 0f;
	float eyeY = 0f;
	float eyeZ = 0f;
	float xAccel;
    float yAccel;
    float zAccel;
    float velocityX;
    float velocityY;
    float velocityZ;
    float motionX;
    float motionY;
    float motionZ;
	
	private float round(float f) {
        return (int)(f * 100f) / 100f;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		 //if sensor is unreliable, return void  
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)  
        {  
            return;  
        }  
        
      //accelerometer
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            mGravity = event.values.clone();
            // Shake detection
            xAccel = mGravity[0];
            yAccel = mGravity[1];
            zAccel = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(xAccel*xAccel + yAccel*yAccel + zAccel*zAccel);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
                // Make this higher or lower according to how much
                // motion you want to detect
            if(mAccel > 1){ 
            // do something
            //	vib.vibrate(30);

            	velocityX = velocityX + xAccel;
            	velocityY = velocityY + yAccel;
            	velocityZ = velocityZ + zAccel;
            	motionX = motionX + xAccel;
            	motionY = motionY + yAccel;
            	motionZ = motionZ + zAccel;
            }
        }
      //accelerometer
        
        //else it will output the Roll, Pitch and Yawn values   
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION){
        	x = event.values[2];
        	if(event.values[1] < 0){
        		y = - event.values[1];
        	}
        	else{
        		y = 180 + event.values[1];
        	}
        	z = event.values[0];
        }
        
       	YawX = (float) (eyeX + radius * Math.cos(z*(Math.PI/180)));
        YawZ = (float) (eyeZ + radius * Math.sin(z*(Math.PI/180)));
        	
       	PitchY = (float) -(eyeX + radius * Math.cos(y*(Math.PI/180)));
       	PitchZ = (float) (eyeX + radius * Math.sin(y*(Math.PI/180)));
       	
       	RollX = (float) -(eyeX + radius * Math.cos(x*(Math.PI/180)));
       	RollY = (float) (eyeX + radius * Math.sin(x*(Math.PI/180)));
  /*      
        tv.setText("netWorkReceive :" + Float.toString(currNetWorkReceive) +"\n"+
        			"netWorkTransmit :" + Float.toString(currNetWorkTransmit) +"\n"+
        			"GPS latitude :"+ Float.toString(latitude) +"\n"+
        			"GPS longitude :"+ Float.toString(longitude) +"\n"+
        		//   "drag x :"+ Float.toString(round(dragX)) +"\n"+
        		//   "drag y :"+ Float.toString(round(dragY)) +"\n"+
        		//   "accelerometer x :"+ Float.toString(round(motionX)) +"\n"+
        	    //   "accelerometer y :"+ Float.toString(round(motionY)) +"\n"+
        		//   "accelerometer z :"+ Float.toString(round(motionZ)) +"\n"+
        		//   "accelerometer mAccelCurrent :"+ Float.toString(round(mAccelCurrent)) +"\n"+
        		//   "Orientation SphereX :"+ Float.toString(round(YawX)) +"\n"+
        		//   "Orientation SphereY :"+ Float.toString(round(PitchY)) +"\n"+
        		//   "Orientation SphereZ :"+ Float.toString(round(YawZ)) +"\n"+
        			"Orientation X (Roll) :"+ Float.toString(round(x)) +"\n"+
        			"Orientation Y (Pitch) :"+ Float.toString(round(y)) +"\n"+
        			"Orientation Z (Yaw) :"+ Float.toString(round(z)));
 */
        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
            	//mRenderer.handleGyroscope(YawX, YawZ, PitchY, PitchZ, RollX, RollY);
            	//mRenderer.handleAccelerometer(-20, motionY *0.1f, -motionZ *0.1f);
            	mRenderer.handleGyroscope(YawX, YawZ, PitchY, PitchZ, x, z);
            	if(gps.canGetLocation()){
        			latitude = (float) (gps.getLatitude() * 1000000);
        			longitude = (float) (gps.getLongitude() * 1000000);
        		//add a puck whenever the location changed	
        			/*if(latitude != preLatitude || longitude != preLongitude){
        				mRenderer.addPucks(latitude, longitude);
        				preLatitude = latitude;
        				preLongitude = longitude;
        			}*/
        		}
            	mRenderer.currentGPScoord(latitude, longitude);
            	currNetWorkReceive = TrafficStats.getTotalRxBytes() - netWorkReceive;
            	currNetWorkTransmit = TrafficStats.getTotalTxBytes() - netWorkTransmit;
            }
        });
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {		
	}

	@Override
	public void onLocationChanged(Location location) {		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {		
	}

	@Override
	public void onProviderEnabled(String provider) {		
	}

	@Override
	public void onProviderDisabled(String provider) {		
	}

}
