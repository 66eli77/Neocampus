package com.icam160.util;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.icam160.util.Constants.BYTES_PER_FLOAT;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


public class SpriteVertexArray {
	private FloatBuffer vertexFloatBuffer;
    private final FloatBuffer textureCoordinatesFloatBuffer;
    
    public SpriteVertexArray(float[] vertexData, float[] textureData) {
    	vertexFloatBuffer = ByteBuffer
            .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData);
    	
    	textureCoordinatesFloatBuffer = ByteBuffer
                .allocateDirect(textureData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
    }
        
    public void setVertexAttribPointer(int dataOffset, int V_attributeLocation,
            int vertexComponentCount, int vertexStride, int T_attributeLocation,
            int textureComponentCount, int textureStride) {        
        	vertexFloatBuffer.position(dataOffset);        
            glVertexAttribPointer(V_attributeLocation, vertexComponentCount,
                GL_FLOAT, false, vertexStride, vertexFloatBuffer);
            glEnableVertexAttribArray(V_attributeLocation);
            vertexFloatBuffer.position(0);
            
            textureCoordinatesFloatBuffer.position(dataOffset);	
            glVertexAttribPointer(T_attributeLocation, textureComponentCount,
                    GL_FLOAT, false, textureStride, textureCoordinatesFloatBuffer);
            glEnableVertexAttribArray(T_attributeLocation);
            textureCoordinatesFloatBuffer.position(0);
        }

}
