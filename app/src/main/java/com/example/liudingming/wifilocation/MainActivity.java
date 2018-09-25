package com.example.liudingming.wifilocation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private AMap myMap;
    private DrawerLayout wifiDrawe;
    private ListView wifiListView;
    private WifiControl myWifi;
    private MyAdapter myAdapter;
    private Context context;
    private RadioButton collect;
    private RadioButton located;
    private RadioButton locatiedWifi;
    private AlertDialog choseMode;
    private AlertDialog.Builder dialogBuilder;
    private LocationUD locationUD;
    private int mark=0;
    private Gson gson;
    private HashMap<String,String[]> wifiKVstation;
    private final String[] mode={"收集数据","定位","定位wifi"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        mapView=(MapView)findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        gson=new Gson();
        initView();
        initAlter();
    }
    private void initAlter(){
        dialogBuilder=new AlertDialog.Builder(context);
        choseMode=dialogBuilder.setTitle("请选择模式").setSingleChoiceItems(mode, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        enterMode0();
                        choseMode.hide();
                        break;
                    case  1:
                        enterMode1();
                        choseMode.hide();
                        break;
                    case 2:
                        enterMode2();
                        break;
                }
            }
        }).create();
        choseMode.show();
    }
    private void enterMode0(){
        locationUD.setLocationStyle(3000,true);
        locationUD.setaLocationListen();
    }
    private void enterMode1(){
        locationUD.setLocationStyle(false);
        locationUD.setWifilocate();
    }
    private void draw5G(){
        new Thread(){
            @Override
            public void run() {
                String temp= Httprequst.conn("http://47.94.86.64/wifiLocate.php?action=3");
                String[][] fromJson = gson.fromJson(temp, String[][].class);
                if (fromJson != null) {
                    for (int x = 0; x < fromJson.length; x++) {

                       if(fromJson[x][3].equals('0'));{
                            double latitude0=Double.parseDouble(fromJson[x][1]);
                            double longtitude0=Double.parseDouble(fromJson[x][2]);
                            double latitude1=Double.parseDouble(fromJson[x][3]);
                            double longtitude1=Double.parseDouble(fromJson[x][4]);
                            float[] result=new float[3];
                            Location.distanceBetween(latitude0,longtitude0,latitude1,longtitude1,result);
                            myMap.addCircle(new CircleOptions().center(new LatLng(latitude0,longtitude0)).radius(result[0]));

                        }
                    }
                }
            }
        }.start();
    }
    private void enterMode2(){
        final List<ScanResult> temp=myWifi.getLastedWifiList();
        String[] tempstr=new String[temp.size()];
        for(int i=0;i<temp.size();i++){
            tempstr[i]=temp.get(i).SSID;
        }
        choseMode.dismiss();
        AlertDialog choseWifi;
        AlertDialog.Builder choseWifiB=new AlertDialog.Builder(context);
        choseWifi=choseWifiB.setTitle("选择要定位的wifi").setSingleChoiceItems(tempstr, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                locationUD.locateAWifi(temp.get(which).SSID);

            }
        }).create();
        choseWifi.show();
    }
    private void initView(){
        myWifi=new WifiControl(context);
        wifiDrawe=(DrawerLayout)findViewById(R.id.wifiDrawer);
        wifiListView=(ListView)findViewById(R.id.wifiList);
        collect=(RadioButton)findViewById(R.id.collect);
        located=(RadioButton)findViewById(R.id.located);
        locatiedWifi=(RadioButton)findViewById(R.id.locatedWifi);
        wifiDrawe.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                wifiKVstation=locationUD.getWifiKVlocaton();
                myAdapter.updateWifiList(myWifi.getLastedWifiList());
                myAdapter.setWifiKVlocation(wifiKVstation);
                wifiListView.setAdapter(myAdapter);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        myMap=mapView.getMap();
        locationUD=new LocationUD(context,myMap,myWifi);
        myAdapter=new MyAdapter(myWifi.getLastedWifiList(),context);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

}
