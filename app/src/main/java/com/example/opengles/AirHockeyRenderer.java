package com.example.opengles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.opengles.util.LoggerConfig;
import com.example.opengles.util.ShaderHelper;
import com.example.opengles.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by  katherine on 2019-09-03.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;
    private Context mContext;
    private int program;

    private static final String A_POSITION = "a_Position";

    private int aPositionLocation;


    private static final String A_COLOR = "a_Color";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE =(POSITION_COMPONENT_COUNT+COLOR_COMPONENT_COUNT)*BYTES_PER_FLOAT;
    private int aColorLocation;

    private static final String U_MATRIX = "u_Matrix";
    private final  float[] projectionMatrix = new float[16];
    private int uMatrixLocation;


    public AirHockeyRenderer(Context context) {
        mContext = context;

        float[] tableVerticesWithTriangles = {

                0f, 0f, 1f, 1f, 1f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

                -0.5f, 0f, 1f, 0f, 0f,
                0.5f, 0f, 1f, 0f, 0f,

                0f, -0.4f, 0f, 0f, 1f,
                0f, 0.4f, 1f, 0f, 0f

        };

        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)// 首先用ByteBuffer.allocateDirect（需要分配的内存大小）分配一块本地内存，这块内存不会被垃圾回收器管理， 每个浮点数4个字节，
                .order(ByteOrder.nativeOrder())//告诉字节缓冲区，按照本地字节序组织它的内容
                .asFloatBuffer();//得到一个可以反映底层的floatbuffer类的实例
        vertexData.put(tableVerticesWithTriangles);//把数据从dalvik的内存复制到本地内存

    }

    /**
     * @param gl10
     * @param eglConfig
     */
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // 当Surface被创建的时候，GLSurfaceView会调用这个方法，发生在程序第一次运行的时候，
        // 并且当设备被唤醒，或者用户从其他activity 切换回来时，这个方法也可能会被调用，
        // 这意味着，当程序运行时，本方法可能会被调用多次

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); //设置清空屏幕用的颜色
        //读取着色器的源代码
        String vertexShadaSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_vertex_shader);
        String fragmentShadaSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_fragment_shader);
        //编译着色器
        int vertexShader = ShaderHelper.compileVertexShader(vertexShadaSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShadaSource);
        //链接程序
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }
        GLES20.glUseProgram(program);
        //保存uniform的位置
        //保存属性的位置
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        //缓冲区位置设为起始位置
        vertexData.position(0);
        //关联属性与顶点数据的数组
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aPositionLocation);

        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);

        vertexData.position(POSITION_COMPONENT_COUNT);
        //关联属性与顶点数据的数组
        GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aColorLocation);

        uMatrixLocation = GLES20.glGetUniformLocation(program,U_MATRIX);

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        // 在Surface被创建以后，每次Surface尺寸发生变化时，这个方法都会被GLSurfaceView调用到，
        // 比如横竖屏切换，尺寸就会发生变化
        GLES20.glViewport(0, 0, width, height); //设置视口尺寸，告诉opengl可以用来渲染的surface大小

        float aspectRatio = width>height?
                (float)width/(float)height:
                (float)height/(float)width;

        if (width>height){
            Matrix.orthoM(projectionMatrix,0,-aspectRatio,aspectRatio,-1f,1f,-1f,1f);
        }else {
            Matrix.orthoM(projectionMatrix,0,-1f,1f,-aspectRatio,aspectRatio,-1f,1f);

        }
        GLES20.glUniformMatrix4fv(uMatrixLocation,1,false,projectionMatrix,0);


    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // 当绘制一帧时，这个方法会被GLSurfaceView调用，在这个方法中，我们一定要绘制一些东西，
        // 即使只是清空屏幕，因为在这个方法返回后，渲染缓冲区会被交换并显示在屏幕上，如果不画，会看到闪烁效果

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);//清空屏幕，并且用之前 glClearColor定义的颜色填充整个屏幕

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);

        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);


    }
}
