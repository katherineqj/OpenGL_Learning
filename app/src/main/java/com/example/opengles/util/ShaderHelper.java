package com.example.opengles.util;

import android.opengl.GLES20;
import android.util.Log;


/**
 * Created by  katherine on 2019-09-04.
 */
public class ShaderHelper {

    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shadeCode) {
        final int shaderObjectId = GLES20.glCreateShader(type);//创建一个新的着色器对象
        if (shaderObjectId == 0) {//检查是否创建成功，等于0代表失败。
            if (LoggerConfig.ON) {
                Log.e(TAG, "compileShader: could not creat new shader");
            }
        }
        GLES20.glShaderSource(shaderObjectId, shadeCode);//关联着色器对象和代码
        GLES20.glCompileShader(shaderObjectId);//编译该对象的代码
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);//获取编译结果
        if (LoggerConfig.ON) {
            Log.e(TAG, " results of compiling source-" + "shaderCode:" + GLES20.glGetShaderInfoLog(shaderObjectId));
        }
        if (compileStatus[0] == 0) {
            GLES20.glDeleteShader(shaderObjectId);
            if (LoggerConfig.ON) {
                Log.e(TAG, " compiliation of shader failed");
            }
            return 0;

        }
        return shaderObjectId;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programObjextId = GLES20.glCreateProgram();//新建程序对象
        if (programObjextId == 0) {
            if (LoggerConfig.ON) {
                Log.e(TAG, " could not create new program");
            }
            return 0;

        }
        GLES20.glAttachShader(programObjextId, vertexShaderId);//附上顶点着色器
        GLES20.glAttachShader(programObjextId, fragmentShaderId);//附上片段着色器
        GLES20.glLinkProgram(programObjextId);//链接程序

        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programObjextId, GLES20.GL_LINK_STATUS, linkStatus, 0);//获取链接结果
        if (LoggerConfig.ON) {
            Log.e(TAG, " results of linkProgram-" + GLES20.glGetProgramInfoLog(programObjextId));
        }
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(programObjextId);
            if (LoggerConfig.ON) {
                Log.e(TAG, " linking of program failed");
            }
            return 0;
        }
        return programObjextId;
    }

    public static boolean validateProgram(int programObjectId) {
        GLES20.glValidateProgram(programObjectId);
        final int[] validateProgram = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateProgram, 0);
        if (LoggerConfig.ON) {
            Log.e(TAG, " results of validate Program-" + GLES20.glGetProgramInfoLog(programObjectId));
        }
        return validateProgram[0] != 0;
    }
}
