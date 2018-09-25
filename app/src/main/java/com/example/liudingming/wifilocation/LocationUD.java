package com.example.liudingming.wifilocation;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.widget.EditText;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

/**
 * Created by LiuDingming on 2018/5/23.
 */

public class LocationUD {
    private AMap aMap;
    private Context mainContext;
    private List<ScanResult> wifiList;
    private WifiControl myWifi;
    private Gson gson;
    private HashMap<String,String[]> wifiKVlocaton;
    private MyLocationStyle myLocationStyle;
    private MarkerOptions markerOptions;
    private Location location;
    private final String uploadUrl="http://47.94.86.64/wifiLocate.php?action=0&updatemessage=";
    private final String downloadUrl="http://47.94.86.64/wifiLocate.php?action=1&updatemessage=";
    public LocationUD(Context context,AMap aMap,WifiControl myWifi){
        mainContext=context;
        this.aMap=aMap;
        this.myWifi=myWifi;
        myLocationStyle=new MyLocationStyle();
        gson=new Gson();
    }
    public void setLocationStyle(long time,boolean show){
        myLocationStyle.interval(time);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(show);
    }
    public void setLocationStyle(boolean show){
        aMap.setMyLocationEnabled(show);
    }
    public void updateKV() {
        String temp = Httprequst.conn(downloadUrl + toStrArray(updateWifilist()));
        Log.d("temp", temp);
        String[][] fromJson = gson.fromJson(temp, String[][].class);
        if (fromJson != null) {
            wifiKVlocaton = new HashMap<String, String[]>();
            for (int x = 0; x < fromJson.length; x++) {
                Log.d("LocationUD", fromJson[x][0] + fromJson[x][1]);
                if (!fromJson[x][1].equals("null"))
                    wifiKVlocaton.put(fromJson[x][0], new String[]{fromJson[x][1], fromJson[x][2]});
                else
                    wifiKVlocaton.put(fromJson[x][0], new String[]{"暂无数据", "暂无数据"});
            }
        }
    }
    public void useWifiLocate(){
        new Thread(){
            @Override
            public void run() {
                updateKV();
                for (int i = 0; i < wifiKVlocaton.size(); i++) {
                    if (!(wifiKVlocaton.get(wifiList.get(i).BSSID)[0].equals("暂无数据"))) {
                        double latitude = Double.parseDouble(wifiKVlocaton.get(wifiList.get(i).BSSID)[0]);
                        double longtitude = Double.parseDouble(wifiKVlocaton.get(wifiList.get(i).BSSID)[1]);
                        markerOptions = new MarkerOptions().position(new LatLng(latitude, longtitude))
                                .title("点击再次定位");
                        aMap.addMarker(markerOptions);

                        break;
                    }
                }
            }
        }.start();
    }
    public void setWifilocate(){
        updateWifilist();
        useWifiLocate();
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                useWifiLocate();
                return true;
            }
        });
    }
    private List<ScanResult> updateWifilist(){
        wifiList=myWifi.getLastedWifiList();
        return wifiList;
    }
    public void setaLocationListen(){
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(final Location location) {
                new Thread(){
                    @Override
                    public void run() {
                        Log.d("Locationup", "locationChanged");
                        String temp = updateSql(location);
                        Log.d("temp",temp);
                        String[][] fromJson = gson.fromJson(temp, String[][].class);
                        if (fromJson != null){
                            wifiKVlocaton = new HashMap<String, String[]>();
                        for (int x = 0; x < fromJson.length; x++) {
                            if (!fromJson[x][1].equals("null"))
                                wifiKVlocaton.put(fromJson[x][0], new String[]{fromJson[x][1], fromJson[x][2]});
                            else
                                wifiKVlocaton.put(fromJson[x][0], new String[]{"暂无数据", "暂无数据"});
                        }
                    }
                    }
                }.start();
            }
        });
    }
    public HashMap<String,String[]> getWifiKVlocaton(){
        return wifiKVlocaton;
    }
    private String toStrArray(List<ScanResult> wifiList,Location location){
        String[][] upMessage=new String[wifiList.size()][5];
        for(int i=0;i<wifiList.size();i++){
            upMessage[i][0]=wifiList.get(i).BSSID;
            upMessage[i][1]=wifiList.get(i).level+"";
            upMessage[i][2]=location.getLatitude()+"";
            upMessage[i][3]=location.getLongitude()+"";
            upMessage[i][4]=WifiControl.getXindao(wifiList.get(i).frequency)+"";
        }
        return gson.toJson(upMessage);
    }
    private String toStrArray(List<ScanResult> wifiList){
        String[] upMessage=new String[wifiList.size()];
        for(int i=0;i<wifiList.size();i++){
            upMessage[i]=wifiList.get(i).BSSID;
        }
        return gson.toJson(upMessage);
    }
    private String updateSql(Location location){
        String wifiGosn=toStrArray(updateWifilist(),location);
        Log.d("wifiGson",wifiGosn);
        return Httprequst.conn(uploadUrl+wifiGosn);
    }
    public void locateAWifi(final String ssid){
        setLocationStyle(1000,true);
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location temp) {
                location=temp;
            }
        });
        aMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                aMap.addMarker(new MarkerOptions().position(locationToLatLng(location)));
                List<ScanResult> temp=myWifi.getLastedWifiList();
                for(int i=0;i<temp.size();i++){
                    if(temp.get(i).SSID.equals(ssid)){
                        double distance=computerDistance(temp.get(i).level,temp.get(i).frequency);
                        aMap.addCircle(new CircleOptions().center(locationToLatLng(location)).radius(distance).visible(true).fillColor(Color.RED));
                        Log.d("LocationUd","DrawCircl；"+distance);
                        break;
                    }
                }

            }
        });
    }
    private double computerDistance(int dB,int f){
       return Math.exp((20-dB-32.44-20*Math.log10(f))/20)*1000;
    }
    private LatLng locationToLatLng(Location temp){
        return new LatLng(temp.getLatitude(),temp.getLongitude());
    }
}
