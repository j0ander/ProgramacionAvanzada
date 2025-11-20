package com.programacionavanzada.primitivas;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//===========================================================================
// Clase para VARIOS PUNTOS
//===========================================================================
public class Points {

    // Variables que forman parte de cualquier primitiva
    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle, colorHandle;

    // Atributos que trabajan con los vertices
    static final int COORDS_POR_VERTEX = 3; // (x, y, z)

    // --- NUEVOS CAMPOS QUE PERMITEN MÚLTIPLES PUNTOS ---

    // ANTES: Las coordenadas eran FIJAS (static) - solo un punto
    // AHORA: Cada instancia puede tener sus propias coordenadas diferentes
    private final float[] pointCoord; // Las coordenadas serán específicas de cada instancia.

    // ANTES: Siempre era 1 vértice (vertexCount = 1)
    // AHORA: Se calcula dinámicamente según cuántos puntos tengamos
    private final int vertexCount; // El número de vértices se calcula a partir del arreglo.

    // El stride sigue igual pero ahora es más importante
    private final int vertexStride = COORDS_POR_VERTEX*4; // Número de bytes por vértice.

    float color[] = {0.0f, 0.0f, 0.0f, 1.0f}; // Color RGBA

    // Shaders (se asume que loadShader existe y GLES20 está importado)
    private final String vertexShaderCode = "attribute vec4 vPosition;" +
            "void main(){" +
            "gl_Position = vPosition;" +
            "gl_PointSize = 100.0;" + // Tamaño de punto razonable
            "}";
    private final String fragmentShaderCode = "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main(){" +
            "gl_FragColor = vColor;" +
            "}";

    // =============================================
    // CONSTRUCTOR MODIFICADO - LA CLAVE PARA MÚLTIPLES PUNTOS
    // =============================================
    /**
     * @param coords Arreglo de floats que contiene las coordenadas (x, y, z) de todos los puntos.
     *               Ejemplo: {x1, y1, z1, x2, y2, z2, ...} para DOS puntos
     *               Ejemplo: {x1, y1, z1, x2, y2, z2, x3, y3, z3} para TRES puntos
     */
    public Points(float[] coords) {
        // ASIGNACIÓN DINÁMICA: En lugar de coordenadas fijas, aceptamos cualquier arreglo
        this.pointCoord = coords;

        // CÁLCULO DINÁMICO: Calculamos cuántos puntos hay basándonos en el arreglo recibido
        // Si coords tiene 6 elementos → 6/3 = 2 puntos
        // Si coords tiene 9 elementos → 9/3 = 3 puntos
        this.vertexCount = this.pointCoord.length / COORDS_POR_VERTEX;


        // 1. Creación del buffer CON LAS NUEVAS COORDENADAS
        ByteBuffer buteBuffer = ByteBuffer.allocateDirect(pointCoord.length * 4);
        buteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = buteBuffer.asFloatBuffer();
        vertexBuffer.put(pointCoord); // Aquí ponemos TODAS las coordenadas de TODOS los puntos
        vertexBuffer.position(0);

        // 2. Cargar shaders (igual que antes)
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // 3. Crear programa OpenGL (igual que antes)
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }


    // =============================================
    // MÉTODO draw() MODIFICADO - DIBUJA MÚLTIPLES PUNTOS
    // =============================================
    public void draw() {
        GLES20.glUseProgram(mProgram);

        // 1. Obtener y activar el handle de posición (igual que antes)
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        // 2. Especificar cómo leer los datos del buffer (igual que antes)
        GLES20.glVertexAttribPointer(
                positionHandle,
                COORDS_POR_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
        );

        // 3. Establecer el color (igual que antes)
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // 4. DIBUJAR TODOS LOS PUNTOS - ¡ESTO ES LO MÁS IMPORTANTE!
        // ANTES: GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1); ← Solo 1 punto
        // AHORA: Usamos 'vertexCount' que puede ser 1, 2, 3, o más puntos
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexCount);

        // 5. Finalizar (igual que antes)
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    // El método loadShader() se mantiene igual
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    // Método auxiliar para establecer un color diferente
//    public void setColor(float r, float g, float b, float a) {
//        this.color[0] = r;
//        this.color[1] = g;
//        this.color[2] = b;
//        this.color[3] = a;
//    }
}
