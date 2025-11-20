package com.programacionavanzada.primitivas;
//Importamos las herramientas de Opengl Es 2.0 para graficos en Android

import android.opengl.GLES20;

//Importamos herramientas para manejar datos en memoria de forma eficiente
import java.nio.ByteBuffer; //Para crear contenedores de bytes
import java.nio.ByteBuffer; //Para asegurar el orden correcto de los bytes
import java.nio.ByteOrder;
import java.nio.FloatBuffer; //Para manejar numeros decimales(floats) en memoria

public class Point {
    //==================================
    //VARIABLES PRINCIPALES DE LA CLASE
    //==================================

    //Este buffer guarda las coordenadas de nuesto punto en la memoria
    //de forma que tarjeta grafica (GPU) pueda leerlas directamente
    private final FloatBuffer vertexBuffer;

    //Este numero indentifica nuestro "programa de graficos" en la GPU
    //Un programa es la combinacion de un vertex shader y un fragment shader
    private final int mProgram;

    //Estos numeros son como "llaves" que nos permiten acceder a las variables
    //dentro de nuestros shaders (programas de graficos)
    private int positionHandle; //llave para acceder a la variable deposicion
    private int colorHandle; //llave para acceder a la variable de color

    //==================================
    //CONFIGURACION GEOMETRICA DEL PUNTO
    //==================================

    //cada punto en Opengl tiene 3 coordenads (xyz) esto define que trabajamos en un espacio tridimensional
    static final int COORD_POR_VERTEX = 3;

    //Aqui definimos EXACTAMENTE donde queremos que aparezca nuestro punto
    //Las coordenadas van desde -1.0 hasta 1.0
    static float pointCoord[] = {0.5f, 0.5f, 0.0f};

    //==================================
    //CALCULOS AUTOMATICOS (NO CAMBIAN)
    //==================================

    //Calculamos cuantos puntos vamos a dibujar
    //pointCoord.length = 3 coordenadas / 3 coordenadas por vertice = 1 punto
    private final int vertexCount = pointCoord.length / COORD_POR_VERTEX;

    //Calculamos cuantos bytes ocupa cada punto en la memoria:
    //3 coordenadas * 4 bytes por cada numero decimal = 12 bytes por punto
    private final int vertexStride = COORD_POR_VERTEX * 4;

    //Definamos el color de nuestro punto en formato RGBA
    float color[] = {1.0f, 0.0f, 0.0f, 1.0f};

    //=================================================
    //CONSTRUCTOR - Aqui preparamos TODO para dibujar
    //=================================================

    public Point() {
        //PASO 1: PREPARAR LA MEMORIA PARA LAS COORDENADAS

        //Reservamos un bloque de memoria especial que la GPU puede leer directamente
        //pointCoord.lenght*4 = 3 coordenas  * 4 bytes cada uno 12 bytes total
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(pointCoord.length * 4);

        //Aseguramos que los bytes esten en el orden que espera nuestro dispositivo
        //(algunos dispositivos leen bytes de izquierda a derecha, otros al reves)
        byteBuffer.order(ByteOrder.nativeOrder());

        //Convertimos nuestro contenedor de bytes a un contenedor de numeros decimales
        //porque nuestras coordenadas son numeros decimales (floats)
        vertexBuffer = byteBuffer.asFloatBuffer();

        //Copiamos las coordenadas de nuestro punto al contenedor de memoria
        vertexBuffer.put(pointCoord);

        //Movemos el "cursor" de lectura al inicio del contenedor
        //para que cuando la GPU lea, empiece desde el principio
        vertexBuffer.position(0);

        //PASO 2 : COMPILAR LOS PROGRAMAS DE GRAFICOS (SHADERS)

        //Compilamos el VERTEX SHADER - se encarga de las POSICIONES
        //GLES20.GL_VERTEX_SHADER le dice que compile un programa para posiciones
        //vertexShaderCode es el codigo fuente del programa (esta al final de  la clase)
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);

        //Copilamos el FRAGMENT SHADER - se encarga de los COLORES
        //GLES20.GL_FRAGMENT_SHADER le dice que compile un programa para colores
        //fragmentShaderCode es el codigo fuente del programa (esta al final de la clase)
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        //PASO 3: CREAR Y CONFIGURAR EL PROGRMA FINAL

        //Creamos un "programa vacio" en la GPU qu luego llenaremos de nuestros shaders
        mProgram = GLES20.glCreateProgram();

        //Conectamos (adjuntamos) nuestro vertex shader al progrma principal
        GLES20.glAttachShader(mProgram, vertexShader);

        //Conectamos (adjuntamos) nuestro fragment shder al programa principal
        GLES20.glAttachShader(mProgram, fragmentShader);

        //Unimos todo y verificamos que los shaders sean compatibles entre si
        //Si hay errores, aqui es donde se detectarian
        GLES20.glLinkProgram(mProgram);
    }

    // =============================================
    // METODO draw - Se llama CADA VEZ que queremos dibujar el punto
    // =============================================

    public void draw() {
        //PASO 1 : ACTIVAR NUESTRO PROGRAMA DE GRAFICOS

        //Le decimos a la GPU: "A partir de ahora, usa este programa para dibujar"
        //Es como cambiar de herramienta en un progrma de diseño
        GLES20.glUseProgram(mProgram);

        //PASO 2: CONFIGURAR LA POSICION DEL PUNTO

        //Obtenemos la "llave" para acceder a la variable 'vPosition' en nuestro shader
        //'vPosition' es donde guardamos las coordenadas de nuestro punto
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        //Activamos el uso del arreglo de vertices (coordenas)
        //Es como decir: "OK, GPU, ahora vas a recibir datos de posicion"
        GLES20.glEnableVertexAttribArray(positionHandle);

        //Le explicamos a la GPU como debe leer nuestros datos de posicion:
        GLES20.glVertexAttribPointer(
                positionHandle, //GUARDA LOS DATOS DE LA VARIABLE 'vPosition'
                COORD_POR_VERTEX, //CADA PUNTO TIENE 3 COORDENADAS (XYZ)
                GLES20.GL_FLOAT, //Los datos son numeros decimales (floats)
                false, //No normalizamos los numeros
                vertexStride, //Cada punto ocupa 12 bytes en la memoria
                vertexBuffer //Lee los datos de este contenedor de memoria
        );

        //PASO 3: CONFIGURAR EL COLOR DEL PUNTO

        //Obtenemos la "llave" para acceder a la variable'vColor' en nuestro shader
        //'vColor' es donde guardamos el color de nuestro punto
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        //Enviamos el color a la GPU
        //Toma este array de color y asignalo a la variable 'vColor'
        //El 1 significa: envia solo 1 color, el 0 significa: empieza desde el primer elemento
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        //PASO 4: DIBUJAMOS

        //Le decimos a la GPU: "Dibuja Puntos, empieza desde el vertice 0, dibuja 1 punto"
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexCount);

        //Paso 5: LIMPIEZA

        //Desactivamos el arreglo de vertices para liberar recursos
        //es como guardar una herramienta despues de usarla
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    // =============================================
    // MÉTODO AUXILIAR: Compilar shaders (programas de graficos)
    // =============================================

    private int loadShader(int type, String shaderCode) {
        //Creamos un shader vacio del tipo especificado (vertex o fragment)
        int shader = GLES20.glCreateShader(type);

        //Asignamos el codigo fuente (texto) a nuestro shader
        GLES20.glShaderSource(shader, shaderCode);

        //Compilamos el shader: convertimos el texto en instrucciones que la GPU entienda
        //Si hay errores en el codigo, aqui es donde fallaria
        GLES20.glCompileShader(shader);

        //Devolvemos el ID del shader compilado
        return shader;
    }

    //Se trata las posiciones
    //vec4 vector de 4 coordenadas, es por que dentro esta xyzw
    //w siempre es 1
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main(){" +
                    "gl_Position = vPosition;" +
                    "gl_PointSize = 50.0;" +
                    "}"; //A cada coordenada de pantalla se asocia con una real

    //La presicion da informacion precisa del grafico
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main(){" +
                    "gl_FragColor = vColor;" +
                    "}";

}