package com.example.liudingming.wifilocation;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by LiuDingming on 2018/5/23.
 */

public class HttpManage {
    private OkHttpClient client;
    private Request request;
    private Call call;
    private String receiveStr;
    public HttpManage(){
        client=new OkHttpClient();
    }
    public void getFun(String url){
        request=new Request.Builder().url(url).build();
        call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                receiveStr=response.body().string();
                Log.d("RETURNBODY",receiveStr);
            }
        });
    }
    public String getFunS(String url)throws IOException {
        request=new Request.Builder().url(url).build();
        call=client.newCall(request);
        return call.execute().body().string();
    }
}
