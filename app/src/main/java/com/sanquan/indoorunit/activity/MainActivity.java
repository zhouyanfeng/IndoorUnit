package com.sanquan.indoorunit.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.icu.text.UnicodeSetSpanner;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.core.persistent.IOnSubscribeListener;
import com.aliyun.alink.linksdk.channel.core.persistent.PersistentNet;
import com.aliyun.alink.linksdk.channel.core.persistent.event.IOnPushListener;
import com.aliyun.alink.linksdk.channel.core.persistent.event.PersistentEventDispatcher;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.adapter.GridViewAdapter;
import com.sanquan.indoorunit.app.MainApplication;
import com.sanquan.indoorunit.bean.LoginBean;
import com.sanquan.indoorunit.bean.WeatherBean;
import com.sanquan.indoorunit.network.RetrofitApi;
import com.sanquan.indoorunit.network.RetrofitNetwork;
import com.sanquan.indoorunit.service.ConnectionService;
import com.sanquan.indoorunit.util.ApkVersionCodeUtils;
import com.sanquan.indoorunit.util.GlobalConfig;
import com.sanquan.indoorunit.util.NetworkUtil;
import com.sanquan.indoorunit.util.TimeUtil;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;
import com.yzxtcp.tools.CustomLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements GridViewAdapter.ItemOnClickListener, SwipeRefreshLayout.OnRefreshListener, ILoginListener {
    private static final String TAG = "MainActivity";

    @BindView(R.id.tv_current_time)
    TextView tvCurrentTime;
    @BindView(R.id.tv_current_week)
    TextView tvCurrentWeek;
    @BindView(R.id.tv_current_date)
    TextView tvCurrentDate;
    @BindView(R.id.tv_detail_temper)
    TextView tvDetailTemper;
    @BindView(R.id.img_toady_day)
    ImageView imgToadyDay;
    @BindView(R.id.img_today_night)
    ImageView imgTodayNight;
    @BindView(R.id.tv_current_temper)
    TextView tvCurrentTemper;
    @BindView(R.id.tv_tomorrow)
    TextView tvTomorrow;
    @BindView(R.id.img_tomorrow_day)
    ImageView imgTomorrowDay;
    @BindView(R.id.img_tomorrow_night)
    ImageView imgTomorrowNight;
    @BindView(R.id.tv_tomorrow_temper)
    TextView tvTomorrowTemper;
    @BindView(R.id.tv_after_tomorrow)
    TextView tvAfterTomorrow;
    @BindView(R.id.img_after_tomorrow_day)
    ImageView imgAfterTomorrowDay;
    @BindView(R.id.img_after_tomorrow_night)
    ImageView imgAfterTomorrowNight;
    @BindView(R.id.tv_after_tomorrow_temper)
    TextView tvAfterTomorrowTemper;

    @BindView(R.id.home_recyclerview)
    RecyclerView homeRecyclerView;

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.tv_refresh_status)
    TextView tvRefreshStatus;
    @BindView(R.id.ll_weather_info)
    LinearLayout llWeatherInfo;

    @BindView(R.id.tv_four)
    TextView tvFour;
    @BindView(R.id.img_four_day)
    ImageView imgFourDay;
    @BindView(R.id.img_four_night)
    ImageView imgFourNight;
    @BindView(R.id.tv_four_temper)
    TextView tvFourTemper;
    @BindView(R.id.tv_yzx_state)
    TextView tvYzxState;
    public static final int CHANGE_TIME = 300;
    private static final int SEND_HEARTBEAT = 400;
    int time = 0;
    private static final int GET_WEATHER = 500;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGE_TIME:
                    time++;
                    if (time == 60){
                        time = 0;
                        getWeather(); //天气是一小时更新一次
                        checkUpdate();
                    }
                    tvCurrentTime.setText(TimeUtil.getCurTime());
                    tvCurrentWeek.setText(TimeUtil.getWeekOfDate());
                    tvCurrentDate.setText(TimeUtil.getTime(System.currentTimeMillis()).split(" ")[0]);
                    handler.sendEmptyMessageDelayed(CHANGE_TIME,1000*60);
                    break;
                case SEND_HEARTBEAT:
                    connectYZX();
                    sendHeartBeat();
                    //也可以加上云之讯的重连机制 ,检测是否连接 未连接则重新连接
                    isServiceRunning(getApplication(),"com.sanquan.indoorunit.service.ConnectionService");
                    sendEmptyMessageDelayed(SEND_HEARTBEAT,1000*60);
                    break;
                case 100:
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setTitle("App下载中...");
                    progressDialog.setProgressNumberFormat("%1d Mb /%2d Mb");
                    progressDialog.setCancelable(false);

                    progressDialog.show();
                    break;
                case 200:
                    progressDialog.dismiss();
                    break;


            }
        }
    };
    private RetrofitNetwork retrofitNetwork;
    private ProgressDialog progressDialog;
    private String apk_url;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDecorView();
        ButterKnife.bind(this);
        retrofitNetwork = RetrofitApi.mRetrofitWeather();
        initRecyclerView();
        getWeather();
        checkUpdate();
        swipeRefreshLayout.setOnRefreshListener(this);
        handler.sendEmptyMessage(SEND_HEARTBEAT);
        startService(new Intent(this, ConnectionService.class)); //开启服务
    }


    private void initRecyclerView(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4);
        homeRecyclerView.setLayoutManager(gridLayoutManager);
        GridViewAdapter adapter = new GridViewAdapter();
        adapter.setItemOnClickListener(this);
        homeRecyclerView.setAdapter(adapter);
    }

    private void connectYZX(){
        String info = UCSManager.isConnect() ? "云之讯已连接" : "云之讯未连接";
        tvYzxState.setText(info);
        Log.i(TAG, "connectYZX: UCSManager.isConnect: "+UCSManager.isConnect());
        if (!UCSManager.isConnect()) {
            Log.i(TAG, "connectYZX: 重新连接");
            UCSManager.connect(GlobalConfig.token,this);
            startService(new Intent(this, ConnectionService.class)); //开启服务
        }
    }
    private void getWeather(){
        progressBar.setVisibility(View.VISIBLE);
        tvRefreshStatus.setVisibility(View.VISIBLE);
        llWeatherInfo.setVisibility(View.GONE);
        tvRefreshStatus.setText("正在刷新中...");
        retrofitNetwork.getWeatherBean(GlobalConfig.getGlobalConfig().getCommunityId(),GlobalConfig.getGlobalConfig().getDevId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WeatherBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError e:"+e.getMessage() );
                        progressBar.setVisibility(View.GONE);
                        tvRefreshStatus.setVisibility(View.VISIBLE);
                        tvRefreshStatus.setText("获取数据失败...");
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(MainActivity.this,R.string.refresh_failed, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(WeatherBean weatherBean) {
                        progressBar.setVisibility(View.GONE);
                        tvRefreshStatus.setVisibility(View.GONE);
                        llWeatherInfo.setVisibility(View.VISIBLE);
                        Log.i(TAG, "onNext weatherBean: "+weatherBean );
                        initData(weatherBean);
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(MainActivity.this,R.string.refresh_success, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initData(WeatherBean weatherBean){
        List<WeatherBean.InfoBean.ResultsBean.WeatherDataBean> results = weatherBean.getInfo().getResults().get(0).getWeather_data();
        tvDetailTemper.setText(results.get(0).getTemperature());
        int length = results.get(0).getDate().length();
        tvCurrentTemper.setText(results.get(0).getDate().substring(length-4,length-1));
        Glide.with(this).load(results.get(0).getDayPictureUrl()).placeholder(R.mipmap.duoyun).into(imgToadyDay);
        Glide.with(this).load(results.get(0).getNightPictureUrl()).placeholder(R.mipmap.duoyun).into(imgTodayNight);

        tvTomorrow.setText(results.get(1).getDate().split(" ")[0]);
        Glide.with(this).load(results.get(1).getDayPictureUrl()).placeholder(R.mipmap.duoyun).into(imgTomorrowDay);
        Glide.with(this).load(results.get(1).getNightPictureUrl()).placeholder(R.mipmap.duoyun).into(imgTomorrowNight);
        tvTomorrowTemper.setText(results.get(1).getTemperature());

        tvAfterTomorrow.setText(results.get(2).getDate().split(" ")[0]);
        Glide.with(this).load(results.get(2).getDayPictureUrl()).placeholder(R.mipmap.duoyun).into(imgAfterTomorrowDay);
        Glide.with(this).load(results.get(2).getNightPictureUrl()).placeholder(R.mipmap.duoyun).into(imgAfterTomorrowNight);
        tvAfterTomorrowTemper.setText(results.get(2).getTemperature());


        tvFour.setText(results.get(3).getDate().split(" ")[0]);
        Glide.with(this).load(results.get(3).getDayPictureUrl()).placeholder(R.mipmap.duoyun).into(imgFourDay);
        Glide.with(this).load(results.get(3).getNightPictureUrl()).placeholder(R.mipmap.duoyun).into(imgFourNight);
        tvFourTemper.setText(results.get(3).getTemperature());

    }

    public void onClick(View view) {
        switch (view.getId()) {


            case R.id.img_my:
                Intent intent = new Intent(this,MyInfoActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void ItemOnClick(int position) {
        if (!GlobalConfig.getGlobalConfig().isOnline()) {
            Toast.makeText(this,"设备不在线，无法进行该操作",Toast.LENGTH_SHORT).show();
            return;
        }
        switch (position) {
            case 0:
                Intent noticesIntent = new Intent(this,NoticesActivity.class);
                startActivity(noticesIntent);
                break;

            case 1://呼叫中心机 云之讯呼叫

//                Intent callIntent = new Intent(MainActivity.this, VideoConverseActivity.class);
//                callIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//
//                String userId = "13874934843";
//                callIntent.putExtra("nickName", userId);
//                callIntent.putExtra("userId", userId);
//                callIntent.putExtra("phoneNumber", userId);
//                CustomLog.e("nickname: "+userId+",userId: "+userId+",call_phone: "+userId);
//                startActivity(callIntent);

                Intent callIntent = new Intent(this,CenterMachineListActivity.class);
                startActivity(callIntent);
                break;

            case 2:  //查询维修
                Intent feedBackIntent1 = new Intent(this,FeedBackActivity.class);
                feedBackIntent1.putExtra("eventType",0);
                startActivity(feedBackIntent1);
                break;

            case 3: //附近商圈
                Intent shoppingIntent = new Intent(this,ShoppingActivity.class);
                shoppingIntent.putExtra("title","附近商圈");
                startActivity(shoppingIntent);
                break;

            case 4:  //意见反馈
                Intent feedBackIntent2 = new Intent(this,FeedBackActivity.class);
                feedBackIntent2.putExtra("eventType",1);
                startActivity(feedBackIntent2);
                break;
            case 5:  //物业账单
                Intent accountIntent = new Intent(this,ShoppingActivity.class);
                accountIntent.putExtra("title","物业账单");
                startActivity(accountIntent);
                break;
            case 6:  //开门记录
                Intent openDoorDecordintent =  new Intent(this,OpenDoorRecordActivity.class);
                startActivity(openDoorDecordintent);
                break;
            case 7:  //系统设置
                Intent intent =  new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeMessages(CHANGE_TIME);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.sendEmptyMessage(CHANGE_TIME);
    }

    @Override
    protected void onResume() {
        super.onResume();
       setDecorView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        handler.removeMessages(SEND_HEARTBEAT);
        stopService(new Intent(this,ConnectionService.class));
    }

    public  void isServiceRunning(Context context, String ServiceName) {
        boolean isServiceRunning = false;
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE)) {
            if(ServiceName.equals(service.service.getClassName()))
            //Service的类名
            {
                isServiceRunning = true;
            }

        }
        if (!isServiceRunning) {
            Intent i = new Intent(context, ConnectionService.class);
            context.startService(i);
        }
    }

    private void setDecorView(){
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    @Override
    public void onRefresh() {
        if (!GlobalConfig.getGlobalConfig().isOnline()) {
            Toast.makeText(this,"设备不在线",Toast.LENGTH_SHORT).show();
        }
        getWeather();
    }
    private void sendHeartBeat(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("dev_id", GlobalConfig.getGlobalConfig().getMacAddr());
            jsonObject.put("dev_type",3);
            String json = jsonObject.toString();
            Log.i(TAG, "sendHeartBeat json: "+json);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json charser=utf-8"),json);
            retrofitNetwork.heartbeat(requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBody>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "onError e"+e.getMessage() );
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String result = responseBody.string();
                                int code = new JSONObject(result).getInt("code");
                                if (code == 102){
                                    GlobalConfig.getGlobalConfig().setOnline(false);
                                  //  Toast.makeText(MainActivity.this,new JSONObject(result).getString("msg"),Toast.LENGTH_SHORT).show();
                                }else if (code == 0){
                                    GlobalConfig.getGlobalConfig().setOnline(true);
                                }
                                Log.e(TAG, "heartBeat onNext result: "+result );
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // app 远程升级

    public void checkUpdate() {
        if (!NetworkUtil.isConnected(MainApplication.getInstance())) {
            Toast.makeText(this, R.string.no_network,Toast.LENGTH_SHORT).show();
            return;
        }
        RetrofitNetwork retrofitNetwork = RetrofitApi.getRetrofitNetwork();
        JSONObject jsonObject = new JSONObject();
        try {
            final int version_code = ApkVersionCodeUtils.getVersionCode(this);
            jsonObject.put("dev_id", GlobalConfig.getGlobalConfig().getMacAddr());
            jsonObject.put("app_name", "IndoorUnit");
            jsonObject.put("version_code", version_code);
            jsonObject.put("os_type", "android");
            final String json = jsonObject.toString();
            Log.e(TAG, "checkUpdate: json: " + json);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json charset=utf-8"), json);
            retrofitNetwork.checkUpgrade(requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBody>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "checkUpdate onError: " + e.getMessage());
                        }

                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String result = responseBody.string();
                                Log.e(TAG, "检测更新 onNext: " + URLDecoder.decode(result,"utf-8"));
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    int latest_version = jsonObject.getInt("latest_version");
                                    Log.e(TAG, "onNext msg: "+URLDecoder.decode(jsonObject.getString("msg"),"utf-8") );
                                    String url = jsonObject.getString("apk_url");
                                    if (latest_version > version_code) {
                                        //这里更新
                                        //这里要检测 写入文件权限
                                        // describe = URLDecoder.decode(jsonObject.getString("apk_message"));
                                        apk_url = url;
                                        showUpdateDialog("", url);
                                    } else {
                                        Toast.makeText(MainActivity.this,"当前为最新版本",Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showUpdateDialog(String describe, final String url) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //这里下载
                dialog.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file = downApkFile(url);
                        handler.sendEmptyMessage(200);
                        installApk(MainApplication.getInstance(), file);
                    }
                }).start();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        dialog.setTitle("App 检测到了更新");
        // dialog.setMessage(describe);
        dialog.show();
    }


    public File downApkFile(String url) {
        handler.sendEmptyMessage(100);
        Log.e(TAG, "downApkFile: url:"+url );
        final File file = new File(getExternalCacheDir(), "SmartLife.apk");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "downApkFile IOException "+e.getMessage() );
                e.printStackTrace();
                handler.sendEmptyMessage(200);
            }
        }
        try {
            URL apk_url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apk_url.openConnection();
            connection.setConnectTimeout(5000);
            InputStream inputStream = connection.getInputStream();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            byte[] bytes = new byte[1024];
            int len = 0;
            progressDialog.setMax(connection.getContentLength()/1024/1024);
            int total = 0;
            while ((len = bis.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
                total += len;
                //这里改变进度条
                progressDialog.setProgress(total/1024/1024);
            }
            fos.close();
            bis.close();
            inputStream.close();
        } catch (MalformedURLException e) {
            handler.sendEmptyMessage(200);
            e.printStackTrace();
            Log.e(TAG, "downApkFile: MalformedURLException "+e.getMessage() );
        } catch (IOException e) {
            handler.sendEmptyMessage(200);
            e.printStackTrace();
            Log.e(TAG, "downApkFile: IOException "+e.getMessage() );
        }
        return file;
    }

    /**
     * 安装apk
     */
    private static void installApk(Context mContext, File file) {
        Uri fileUri = Uri.fromFile(file);
        Intent it = new Intent();
        it.setAction(Intent.ACTION_VIEW);
        it.setDataAndType(fileUri, "application/vnd.android.package-archive");
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 防止打不开应用
        mContext.startActivity(it);
    }


    @Override
    public void onLogin(UcsReason ucsReason) {

    }
}
