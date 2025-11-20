package com.programacionavanzada.primitivas;

import android.opengl.GLES20;
import com.programacionavanzada.MyGLRenderer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangles{

    private FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle, colorHandle;

    static final int COORDS_POR_VERTEX = 3; // Cada vértice tiene (x, y, z)

    // --- CAMPOS MEJORADOS PARA MÚLTIPLES TRIÁNGULOS ---
    private final float[] triangleCoords;  // Ahora es dinámico, no estático
    private final int vertexCount;         // Número total de vértices
    private final int vertexStride;        // Bytes entre cada vértice

    float color[] = {0.0f, 0.0f, 0.0f, 1.0f}; // Color negro por defecto

    // =============================================
    // CONSTRUCTOR MEJORADO - ACEPTA MÚLTIPLES TRIÁNGULOS
    // =============================================
    /**
     * @param coords Arreglo de floats con coordenadas para uno o más triángulos
     *               FORMATO: Cada triángulo necesita 3 vértices (9 coordenadas)
     *
     *               Ejemplo 1 triángulo: {x1,y1,z1, x2,y2,z2, x3,y3,z3}
     *
     *               Ejemplo 2 triángulos:
     *               {x1,y1,z1, x2,y2,z2, x3,y3,z3,  // Triángulo 1
     *                x4,y4,z4, x5,y5,z5, x6,y6,z6}  // Triángulo 2
     *
     *               Ejemplo 3 triángulos: 27 coordenadas (3×9)
     */
    public Triangles(float[] coords) {
        // GUARDAMOS las coordenadas específicas para ESTOS triángulos
        this.triangleCoords = coords;

        // CÁLCULO DINÁMICO del número de vértices:
        // coords.length = total de coordenadas
        // ÷ 3 coordenadas por vértice = número total de vértices
        this.vertexCount = coords.length / COORDS_POR_VERTEX;

        // Cada vértice ocupa: 3 coordenadas × 4 bytes = 12 bytes
        this.vertexStride = COORDS_POR_VERTEX * 4;

        // PREPARAR el buffer de vértices con TODAS las coordenadas
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(coords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(coords);  // Copiamos TODAS las coordenadas de TODOS los triángulos
        vertexBuffer.position(0);  // Preparamos para leer desde el inicio

        // COMPILACIÓN de shaders usando el método de MyGLRenderer
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // CREACIÓN del programa OpenGL
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    // Shaders (sin cambios - funcionan para cualquier cantidad de triángulos)
    private final String vertexShaderCode =
            "attribute vec4 vPosition;"+
                    "void main(){" +
                    "gl_Position = vPosition;"+ // Transforma la posición del vértice
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;"+
                    "uniform vec4 vColor;" +
                    "void main(){"+
                    "gl_FragColor = vColor;"+ // Color de cada fragmento (píxel)
                    "}";

    // =============================================
    // MÉTODO draw() - DIBUJA MÚLTIPLES TRIÁNGULOS
    // =============================================
    public void draw(){
        // ACTIVAR nuestro programa de shaders
        GLES20.glUseProgram(mProgram);

        // OBTENER la ubicación de la variable de posición en el shader
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // ACTIVAR el uso del arreglo de vértices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // EXPLICAR cómo OpenGL debe leer los datos de vértices:
        GLES20.glVertexAttribPointer(
                positionHandle,      // Dónde guardar las posiciones
                COORDS_POR_VERTEX,   // 3 coordenadas por vértice (x,y,z)
                GLES20.GL_FLOAT,     // Tipo de datos: números decimales
                false,               // No normalizar los valores
                vertexStride,        // 12 bytes entre cada vértice
                vertexBuffer         // Buffer con TODOS los vértices
        );

        // CONFIGURAR el color de todos los triángulos
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // --- DIBUJADO DE MÚLTIPLES TRIÁNGULOS - LA PARTE MÁS IMPORTANTE ---
        // GL_TRIANGLES: modo de dibujo para triángulos INDEPENDIENTES
        // 0: empezar desde el primer vértice
        // vertexCount: dibujar TODOS los vértices que tenemos
        //
        // ¿CÓMO FUNCIONA? OpenGL toma los vértices de 3 en 3 para formar triángulos:
        // - Vértices 0-1-2: forman el primer triángulo
        // - Vértices 3-4-5: forman el segundo triángulo
        // - Vértices 6-7-8: forman el tercer triángulo
        // ¡Y así sucesivamente!
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // LIMPIEZA: desactivar el arreglo de vértices
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

}
