package com.sanquan.indoorunit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.adapter.OpenDoorRecordAdapter;
import com.sanquan.indoorunit.app.MainApplication;
import com.sanquan.indoorunit.bean.OpenDoorRecordBean;
import com.sanquan.indoorunit.network.RetrofitApi;
import com.sanquan.indoorunit.network.RetrofitNetwork;
import com.sanquan.indoorunit.util.GlobalConfig;
import com.sanquan.indoorunit.util.NetworkUtil;
import com.sanquan.indoorunit.view.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OpenDoorRecordActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "OpenDoorRecordActivity";
    @BindView(R.id.open_door_record_recyclerView)
     RecyclerView openDoorRecordRecyclerView;
    @BindView(R.id.open_door_record_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.img_no_data)
    ImageView imgNoData;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private List<OpenDoorRecordBean.UnlockLogBean> unlockLogBeanList;
    private OpenDoorRecordAdapter adapter;
    private MyDialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_door_record);
        ButterKnife.bind(this);
        myDialog = MyDialog.newInstance(R.layout.loading);
        tvTitle.setText("开门记录");
        initRecyclerView();
        refreshLayout.setOnRefreshListener(this);
        getUnlockLogs();
    }
   private void  getUnlockLogs(){
       if (!NetworkUtil.isConnected(MainApplication.getInstance())) {
           Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
           return;
       }
       JSONObject jsonObject = new JSONObject();
       RetrofitNetwork retrofitNetwork = RetrofitApi.getRetrofitNetwork();
       try {
           jsonObject.put("dev_id", GlobalConfig.getGlobalConfig().getMacAddr());
           jsonObject.put("dev_type",3);
           String json = jsonObject.toString();
           Log.e(TAG, "getUnlockLogs json: "+json );
           myDialog.show(getFragmentManager(),"loading");
           RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json);
           retrofitNetwork.getUnlockLogs(requestBody)
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new Subscriber<OpenDoorRecordBean>() {
                       @Override
                       public void onCompleted() {

                       }

                       @Override
                       public void onError(Throwable e) {
                           Log.e(TAG, "getUnlockLogs onError e: "+e.getMessage() );
                           myDialog.dismiss();
                           refreshLayout.setRefreshing(false);
                           Toast.makeText(OpenDoorRecordActivity.this,"获取开门记录失败",Toast.LENGTH_SHORT).show();

                       }

                       @Override
                       public void onNext(OpenDoorRecordBean openDoorRecordBean) {
                           Log.e(TAG, "onNext openDoorRecordBean: "+openDoorRecordBean );
                           myDialog.dismiss();
                           refreshLayout.setRefreshing(false);
                           if (openDoorRecordBean.getCode() == 0) {
                               if (openDoorRecordBean.getUnlock_log() != null && openDoorRecordBean.getUnlock_log().size()>0){
                                   imgNoData.setVisibility(View.GONE);
                                    unlockLogBeanList.clear();
                                    unlockLogBeanList.addAll(openDoorRecordBean.getUnlock_log());
                                    adapter.notifyDataSetChanged();
                               }else {
                                   imgNoData.setVisibility(View.VISIBLE);
                                   Toast.makeText(OpenDoorRecordActivity.this,"当前无开门记录",Toast.LENGTH_SHORT).show();
                               }
                           }else if (openDoorRecordBean.getCode() == 400){
                               imgNoData.setVisibility(View.VISIBLE);
                               Toast.makeText(OpenDoorRecordActivity.this,"当前无开门记录",Toast.LENGTH_SHORT).show();
                           }else {
                               imgNoData.setVisibility(View.VISIBLE);
                               Toast.makeText(OpenDoorRecordActivity.this,"获取记录失败,"+openDoorRecordBean.getMsg(),Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
       } catch (JSONException e) {
           e.printStackTrace();
       }
   }


    private void initRecyclerView(){
        unlockLogBeanList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        openDoorRecordRecyclerView.setLayoutManager(manager);
        adapter = new OpenDoorRecordAdapter(this,unlockLogBeanList);
        openDoorRecordRecyclerView.setAdapter(adapter);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        getUnlockLogs();
    }
}
