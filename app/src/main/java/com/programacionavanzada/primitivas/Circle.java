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
    float color[] = {0.9f, 0.8f, 0.0f, 1.0f};
    private final int vertexStride = COORD_POR_VERTEX * 4;
    private final int vertexCount;
    private float circleCoords[];

    public Circle(float radius, int numPoints) {

        circleCoords = createCircleCoords(radius,numPoints);
        vertexCount = circleCoords.length/COORD_POR_VERTEX;

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

        double angle = 2.0 * Math.PI / numPoints; // calculo del angulo dependiendo del numero de puntos

        for (int i = 0; i <= numPoints; i++) {
            double angle2 = angle * i;
            coords.add((float) (radius * Math.cos(angle2)));
            coords.add((float) (radius * Math.sin(angle2)));
            coords.add(0.0f);

        }

        float[] arrayV = new float[coords.size()];
        for(int i = 0; i<coords.size(); i++){
            arrayV[i] = coords.get(i);
        }

        return arrayV;
    }

    public void draw(){
        GLES20.glUseProgram(mProgram);

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(
                positionHandle, //GUARDA LOS DATOS DE LA VARIABLE 'vPosition'
                COORD_POR_VERTEX, //CADA PUNTO TIENE 3 COORDENADAS (XYZ)
                GLES20.GL_FLOAT, //Los datos son numeros decimales (floats)
                false, //No normalizamos los numeros
                vertexStride, //Cada punto ocupa 12 bytes en la memoria
                vertexBuffer //Lee los datos de este contenedor de memoria
        );

        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);

        GLES20.glDisableVertexAttribArray(positionHandle);

    }
}
