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

import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.adapter.FeedbackAdapter;
import com.sanquan.indoorunit.app.MainApplication;
import com.sanquan.indoorunit.bean.FeedbackBean;
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

public class FeedBackActivity extends AppCompatActivity implements FeedbackAdapter.OnImageItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "FeedBackActivity";
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.getFeedbackRecyclerView)
    RecyclerView getFeedbackRecyclerView;

    @BindView(R.id.tv_title)
    TextView tvTtile;

    private FeedbackAdapter adapter;
    private List<FeedbackBean.ResBean> resBeanList;
    private int event_type;
    private boolean isFresh = false;
    private MyDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);
        event_type = getIntent().getIntExtra("eventType", 0);
        String title = event_type == 0 ? "房屋报修列表" : "意见反馈列表";
        tvTtile.setText(title);
        initRecyclerView();
        myDialog = MyDialog.newInstance(R.layout.loading);
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    private void initRecyclerView(){
        resBeanList = new ArrayList<>();
        adapter = new FeedbackAdapter(resBeanList);
        adapter.setOnImageItemClickListener(this);
        getFeedbackRecyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        getFeedbackRecyclerView.setLayoutManager(manager);
    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.btn_build_new_issue:
                Intent intent = new Intent(this,BuildNewIssueActivity.class);
                intent.putExtra("eventType",event_type);
                startActivity(intent);
                break;
            case R.id.img_back:
                finish();
                break;
        }
    }

    private static final int PREVIEW_IMAGE = 100;
    @Override
    public void onImageItemClickListener(int itemPosition, int imagePosition) {
        Log.i(TAG, "onImageItemClickListener itemPosition: "+itemPosition+",data:"+resBeanList.get(itemPosition));
        Intent intent = new Intent(this,ImagePreviewActivity.class);
        intent.putStringArrayListExtra("imagePath",resBeanList.get(itemPosition).getEvent_img());
        intent.putExtra("imagePosition",imagePosition);
        startActivityForResult(intent,PREVIEW_IMAGE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getFeedback();
    }

    @Override
    public void onRefresh() {
        getFeedback();
        isFresh = true;
    }


    private void getFeedback(){
        if (!NetworkUtil.isConnected(MainApplication.getInstance())) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        RetrofitNetwork retrofitNetwork = RetrofitApi.getRetrofitNetwork();
        try {
            jsonObject.put("dev_id", GlobalConfig.getGlobalConfig().getMacAddr());
            jsonObject.put("dev_type",3);
            jsonObject.put("event_type",event_type);
            String json = jsonObject.toString();
            Log.e(TAG, "getFeedback post_json: "+json);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json);
            myDialog.show(getFragmentManager(),"loading");
            retrofitNetwork.getFeedback(requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<FeedbackBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            myDialog.dismiss();
                            swipeRefreshLayout.setRefreshing(false);
                            Log.e(TAG, "onError e: "+e.getMessage()+","+e.toString());
                            Toast.makeText(FeedBackActivity.this,R.string.get_data_failed,Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(FeedbackBean feedBackBean) {
                            myDialog.dismiss();
                            if (feedBackBean.getCode() == 0){
                                swipeRefreshLayout.setRefreshing(false);
                                resBeanList.clear();
                                resBeanList.addAll(feedBackBean.getRes());
                                adapter.notifyDataSetChanged();
                                if (feedBackBean.getRes().size() == 0){
                                    Toast.makeText(FeedBackActivity.this,"当前无数据",Toast.LENGTH_SHORT).show();
                                }else {
                                    if (isFresh)
                                        Toast.makeText(FeedBackActivity.this,R.string.refresh_success,Toast.LENGTH_SHORT).show();
                                }
                            }
                            Log.e(TAG, "onNext feedbackBean: "+feedBackBean );
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
