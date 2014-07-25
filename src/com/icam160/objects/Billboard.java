package com.icam160.objects;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.icam160.util.Constants.BYTES_PER_FLOAT;

import com.icam160.programs.TextureShaderProgram;
import com.icam160.util.VertexArray;

public class Billboard {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT 
        + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    
    private static final float[] VERTEX_DATA = {
        // Order of coordinates: X, Y, S, T

        // Triangle Fan
           0f,    0f, 0.5f, 0.5f, 
        -0.5f, -1f,   0f, 1f,  
         0.5f, -1f,   1f, 1f, 
         0.5f,  1f,   1f, 0f, 
        -0.5f,  1f,   0f, 0f, 
        -0.5f, -1f,   0f, 1f };
    
    private final VertexArray vertexArray;
    
    private float positionX;
    private float positionY;
    private float positionZ;
    
    public Billboard(float posX, float posY, float posZ) {
        vertexArray = new VertexArray(VERTEX_DATA);
        positionX = posX;
        positionY = posY;
        positionZ = posZ;
    }
    
    public void bindData(TextureShaderProgram textureProgram) {
        vertexArray.setVertexAttribPointer(
            0, 
            textureProgram.getPositionAttributeLocation(), 
            POSITION_COMPONENT_COUNT,
            STRIDE);
        
        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT, 
            textureProgram.getTextureCoordinatesAttributeLocation(),
            TEXTURE_COORDINATES_COMPONENT_COUNT, 
            STRIDE);
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
    
    public void draw() {                                
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}