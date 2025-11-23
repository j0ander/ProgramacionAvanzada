package com.programacionavanzada.primitivas;

import static com.programacionavanzada.MyGLRenderer.loadShader;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square {

    private final FloatBuffer vertexBuffer;

    private final int mProgram;

    private int positionHandle;
    private int colorHandle;

    static final int COORD_POR_VERTEX = 3;


    private final int vertexCount = squareCoord.length / COORD_POR_VERTEX;

    private final int vertexStride = COORD_POR_VERTEX * 4;

    float color[] = {0.00f, 0.25f, 0.25f, 1.0f};
    static float squareCoord[] = {
            // (0) sup izquierdo
            0.25f, 0.75f, 0.0f,
            // (1) inf izquierdo
            0.25f, 0.0f,0.0f,
            // (2) inf derecho
            1.00f, 0.0f, 0.0f,
            // (3) sup derecho
            1.00f, 0.75f, 0.0f

    };

    //especifico el orden de los triangulos para formar el cuadrado
        // 0 1 2 luego 0 2 3
    private final short drawOrder[] = {
            0,1,2, //primer triangulo
            0,2,3  //segundo
    };

    private final ShortBuffer shortBuffer;

    public Square(){

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(squareCoord.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(squareCoord);
        vertexBuffer.position(0);

        //para el shortBuffer

        ByteBuffer sb =ByteBuffer.allocateDirect(drawOrder.length*4);
        sb.order(ByteOrder.nativeOrder());
        shortBuffer = sb.asShortBuffer();
        shortBuffer.put(drawOrder);
        shortBuffer.position(0);


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
         // atributos
        // 1) primitiva que voy a dibujar
        // 2) numero de indices que se van a dibujar
        // 3) el tipo de dato
        // 4)
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT,
                shortBuffer
        );

        GLES20.glDisableVertexAttribArray(positionHandle);



    }



}
