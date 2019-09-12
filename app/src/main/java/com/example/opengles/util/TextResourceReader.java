package com.example.opengles.util;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by  katherine on 2019-09-04.
 */
public class TextResourceReader {
    public static String readTextFileFromResource(Context context, int resourceId) {
        StringBuilder body = new StringBuilder();
        try{
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;
            while ((nextLine = bufferedReader.readLine())!=null){
                body.append(nextLine);
                body.append("\n");
            }
        }catch (IOException e){
            throw new RuntimeException(" open error");
        }catch (Resources.NotFoundException nfe){
            throw new RuntimeException(" found error");
        }
       return body.toString();
    }
}
