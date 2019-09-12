package com.example.opengles;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

/**
 *
 */
public class AirHockeyActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView; // 添加GLSurfaceView实例，可以初始化OpenGL
    private boolean rendererSet = false;
    private ActivityManager activityManager;
    private ConfigurationInfo configurationInfo;
    private boolean supportsEs2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);

        //检查系统是否支持OpenGL ES 2.0
        activityManager = (ActivityManager) getSystemService(getApplicationContext().ACTIVITY_SERVICE);
        configurationInfo = activityManager.getDeviceConfigurationInfo();
        supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        if (supportsEs2){

            //为OpenGL ES 2.0配置渲染表面
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(new AirHockeyRenderer(this));
            rendererSet = true;

        }else {
            Log.e("qinjie", "当前设备不支持opengl es 2.0" );
        }

        setContentView(glSurfaceView);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rendererSet){
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet){
            glSurfaceView.onResume();
        }
    }
}
