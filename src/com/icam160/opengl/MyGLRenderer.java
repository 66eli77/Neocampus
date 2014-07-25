package com.icam160.opengl;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.icam160.R;
import com.icam160.objects.Billboard;
import com.icam160.programs.TextureShaderProgram;
import com.icam160.util.MatrixHelper;
import com.icam160.util.TextureHelper;
import com.icam160.objects.SpriteAnimation4x4;
import com.icam160.programs.SpriteShaderProgram;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Vibrator;
import android.widget.Toast;

public class MyGLRenderer implements Renderer {	
	private final Context context;
	Activity activity;
	Vibrator vib;
	
	private final float[] projectionMatrix = new float[16];
	private final float[] modelMatrix = new float[16];
	private final float[] viewMatrixYaw = new float[16];
	private final float[] viewMatrixPitch = new float[16];
	private final float[] viewProjectionMatrix = new float[16]; 
	private final float[] modelViewProjectionMatrix = new float[16];
	private final float[] viewMatrix1 = new float[16];
	private final float[] textureMatrix = new float[16];

	private SpriteAnimation4x4 spriteAnimationGirl;
	private SpriteAnimation4x4 spriteAnimationGirl_1;
	private ArrayList<SpriteAnimation4x4> spriteAnimations = 
			new ArrayList<SpriteAnimation4x4>();
	private Billboard fuckedUP;
	
	private SpriteShaderProgram spriteProgram;
	private TextureShaderProgram textureProgram;
	private int spriteTexture;
		
	float eyeX = 0f;
	float eyeY = 0f;
	float eyeZ = 0f;
	
	float dragY = 0f;
	float dragX = 0f;
	
	float YawCenterX;
	float PitchCenterY;
	float YawCenterZ;
	float PitchCenterZ;
	float RollUpX;
	float zGyroscope;
		
	public MyGLRenderer(Context context) { 
    	this.context = context; 
    	activity = (Activity) context;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {	
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		glEnable(GL_DEPTH_TEST);
		// enable face culling feature
		//gl.glEnable(GL10.GL_CULL_FACE);
		// specify which faces to not draw
		//gl.glCullFace(GL10.GL_BACK);
		
		textureProgram = new TextureShaderProgram(context);
		spriteProgram = new SpriteShaderProgram(context);
		
		spriteTexture = TextureHelper.loadTexture(context, R.drawable.gorrila);
		
		spriteAnimationGirl = new SpriteAnimation4x4();
		spriteAnimationGirl_1 = new SpriteAnimation4x4();
		
		fuckedUP = new Billboard(0,0,0);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
// "100000f" means you wouldn’t see the models that are 8 kilometer away.
		MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height,
				0f, 100000f);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		setLookAtM(viewMatrixYaw, 0, eyeX, eyeY, eyeZ, 
				YawCenterX, 0, YawCenterZ, 0f, 1f, 0f);
		setLookAtM(viewMatrixPitch, 0, eyeX, eyeY, eyeZ, 
				0, PitchCenterY, PitchCenterZ, 0, 1f, 0f);
		
		setIdentityM(viewMatrix1, 0);
		multiplyMM(viewMatrix1, 0, viewMatrixPitch, 0, viewMatrixYaw, 0);
		rotateM(viewMatrix1, 0, -RollUpX, YawCenterX, 0f, YawCenterZ); 
		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix1, 0);
		
		ArrayList<SpriteAnimation4x4> tempArraySprites = 
				(ArrayList<SpriteAnimation4x4>) spriteAnimations.clone();
		        
		if(FrameCounter >= 16){
			drawSpriteAnimationGirl(0f , 0f, 3f);
		}
        fuckedUP.bindData(textureProgram);
      //  drawSpriteAnimationFrames(1,1,11);
        

      // draw individual sprite animation frames
       	for(SpriteAnimation4x4 s : tempArraySprites){
       		System.out.println("eli yeah draw !");
       		drawSpriteAnimationFrames((s.getPosX() - currLatitude + 1f), 0f, 
      				(s.getPosZ() - currLongitude + 1f), s.getTextureHorizontal(),
       				s.getTextureVertical());
       	}
        
        if(SwitchMode){
        	for(SpriteAnimation4x4 s : tempArraySprites){
        		if(s.getPosX() - currLatitude < 80 && s.getPosZ() - currLongitude < 80){
        			spriteAnimations.remove(s);
        			FrameCounter++;
        			
        			activity.runOnUiThread(new Runnable() {
                	    @Override
                	    public void run() {
                	    	vib = (Vibrator) activity.getSystemService(context.VIBRATOR_SERVICE);
                	    	vib.vibrate(30);
                	    }
                	});
        		}
        	}
        }
  	}
	
	private float spinning = 0f;
	private float[] texture_uv_coordinates_vertical_1 = new float[16];
    private float[] texture_uv_coordinates_horizontal_1 = new float[16];
    private void drawSpriteAnimationFrames(float x, float y, float z, 
    		float textureHorizontal, float textureVertical){
    	setIdentityM(modelMatrix, 0);
    	translateM(modelMatrix, 0, x, y, z);
    	//make sure the picture always facing you
    	rotateM(modelMatrix, 0, 270f - zGyroscope, 0f, 1f, 0f); 
    	scaleM(modelMatrix, 0, 8f, 8f, 1f);
    	
    	
    		texture_uv_coordinates_horizontal_1[1] = textureHorizontal;
    		texture_uv_coordinates_vertical_1[1] = textureVertical;
    		
    	setIdentityM(textureMatrix, 0);
    	translateM(textureMatrix, 0, texture_uv_coordinates_horizontal_1[1], 
    			texture_uv_coordinates_vertical_1[1], 0f); 
    	
    	multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    	    	
    	spriteProgram.useProgram();
    	spriteProgram.setUniforms(modelViewProjectionMatrix, textureMatrix, spriteTexture);
    	spriteAnimationGirl_1.bindData(spriteProgram);
    	spriteAnimationGirl_1.draw();
    }
    private float spinningg = 0f;
	private float[] texture_uv_coordinates_vertical = new float[16];
    private float[] texture_uv_coordinates_horizontal = new float[16];
    private int tickCounterForSpeed = 0;
    private int tickCounter = 0;
    private float running = 0f;
    private void drawSpriteAnimationGirl(float x, float y, float z){
    	setIdentityM(modelMatrix, 0);
    	translateM(modelMatrix, 0, x, y, z);
    //	scaleM(modelMatrix, 0, 4f, 4f, 1f); 
    	//make sure the picture always facing you
    	//rotateM(modelMatrix, 0, zGyroscope, 0f, 1f, 0f);  
    //	rotateM(modelMatrix, 0, spinningg, 0f, 1f, 0f);
    //	spinningg++;
    	
    	tickCounterForSpeed --;
    	if(tickCounterForSpeed < 0){
    		running += 0.5;   // define the running speed for girl !!
    		tickCounterForSpeed = 1; 
    	}
    	tickCounter --;
    	if(tickCounter < 0){
    		texture_uv_coordinates_horizontal[1] += 0.25f;
    		if (texture_uv_coordinates_horizontal[1] >= 1f){
    			texture_uv_coordinates_horizontal[1] = 0f;
    			texture_uv_coordinates_vertical[1] += 0.25f;
    			if (texture_uv_coordinates_vertical[1] >= 1f)
    				texture_uv_coordinates_vertical[1] = 0f;
    		}
    		tickCounter = 2;  //define the frame rate of the sprite animation !!
    	}
    	setIdentityM(textureMatrix, 0);
    	translateM(textureMatrix, 0, texture_uv_coordinates_horizontal[1], 
    			texture_uv_coordinates_vertical[1], 0f); 
    	
    	multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    	    	        
    	spriteProgram.useProgram();
    	spriteProgram.setUniforms(modelViewProjectionMatrix, textureMatrix, spriteTexture);
    	spriteAnimationGirl.bindData(spriteProgram);
    	spriteAnimationGirl.draw();
    }
	
	public void handleTouchPress(float normalizedX, float normalizedY) {
		System.out.println("eli down");
	}
	public void handleTouchDrag(float normalizedX, float normalizedY) { 
		System.out.println("eli drag");
		dragX += normalizedX;
		dragY += normalizedY;
	}
	
	public void handleTouchUp(float normalizedX, float normalizedY) { 
		System.out.println("eli up");
	}

	/*public void handleGyroscope(float YawX, float YawZ, 
			float PitchY, float PitchZ, float RollX, float RollY) { 	
		
		YawCenterX = YawX;
		YawCenterZ = YawZ;
		PitchCenterY = PitchY;
		PitchCenterZ = PitchZ;
		RollUpX = RollX;
		RollUpY = RollY;
	}*/
	
	private float height = 1f;  // set the height for all models
	private float currLatitude;	
	private float currLongitude;
	
/*
	public void addPucks(float GPSaltitude, float GPSlongitude){
		pucks.add(new Puck(4f, 28f, 32, GPSaltitude, height, GPSlongitude));
	}*/
	private float spriteX = 0f;
	private float spriteY = -0.5f;
	private float spriteZ = 10f;
	private float frameUpdateHorizontal = 0f;
	private float frameUpdateVertical = 0f;
	private int FrameCounter = 0;
	public void addSprites(){
		spriteX ++;
		//spriteY ++;
		spriteZ --;
		FrameCounter ++;

		spriteAnimations.add(new SpriteAnimation4x4(spriteX, spriteY, spriteZ,
				frameUpdateHorizontal, frameUpdateVertical));
		
		//update the frames
				frameUpdateHorizontal += 0.25f;
				if (frameUpdateHorizontal >= 1f){
					frameUpdateHorizontal = 0f;
					frameUpdateVertical += 0.25f;
					if (frameUpdateVertical >= 1f)
						frameUpdateVertical = 0f;
				}
	}
	
	public void addSprites(float GPSaltitude, float GPSlongitude){
		FrameCounter ++;

		spriteAnimations.add(new SpriteAnimation4x4(GPSaltitude, spriteY, GPSlongitude,
				frameUpdateHorizontal, frameUpdateVertical));
		
		//update the frames
				frameUpdateHorizontal += 0.25f;
				if (frameUpdateHorizontal >= 1f){
					frameUpdateHorizontal = 0f;
					frameUpdateVertical += 0.25f;
					if (frameUpdateVertical >= 1f)
						frameUpdateVertical = 0f;
				}
	}
	
	// update the current GPS coordinates
	public void currentGPScoord(float GPSaltitude, float GPSlongitude){
		currLatitude = GPSaltitude;
		currLongitude = GPSlongitude;
	}

	public void handleGyroscope(float YawX, float YawZ, 
			float PitchY, float PitchZ, float GyroscopeX, float GyroscopeZ) { 	
		
		YawCenterX = YawX;
		YawCenterZ = YawZ;
		PitchCenterY = PitchY;
		PitchCenterZ = PitchZ;
		RollUpX = GyroscopeX;
		zGyroscope = GyroscopeZ;
	}
	
	private boolean SwitchMode = false;
	public void handleSwitchMode(){
		System.out.println("eli switched !");
		SwitchMode = true;
	}
}
