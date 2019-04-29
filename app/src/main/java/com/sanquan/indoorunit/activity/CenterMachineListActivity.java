package com.sanquan.indoorunit.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.adapter.CenterMachinesAdapter;
import com.sanquan.indoorunit.app.MainApplication;
import com.sanquan.indoorunit.bean.CenterMachinesBean;
import com.sanquan.indoorunit.network.RetrofitApi;
import com.sanquan.indoorunit.network.RetrofitNetwork;
import com.sanquan.indoorunit.util.GlobalConfig;
import com.sanquan.indoorunit.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.security.auth.login.LoginException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CenterMachineListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, CenterMachinesAdapter.ItemClickListener {

    private static final String TAG = "CenterMachineListActivi";
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private CenterMachinesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_machine_list);
        ButterKnife.bind(this);
        initRecy();
        getCenterList();
    }

    private void initRecy(){
        swipeRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CenterMachinesAdapter(null);
        adapter.setItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    private void getCenterList() {
        if (!NetworkUtil.isConnected(MainApplication.getInstance())) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("community_id", GlobalConfig.getGlobalConfig().getCommunityId());
            jsonObject.put("dev_type", 3);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        Log.i(TAG, "getCenterList json: "+json);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json);
        RetrofitNetwork retrofitNetwork = RetrofitApi.getRetrofitNetwork();
        retrofitNetwork.getCenterList(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CenterMachinesBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError e: "+e.getMessage() );
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(CenterMachineListActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(CenterMachinesBean centerMachinesBean) {
                        Log.i(TAG, "onNext CenterMachinesBean: "+centerMachinesBean);
                        swipeRefreshLayout.setRefreshing(false);
                        if (centerMachinesBean.getCode() == 0) {
                            adapter.setCenterListBeanList(centerMachinesBean.getCenter_list());
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        getCenterList();
    }

    @Override
    public void onItemClickListener(CenterMachinesBean.CenterListBean centerListBean) {
        callCenter(centerListBean.getDoor_device_id());
    }
    public void callCenter(String dev_id){
        Intent intent1 = new Intent(CenterMachineListActivity.this, VideoConverseActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //clientNumber":"60000128169867","userid":"sqsx000001"
//        String userId = "10D07A37DD84";
        intent1.putExtra("phoneNumber", dev_id).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("inCall", false);
        startActivity(intent1);
    }
}
