package com.sanquan.indoorunit.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.gson.Gson;
import com.sanquan.indoorunit.app.MainApplication;
import com.sanquan.indoorunit.bean.LoginBean;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * 作者：zyf on 2018/10/9.
 * 用途：TODO
 */
public class GlobalConfig {

    SharedPreferences saveLoginJsonPre = MainApplication.getInstance().getSharedPreferences("saveLoginJson", Context.MODE_PRIVATE);
    SharedPreferences sharedPreferences = MainApplication.getInstance().getSharedPreferences("isOnline",Context.MODE_PRIVATE);
    private static final String TAG = "GlobalConfig";
    public static final String appkey = "b79f79ef18e60f03698e99f5d7148e77";
    public static final String secrekey = "bUkqvrDVSqTgDRZMLUqN";
    private String communityId;
    private String dev_secret;
    private String name;

    public String getDev_secret() {
        return dev_secret == null ? getLoginBean().getInfo().getDevice_secret() : dev_secret;
    }

    public void setDev_secret(String dev_secret) {
        this.dev_secret = dev_secret;
    }

    public String getName() {
        return name == null ? getLoginBean().getInfo().getName() :name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommunityId() {
        return communityId == null ? getLoginBean().getInfo().getCommunity_id()+"" : communityId;
    }


    private LoginBean getLoginBean(){
        String loginJson = getLoginJson();
        return new Gson().fromJson(loginJson,LoginBean.class);
    }
    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    private boolean isOnline;

    public boolean isOnline() {
        return isOnline == false ? getOnLineState():isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
        saveOnLineState(online);
    }

    private boolean getOnLineState(){
        return  saveLoginJsonPre.getBoolean("isOnline",false);
    }
    private void saveOnLineState(boolean isOnline){

        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean("isOnline",isOnline);
    }
    private String loginJson;

    public String getLoginJson() {
        return loginJson == null ? getLoginJsonPre() : loginJson;
    }

    public void setLoginJson(String loginJson) {
        this.loginJson = loginJson;
        savaLoginJson(loginJson);
    }

    private void savaLoginJson(String loginJson){
        SharedPreferences.Editor edit = saveLoginJsonPre.edit();
        edit.putString("saveLoginJson",loginJson);
        boolean commit = edit.commit();
        Log.e(TAG, "savaLoginJson commit "+commit );
    }

    private String getLoginJsonPre(){
        String saveLoginJson = saveLoginJsonPre.getString("saveLoginJson", new Gson().toJson(new LoginBean.InfoBean()));
        Log.e(TAG, "getLoginJsonPre saveLoginJson: "+saveLoginJson );
        return saveLoginJson;
    }

    public static String token;
    private String devId;

    public String getDevId() {
        return devId == null ? getMacAddr() : devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }
    private GlobalConfig(){
        setDevId(getMacAddr());
    }

    private static GlobalConfig globalConfig = null;
    public  static GlobalConfig getGlobalConfig(){
        if (globalConfig == null)
           synchronized (GlobalConfig.class){
                if (globalConfig == null)
                    globalConfig = new GlobalConfig();
           }
        return globalConfig;
    }

    public  String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                String result = res1.toString();
                return result.toUpperCase().replaceAll(":","").trim();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00".replaceAll(";","").trim();
    }
}
