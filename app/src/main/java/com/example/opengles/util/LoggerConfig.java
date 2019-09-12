package com.example.opengles.util;

import android.util.Log;

/**
 * Created by  katherine on 2019-09-04.
 */
public  class LoggerConfig {
    public LoggerConfig getInstance(){
        return  new LoggerConfig();

    }

    public static final boolean ON = true;
    public void Log(String TAG,String s){
        Log.e(TAG, s );
    }
}
