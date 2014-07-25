package com.icam160.objects;

import java.util.List;

import com.icam160.util.VertexArray;
import com.icam160.objects.ObjectBuilder.DrawCommand;
import com.icam160.objects.ObjectBuilder.GeneratedData;
import com.icam160.programs.ColorShaderProgram;
import com.icam160.util.Geometry.Cylinder;
import com.icam160.util.Geometry.Point;

public class Puck {
    private static final int POSITION_COMPONENT_COUNT = 3;

    public final float radius, height;
    
    private float positionX = 0;
    private float positionY = 0;
    private float positionZ = 0;

    private final VertexArray vertexArray;
    private final List<DrawCommand> drawList;

    public Puck(float radius, float height, int numPointsAroundPuck) {
        GeneratedData generatedData = ObjectBuilder.createPuck(new Cylinder(
            new Point(0f, 0f, 0f), radius, height), numPointsAroundPuck);
        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }
    
    // another constructor with GPS coordinates specified
    public Puck(float radius, float height, int numPointsAroundPuck, 
    		float posX, float posY, float posZ) {
        GeneratedData generatedData = ObjectBuilder.createPuck(new Cylinder(
            new Point(0f, 0f, 0f), radius, height), numPointsAroundPuck);
        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
        
        positionX = posX;
        positionY = posY;
        positionZ = posZ;
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

    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(0,
            colorProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT, 0);
    }
    public void draw() {
        for (DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}