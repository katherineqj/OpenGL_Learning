package com.example.opengles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
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
    private final FloatBuffer vertexData ;
    private Context mContext;
    private int program;
    private static final String U_COLOR= "u_Color";
    private static final String A_POSITION= "a_Position";
    private int uColorLocation;
    private int sPositionLocation;
    private int aPositionLocation;

    public AirHockeyRenderer(Context context) {
        mContext = context;

        //在代码中定义顶点，顶点表示为一个浮点数列表，每个顶点要用两个浮点数进行标记，一个标记x的位置，一个标记y的位置
        float [] tableVertices = {
                0f,0f,
                0f,14f,
                9f,14f,
                0f,9f
        };

        float[] tableVerticesWithTriangles = {
                //triangle 1
                -0.5f,-0.5f,
                0.5f,0.5f,
                -0.5f,0.5f,
                //triangle 2
                -0.5f,-0.5f,
                0.5f,-0.5f,
                0.5f,0.5f,

                -0.5f,0f,
                0.5f,0f,

                0f,-0.25f,
                0f,0.25f,

                0f,0f,

                //triangle 1
                -0.6f,-0.6f,
                0.6f,0.6f,
                -0.6f,0.6f,
                //triangle 2
                -0.6f,-0.6f,
                0.6f,-0.6f,
                0.6f,0.6f,


        };


        float[] new_tableVerticesWithTriangles = {

                0,0,
                -0.5f,-0.5f,
                0.5f,-0.5f,
                0.5f,0.5f,
                -0.5f,0.5f,
                -0.5f,-0.5f,


        };

        float[] tableLine = {
                -0.5f,0f,
                0.5f,0f,

        };

        float [] tableMallets = {
                0f,-0.25f,
                0f,0.25f,
        };

        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length*BYTES_PER_FLOAT)// 首先用ByteBuffer.allocateDirect（需要分配的内存大小）分配一块本地内存，这块内存不会被垃圾回收器管理， 每个浮点数4个字节，
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
        String vertexShadaSource = TextResourceReader.readTextFileFromResource(mContext,R.raw.simple_vertex_shader);
        String fragmentShadaSource = TextResourceReader.readTextFileFromResource(mContext,R.raw.simple_fragment_shader);
        //编译着色器
        int vertexShader = ShaderHelper.compileVertexShader(vertexShadaSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShadaSource);
        //链接程序
        program = ShaderHelper.linkProgram(vertexShader,fragmentShader);
        if (LoggerConfig.ON) {
           ShaderHelper.validateProgram(program);
        }
        GLES20.glUseProgram(program);
        //保存uniform的位置
        uColorLocation = GLES20.glGetUniformLocation(program,U_COLOR);
        //保存属性的位置
        aPositionLocation = GLES20.glGetAttribLocation(program,A_POSITION);
        //缓冲区位置设为起始位置
        vertexData.position(0);
        //关联属性与顶点数据的数组
        GLES20.glVertexAttribPointer(aPositionLocation,POSITION_COMPONENT_COUNT,GLES20.GL_FLOAT, false,0,vertexData);
        GLES20.glEnableVertexAttribArray(aPositionLocation)
        ;

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        // 在Surface被创建以后，每次Surface尺寸发生变化时，这个方法都会被GLSurfaceView调用到，
        // 比如横竖屏切换，尺寸就会发生变化
        GLES20.glViewport(0, 0, i, i1); //设置视口尺寸，告诉opengl可以用来渲染的surface大小


    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // 当绘制一帧时，这个方法会被GLSurfaceView调用，在这个方法中，我们一定要绘制一些东西，
        // 即使只是清空屏幕，因为在这个方法返回后，渲染缓冲区会被交换并显示在屏幕上，如果不画，会看到闪烁效果

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);//清空屏幕，并且用之前 glClearColor定义的颜色填充整个屏幕

        GLES20.glUniform4f(uColorLocation,1.0f,0.0f,0.0f,1.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,11,6);

        GLES20.glUniform4f(uColorLocation,1.0f,1.0f,1.0f,1.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,6);


        GLES20.glUniform4f(uColorLocation,1.0f,0.0f,0.0f,1.0f);
        GLES20.glDrawArrays(GLES20.GL_LINES,6,2);

        GLES20.glUniform4f(uColorLocation,0.0f,0.0f,1.0f,1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS,8,1);

        GLES20.glUniform4f(uColorLocation,0.0f,0.0f,1.0f,1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS,9,1);

        GLES20.glUniform4f(uColorLocation,0.0f,1.0f,1.0f,1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS,10,1);











    }
}
