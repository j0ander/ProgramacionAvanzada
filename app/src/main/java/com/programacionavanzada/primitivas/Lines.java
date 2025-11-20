package com.programacionavanzada.primitivas;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Lines{

    private final FloatBuffer vertexBuffer;
    private final int mProgram;

    private int positionHandle, colorHandle;

    // --- CAMBIO IMPORTANTE 1: Ahora trabajamos en 2D en lugar de 3D ---
    // Para líneas simples, solo necesitamos coordenadas (x, y) - más eficiente
    static final int COORDS_POR_VERTEX = 2; // 2D: (x, y)

    // --- CAMPOS QUE PERMITEN MÚLTIPLES LÍNEAS ---
    private final int vertexCount;    // Número total de vértices (se calcula dinámicamente)
    private final int vertexStride;   // Bytes entre cada vértice
    private final float[] pointCoord; // Coordenadas ESPECÍFICAS para cada conjunto de líneas

    float color[] = {0.0f, 0.0f, 0.0f, 1.0f}; // Color negro por defecto

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main(){" +
                    "   gl_Position = vPosition;" + // Posición en 2D
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main(){" +
                    "   gl_FragColor = vColor;" + // Color de la línea
                    "}";

    // =============================================
    // CONSTRUCTOR MEJORADO - ACEPTA MÚLTIPLES LÍNEAS
    // =============================================
    /**
     * @param coords Arreglo de floats que contiene pares de coordenadas (x, y) para las líneas
     *               FORMATO: Cada línea necesita 2 vértices (4 coordenadas)
     *               Ejemplo 1 línea: {x1, y1, x2, y2}
     *               Ejemplo 2 líneas: {x1,y1, x2,y2, x3,y3, x4,y4}
     */
    public Lines(float[] coords) {
        // GUARDAMOS las coordenadas específicas para ESTE conjunto de líneas
        this.pointCoord = coords;

        // CÁLCULO DINÁMICO:
        // coords.length = número total de coordenadas
        // COORDS_POR_VERTEX = 2 (cada vértice tiene x,y)
        // vertexCount = número total de VÉRTICES (no líneas)
        this.vertexCount = coords.length / COORDS_POR_VERTEX;

        // Cada vértice ocupa: 2 coordenadas × 4 bytes = 8 bytes
        this.vertexStride = COORDS_POR_VERTEX * 4;

        // PREPARAR el buffer de vértices con TODAS las coordenadas
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(coords);  // Copiamos TODAS las coordenadas de TODAS las líneas
        vertexBuffer.position(0);  // Preparamos para leer desde el inicio

        // COMPILACIÓN de shaders (igual que antes)
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // CREACIÓN del programa OpenGL
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    // Método para compilar shaders (sin cambios)
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    // =============================================
    // MÉTODO draw() - DIBUJA MÚLTIPLES LÍNEAS
    // =============================================
    public void draw() {
        // ACTIVAR nuestro programa de shaders
        GLES20.glUseProgram(mProgram);

        // OBTENER la ubicación de la variable de posición en el shader
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // ACTIVAR el uso del arreglo de vértices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // EXPLICAR cómo OpenGL debe leer los datos de vértices:
        GLES20.glVertexAttribPointer(
                positionHandle,      // Dónde guardar las posiciones
                COORDS_POR_VERTEX,   // 2 coordenadas por vértice (x,y)
                GLES20.GL_FLOAT,     // Tipo de datos: números decimales
                false,               // No normalizar
                vertexStride,        // 8 bytes entre cada vértice
                vertexBuffer         // Buffer con TODOS los vértices
        );

        // CONFIGURAR el color de la línea
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // --- CARACTERÍSTICA ESPECIAL PARA LÍNEAS ---
        // Definir el GROSOR de todas las líneas (10 píxeles de ancho)
        GLES20.glLineWidth(10);

        // --- DIBUJADO DE MÚLTIPLES LÍNEAS - LA PARTE MÁS IMPORTANTE ---
        // GL_LINES: modo de dibujo para líneas
        // 0: empezar desde el primer vértice
        // vertexCount: dibujar TODOS los vértices que tenemos
        //
        // ¿CÓMO FUNCIONA? OpenGL toma los vértices de 2 en 2 para formar líneas:
        // - Vértices 0-1: forman la primera línea
        // - Vértices 2-3: forman la segunda línea
        // - Vértices 4-5: forman la tercera línea
        // ¡Y así sucesivamente!
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount);

        // LIMPIEZA: desactivar el arreglo de vértices
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    // =============================================
    // MÉTODO PARA CAMBIAR EL COLOR
    // =============================================
//    public void setColor(float red, float green, float blue, float alpha) {
//        this.color[0] = red;    // Componente rojo (0.0 a 1.0)
//        this.color[1] = green;  // Componente verde (0.0 a 1.0)
//        this.color[2] = blue;   // Componente azul (0.0 a 1.0)
//        this.color[3] = alpha;  // Transparencia (0.0 transparente, 1.0 opaco)
//    }
}
