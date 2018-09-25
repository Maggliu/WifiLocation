package com.example.liudingming.wifilocation;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LiuDingming on 2017/9/26.
 */

public class MyAdapter extends BaseAdapter {
    private Context myContext;
    private String check_result;
    private String url;
    private Handler handler;
    private List<ScanResult> wifiList;
    private HashMap<String,String[]> wifiKVlocation;
    public MyAdapter(List<ScanResult> wifiList, Context myContext){
        this.wifiList=wifiList;
        this.myContext=myContext;
    }
    public void updateWifiList(List<ScanResult> wifiList){
        this.wifiList=wifiList;
    }
    public void setWifiKVlocation(HashMap<String,String[]> wifiKVlocation)
    {this.wifiKVlocation=wifiKVlocation;}
    @Override
    public int getCount() {
        return wifiList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(myContext).inflate(R.layout.wifi_coantaion,parent,false);
        TextView ssid=(TextView)convertView.findViewById(R.id.ssid);
        TextView bssid=(TextView)convertView.findViewById(R.id.Bssid);
        TextView level=(TextView)convertView.findViewById(R.id.level);
        TextView latitude=(TextView)convertView.findViewById(R.id.latitude);
        TextView longtitude=(TextView)convertView.findViewById(R.id.longtitude);
        TextView xindao=(TextView)convertView.findViewById(R.id.xindao);
        TextView frequency=(TextView)convertView.findViewById(R.id.frequency);
        String BSSID=wifiList.get(position).BSSID;
        ssid.setText(wifiList.get(position).SSID);
        bssid.setText(BSSID);
        level.setText(wifiList.get(position).level+"");
        frequency.setText(wifiList.get(position).frequency+"MHz");
        xindao.setText(WifiControl.getXindao(wifiList.get(position).frequency)+"信道");
        if(wifiKVlocation!=null){
            if(wifiKVlocation.containsKey(BSSID)){
                latitude.setText(wifiKVlocation.get(BSSID)[0]);
                longtitude.setText(wifiKVlocation.get(BSSID)[1]);
            }
            else {latitude.setText("NOBSSID");longtitude.setText("NOBSSID");}
        }
        else latitude.setText("null");
        return convertView;
    }
}
