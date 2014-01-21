package com.ICAM160.android.neocampus;

import com.example.neocampus.R;

import android.app.Activity;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

    private GLSurfaceView mGLView;
    //Eli
    private Camera mCamera;
    private CameraPreview mPreview;
    
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        mGLView = new MyGLSurfaceView(this);
        
        //Eli
        setContentView(R.layout.main);
        
      //Eli
        //create an instance of Camera
      	mCamera = getCameraInstance();
      //create our Preview view and set it as the content of our activity
      	mPreview = new CameraPreview(this, mCamera);
      	FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
      	preview.addView(mGLView);
   		preview.addView(mPreview);
   		setCameraDisplayOrientation(this, 0, mCamera);
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

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
        releaseCamera();
    }
    
    private void releaseCamera(){
	    if (mCamera != null){
	        mCamera.release();        // release the camera for other applications
	        mCamera = null;
	    }
	}

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        
        if (mCamera == null){
        	mGLView.onResume();
         	((FrameLayout)mGLView.getParent()).removeAllViews();
        	setContentView(R.layout.main);
        	mCamera = getCameraInstance();
        	mPreview = new CameraPreview(this, mCamera);
        	FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        	//preview.removeAllViews();
        	preview.addView(mPreview);
        	preview.addView(mGLView);
        	setCameraDisplayOrientation(this, 0, mCamera);
        }
    }
}
