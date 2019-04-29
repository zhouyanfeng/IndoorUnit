package com.sanquan.indoorunit.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.adapter.NoticeListAdapter;
import com.sanquan.indoorunit.app.MainApplication;
import com.sanquan.indoorunit.bean.AppHomeInfoBean;
import com.sanquan.indoorunit.bean.LoginBean;
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

public class NoticesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "NoticesActivity";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.notice_list_recyclerview)
    RecyclerView noticeListRecycler;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private NoticeListAdapter adapter;
    private List<AppHomeInfoBean.NoticeBean> noticeBeanList = new ArrayList<>();
    private MyDialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);
        ButterKnife.bind(this);
        tvTitle.setText("通知");
        swipeRefreshLayout.setOnRefreshListener(this);
        adapter = new NoticeListAdapter(noticeBeanList);
        noticeListRecycler.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        noticeListRecycler.setLayoutManager(manager);
        myDialog = MyDialog.newInstance(R.layout.loading);
        getNoticesList();
    }




    private void getNoticesList() {
        if (!NetworkUtil.isConnected(MainApplication.getInstance())) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
            return;
        }
        RetrofitNetwork retrofitNetwork = RetrofitApi.getRetrofitNetwork();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("community_id", new Gson().fromJson(GlobalConfig.getGlobalConfig().getLoginJson(), LoginBean.InfoBean.class).getCommunity_id());
            String json = jsonObject.toString();
            Log.e(TAG, "getAppHomeInfo: json: " + json);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
            myDialog.show(getFragmentManager(),"loading");
            retrofitNetwork.getNoticesList(requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<AppHomeInfoBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            myDialog.dismiss();
                            Log.e(TAG, "onError: " + e.getMessage());

                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(NoticesActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(AppHomeInfoBean  appHomeInfoBean) {
                            myDialog.dismiss();
                            Log.e(TAG, "onNext notices: " + appHomeInfoBean.getNotice());
                            swipeRefreshLayout.setRefreshing(false);
                            if (appHomeInfoBean.getNotice().size() == 0){
                                Toast.makeText(NoticesActivity.this, "当前无数据", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(NoticesActivity.this, "刷新完成", Toast.LENGTH_SHORT).show();
                            }
                            noticeBeanList.clear();
                            noticeBeanList.addAll(appHomeInfoBean.getNotice());
                            adapter.notifyDataSetChanged();
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        getNoticesList();
    }
}
