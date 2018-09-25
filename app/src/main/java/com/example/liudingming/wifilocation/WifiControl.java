package com.example.liudingming.wifilocation;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LiuDingming on 2018/5/23.
 */

public class WifiControl {
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private ScanResult result;
    private List<ScanResult> scanList;
    private Context context;
    public WifiControl(Context context){
        this.context=context;
        wifiManager=(WifiManager)context.getSystemService(context.WIFI_SERVICE);
        wifiInfo=wifiManager.getConnectionInfo();
        openWifi();
    }
    public void openWifi(){
        if(!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
    }
    public List<ScanResult> getLastedWifiList(){
        wifiManager.startScan();
        scanList=wifiManager.getScanResults();
        return scanList;
    }
    static public int getXindao(int frequency){
        int channel = -1;
        switch (frequency) {
            case 2412:
                channel = 1;
                break;
            case 2417:
                channel = 2;
                break;
            case 2422:
                channel = 3;
                break;
            case 2427:
                channel = 4;
                break;
            case 2432:
                channel = 5;
                break;
            case 2437:
                channel = 6;
                break;
            case 2442:
                channel = 7;
                break;
            case 2447:
                channel = 8;
                break;
            case 2452:
                channel = 9;
                break;
            case 2457:
                channel = 10;
                break;
            case 2462:
                channel = 11;
                break;
            case 2467:
                channel = 12;
                break;
            case 2472:
                channel = 13;
                break;
            case 2484:
                channel = 14;
                break;
            case 5745:
                channel = 149;
                break;
            case 5765:
                channel = 153;
                break;
            case 5785:
                channel = 157;
                break;
            case 5805:
                channel = 161;
                break;
            case 5825:
                channel = 165;
                break;
        }
        return channel;
    }

}
