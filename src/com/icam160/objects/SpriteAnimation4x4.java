package com.icam160.objects;

import static android.opengl.GLES20.*;
import static com.icam160.util.Constants.BYTES_PER_FLOAT;

import com.icam160.util.SpriteVertexArray;
import com.icam160.programs.SpriteShaderProgram;

public class SpriteAnimation4x4 {
//	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int V_STRIDE = POSITION_COMPONENT_COUNT * BYTES_PER_FLOAT;
    private static final int T_STRIDE = TEXTURE_COORDINATES_COMPONENT_COUNT * BYTES_PER_FLOAT;
    
    private static final float[] VERTEX_DATA = {
        // Order of coordinates: X, Y
    	  /* 0f,    0f,  
          -0.5f, -1f,   
           0.5f, -1f,   
           0.5f,  1f,   
          -0.5f,  1f,   
          -0.5f, -1f };*/
    	0f,    0f,  0f, 
        -1f, -1f, 0f,  
         1f, -1f,   0f,
         1f,  1f,  0f, 
        -1f,  1f,  0f, 
        -1f, -1f, 0f};
    
    private static final float[] TEXTURE_DATA = {
        // Order of coordinates: S, T
    	
    	/*0.125f, 0.125f,
    	0f, 0.25f,  
    	0.25f, 0.25f, 
    	0.25f, 0f, 
    	0f, 0f, 
    	0f, 0.25f }; this one upside down*/
    
    	0.125f, 0.125f,
    	0.25f, 0f,
    	0f, 0f,
    	0f, 0.25f, 
    	0.25f, 0.25f, 
    	0.25f, 0f};
    
    private SpriteVertexArray spriteVertexArray;
    
    public SpriteAnimation4x4() {
    	spriteVertexArray = new SpriteVertexArray(VERTEX_DATA, TEXTURE_DATA);
    }
    
    private float positionX;
    private float positionY;
    private float positionZ;
    private float texture_uv_horizontal;
    private float texture_uv_vertical;
    public SpriteAnimation4x4(float posX, float posY, float posZ, 
    		float textureHorizontal, float textureVertical) {
    	spriteVertexArray = new SpriteVertexArray(VERTEX_DATA, TEXTURE_DATA);
    	positionX = posX;
        positionY = posY;
        positionZ = posZ;
        texture_uv_horizontal = textureHorizontal;
        texture_uv_vertical = textureVertical;
    }
    
    public float getPosX(){
    	return positionX;
    }
    public float getPosY(){
    	return positionY;
    }
    public float getPosZ(){
    	return positionZ;
    }
    public float getTextureHorizontal(){
    	return texture_uv_horizontal;
    }
    public float getTextureVertical(){
    	return texture_uv_vertical;
    }
    
    public void bindData(SpriteShaderProgram spriteProgram) {
    	spriteVertexArray.setVertexAttribPointer(
            0, 
            spriteProgram.getPositionAttributeLocation(), 
            POSITION_COMPONENT_COUNT,
            V_STRIDE,
            spriteProgram.getTextureCoordinatesAttributeLocation(),
            TEXTURE_COORDINATES_COMPONENT_COUNT,
            T_STRIDE
            ); 
    }
        
    public void draw() {                                
        //glDrawArrays(GL_TRIANGLES, 0, 6);
    	glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }    
}
