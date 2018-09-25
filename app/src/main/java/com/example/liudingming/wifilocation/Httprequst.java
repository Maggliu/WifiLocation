package com.example.liudingming.wifilocation;

import android.location.Location;
import android.os.Handler;

import com.amap.api.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by LiuDingming on 2017/9/27.
 */

public class Httprequst {
    public static String conn(String path){
        InputStream in=null;
        HttpURLConnection httpURLConnection;
        try {
            URL url=new URL(path);
            httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setReadTimeout(1000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            if(httpURLConnection.getResponseCode()==200)
            in=httpURLConnection.getInputStream();
        }catch (Exception e){e.printStackTrace();}
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        String str=null;
        try{
            while((len = in.read(buffer)) != -1)
            {
                outStream.write(buffer,0,len);
            }
            in.close();
            str=new String(outStream.toByteArray(),"UTF-8");
        }catch (Exception e){e.printStackTrace();}
        return  str;
    }

}
