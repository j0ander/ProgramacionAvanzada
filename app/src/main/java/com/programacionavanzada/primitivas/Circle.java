package com.programacionavanzada.primitivas;

import static com.programacionavanzada.MyGLRenderer.loadShader;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class Circle {

    private final FloatBuffer vertexBuffer;

    private final int mProgram;

    private int positionHandle;
    private int colorHandle;

    static final int COORD_POR_VERTEX = 3;
    float color[] = {0.00f, 0.25f, 0.25f, 1.0f};
    private final int vertexStride = COORD_POR_VERTEX * 4;
    private final int vertexCount;
    private float circleCoords[];

    public Circle(float radius, int numPoints) {

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(circleCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(circleCoords);
        vertexBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

    }

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main(){" +
                    "gl_Position = vPosition;" +
                    "gl_PointSize = 50.0;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main(){" +
                    "gl_FragColor = vColor;" +
                    "}";

    private float[] createCircleCoords(float radius, int numPoints) {

        List<Float> coords = new ArrayList<>();
        coords.add(0.0f); //para el centro en x
        coords.add(0.0f); //para el centro en y
        coords.add(0.0f); //para el centro en z

        double angle = 2.0*Math.PI/numPoints; // calculo del angulo dependiendo del numero de puntos

        for(int i = 1; i <= numPoints; i++){
            double angle2 = angle*i;
            coords.add((float)(radius*Math.cos(angle2)));
            coords.add((float)(radius*Math.sin(angle2)));
            coords.add(0.0f);

        }

        return coords;
    }


}
