package com.programacionavanzada.primitivas;

import android.opengl.GLES20;

import com.programacionavanzada.MyGLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {

    private FloatBuffer vertexBuffer;

    private final int mProgram ;

    private int positionHandle,colorHandle;

    static final int COORDS_POR_VERTEX = 3;

    static float triangleCoord[] = {
            -0.5f, 0.75f, 0.0f,  //vert superior
            -0.75f, 0.25f, 0.0f, //vert inf izquierda
            -0.25f, 0.25f, 0.0f,  //vert inf derecho
    };

    private final  int vertexCount =triangleCoord.length/COORDS_POR_VERTEX;
    private final int vertexStride = COORDS_POR_VERTEX*4;

    float color[] = {0.0f, 0.0f, 0.0f, 1.0f};

    public Triangle(){

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoord.length*4);
        byteBuffer.order(ByteOrder.nativeOrder());

        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(triangleCoord);
        vertexBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    private final String vertexShaderCode =
            "attribute vec4 vPosition;"+
                    "void main(){" +
                    "gl_Position = vPosition;"+
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;"+
                    "uniform vec4 vColor;" +
                    "void main(){"+
                    "gl_FragColor = vColor;"+
                    "}";

    public void draw(){

        GLES20.glUseProgram(mProgram);
        //obtener los vertices
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, //Posicion
                COORDS_POR_VERTEX, //Coordenadas de los vertices
                GLES20.GL_FLOAT, //Que tipo de datos se usa
                false, //Si necesitar normalizar los datos
                vertexStride, //tamaño de memoria de cada uno de los vertices
                vertexBuffer //De donde tomarlos
        );
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        GLES20.glUniform4fv(colorHandle, 1,color, 0);

        // basico GL_TRIANGLES
        // GL_TRIANGLE_STRIP comparte bordes entre ellos
        //GL_TRIANGLE_FAN todos los triangulos comparten un vertice central

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }

}
