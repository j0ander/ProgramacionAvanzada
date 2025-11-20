package com.programacionavanzada.primitivas;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Line {
    private final FloatBuffer vertexBuffer;
    private final int mProgram;

    private int positionHandle, colorHandle;

    private final float lineCoord[] = {
         //   -0.85f, -0.85f,
          //  0.85f, 0.85f
            -0.5f, -0.25f,
           -0.75f, -0.75f
    };
    float color[] = {0.0f,1.0f,0.0f,1.0f};

    public Line(){
        //reserva de memoria para que la gpu lo lea directo + rápido
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(lineCoord.length*4);
        //que la ejecución utilice el orden propuesto
        byteBuffer.order(ByteOrder.nativeOrder());

        vertexBuffer = byteBuffer.asFloatBuffer();
        //copiar las coordenadas hacia el buffer que va al gpu
        vertexBuffer.put(lineCoord);
        //situar el cursor en el centro
        vertexBuffer.position(0);

        //Se encarga del primer shader
        //Se repite en todas las primitivas
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        //Crear el programa vacio en la GPU
        //Es un contenedor que une los dos shaders, fragment y vertex
        mProgram = GLES20.glCreateProgram();
        //Agrega los dos shaders al programa
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        //Revisar que los shaders sean compatibles
        GLES20.glLinkProgram(mProgram);

    }
    private int loadShader(int type, String shaderCode){
        //type puede ser vertex o fragment, a shader se le da un id
        int shader = GLES20.glCreateShader(type);
        //shaderCode en string interpretado por GLES20
        GLES20.glShaderSource(shader, shaderCode);
        //Implementar completamente el shader
        GLES20.glCompileShader(shader);
        return shader;
    }
    private final String vertexShaderCode =
            "attribute vec4 vPosition;"+
                    "void main(){" +
                    "gl_Position = vPosition;"+
                    "}"; //A cada coordenada de pantalla se asocia con una real

    //La presicion da informacion precisa del grafico
    private final String fragmentShaderCode =
            "precision mediump float;"+
                    "uniform vec4 vColor;" +
                    "void main(){"+
                    "gl_FragColor = vColor;"+
                    "}";

    public void draw(){

        GLES20.glUseProgram(mProgram);

        positionHandle = GLES20.glGetAttribLocation(mProgram,"vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(
                positionHandle,
                2,
                GLES20.GL_FLOAT,
                false,
                0,
                vertexBuffer
        );

        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1,color, 0);

        GLES20.glLineWidth(10);

        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);

        GLES20.glDisableVertexAttribArray(positionHandle);




    }
}
