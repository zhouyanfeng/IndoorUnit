package com.sanquan.indoorunit.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.app.MainApplication;
import com.sanquan.indoorunit.bean.LoginBean;
import com.sanquan.indoorunit.util.GlobalConfig;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyInfoActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTtile;
    @BindView(R.id.tv_building_info)
    TextView tvBuildingInfo;
    @BindView(R.id.tv_app_version)
    TextView tvAppVersion;
    @BindView(R.id.tv_app_mac_address)
    TextView tvAppMacAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        ButterKnife.bind(this);
        tvTtile.setText("本机信息");
        if (!GlobalConfig.getGlobalConfig().isOnline()) {
            tvBuildingInfo.setText("设备不存在");
        }else {
            LoginBean.InfoBean infoBean = new Gson().fromJson(GlobalConfig.getGlobalConfig().getLoginJson(),LoginBean.InfoBean.class);
            tvBuildingInfo.setText(infoBean.getCommunity_name()+infoBean.getBuilding_name()+infoBean.getFloor()+"层"+infoBean.getRoom()+"房");
        }
        tvAppVersion.setText("V "+getVersion());
        tvAppMacAddress.setText(GlobalConfig.getGlobalConfig().getMacAddr());
    }


    public void onClick(View view){
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }
    private int getVersion(){
        PackageInfo packageInfo = null;
        int appVersion = 1;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersion;
    }
}
