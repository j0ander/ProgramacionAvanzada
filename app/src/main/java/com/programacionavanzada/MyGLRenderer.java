package com.programacionavanzada;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.programacionavanzada.primitivas.Circle;
import com.programacionavanzada.primitivas.Line;
import com.programacionavanzada.primitivas.Lines;
import com.programacionavanzada.primitivas.Point;
import com.programacionavanzada.primitivas.Points;
import com.programacionavanzada.primitivas.Square;
import com.programacionavanzada.primitivas.Triangle;
import com.programacionavanzada.primitivas.Triangles;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Point point;
    private Points points;

    private Line line;

    private Lines lines;

    private Triangle trg;

    private Triangles trgs;

    private Square sq;

    private Circle cr;

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //point.draw();
        //points.draw();
        //line.draw();
        //lines.draw();
        //trg.draw();
//        trgs.draw();
//        sq.draw();
        cr.draw();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //point = new Point();
        //line = new Line();
        //trg = new Triangle();

//        float[] coordsTrgs = {
//                -0.75f, 0.10f, 0.0f,
//                -0.75f, 0.90f, 0.0f,
//                -0.25f, 0.90f, 0.0f,
//                -0.25f, 0.90f, 0.0f,
//                -0.75f, 0.10f, 0.0f,
//                -0.25f, 0.10f, 0.0f
//        };
//
//        trgs = new Triangles(coordsTrgs);

//        float[] coords = {
//                -0.25f, 0.75f, 0.0f,   // Punto 1
//                0.25f, 0.75f, 0.0f,   // Punto 2
//                -0.5f, -0.25f, 0.0f   // Punto 3
//        };
//
//        points = new Points(coords);

//        float[] coordsLinea = {
//                -0.5f, 0.5f,
//                0.5f, 0.5f,
//                -0.5f,-0.30f,
//                -0.5f, -0.70f
//        };
//
//      lines = new Lines(coordsLinea);

//        sq = new Square();
        cr = new Circle(0.5f,25);
    }

    public static int loadShader(int type, String shaderCode) {

        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;

    }
}