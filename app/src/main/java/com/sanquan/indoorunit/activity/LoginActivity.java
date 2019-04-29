package com.sanquan.indoorunit.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;
import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.app.MainApplication;
import com.sanquan.indoorunit.bean.LoginBean;
import com.sanquan.indoorunit.network.RetrofitApi;
import com.sanquan.indoorunit.network.RetrofitNetwork;
import com.sanquan.indoorunit.service.ConnectionService;
import com.sanquan.indoorunit.util.GlobalConfig;
import com.sanquan.indoorunit.util.NetworkUtil;
import com.sanquan.indoorunit.util.QRCodeUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import com.google.zxing.common.BitMatrix;

import javax.security.auth.login.LoginException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity implements ILoginListener {

    @BindView(R.id.tv_machine_name)
    TextView tvMachineName;
    private static final String TAG = "LoginActivity";
    private String macStr;
    @BindView(R.id.qrcode_img)
    ImageView qrcodeImg;

    @BindView(R.id.tv_login_state)
    TextView tvLoginState;
    public static final int RELOGIN = 100;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RELOGIN:
                    doLogin();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        macStr = GlobalConfig.getGlobalConfig().getMacAddr();
        tvMachineName.setText("本机MAC:" + macStr);
        Log.e(TAG, "onCreate mac: "+ macStr);
        Bitmap qrImage = QRCodeUtil.createQRImage(GlobalConfig.getGlobalConfig().getDevId(), 500, 500);
        qrcodeImg.setImageBitmap(qrImage);
        doLogin();
    }



    private void doLogin() {
        if (!NetworkUtil.isConnected(MainApplication.getInstance())) {
            tvLoginState.setText("当前无网络,请连接网络后进行登录");
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessageDelayed(RELOGIN,2000);
            return;
        }
        RetrofitNetwork retrofitNetwork = RetrofitApi.getRetrofitNetwork();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("dev_id", macStr);
            jsonObject.put("dev_type", 3);
            final RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
            retrofitNetwork.login(requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<LoginBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            tvLoginState.setText("登录失败 error: "+e.getMessage());
                           // Toast.makeText(LoginActivity.this,R.string.login_failed,Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onError e: " + e.getMessage());
                            handler.sendEmptyMessageDelayed(RELOGIN,2000);
                        }

                        @Override
                        public void onNext(LoginBean loginBean) {
                            Log.e(TAG, "login onNext loginBean: " + loginBean);
                            if (loginBean.getCode() == 0) {
                                GlobalConfig.getGlobalConfig().setName(loginBean.getInfo().getName());
                                GlobalConfig.getGlobalConfig().setDev_secret(loginBean.getInfo().getDevice_secret());
                                GlobalConfig.getGlobalConfig().setCommunityId(loginBean.getInfo().getCommunity_id()+"");
                                GlobalConfig.getGlobalConfig().setLoginJson(new Gson().toJson(loginBean.getInfo()));
                                GlobalConfig.getGlobalConfig().setOnline(true);
                                GlobalConfig.token = loginBean.getInfo().getYzx_token();
                                Toast.makeText(LoginActivity.this,R.string.login_success,Toast.LENGTH_SHORT).show();
                                UCSManager.connect(loginBean.getInfo().getYzx_token(),LoginActivity.this);
                                tvLoginState.setText(R.string.login_success);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                handler.sendEmptyMessageDelayed(RELOGIN,2000);
                                tvLoginState.setText("登录失败,"+loginBean.getMsg());
                               // Toast.makeText(LoginActivity.this,"登录失败,"+loginBean.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLogin(UcsReason ucsReason) {
        Log.e(TAG, "onLogin ucsReason: "+ucsReason.getReason()+",msg: "+ucsReason.getReason() );
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeMessages(RELOGIN);
    }
}
