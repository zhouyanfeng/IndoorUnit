package com.sanquan.indoorunit.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.app.MainApplication;
import com.sanquan.indoorunit.network.RetrofitApi;
import com.sanquan.indoorunit.network.RetrofitNetwork;
import com.sanquan.indoorunit.util.GlobalConfig;
import com.sanquan.indoorunit.util.NetworkUtil;
import com.sanquan.indoorunit.view.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BuildNewIssueActivity extends AppCompatActivity {

    private static final String TAG = "BuildNewIssueActivity";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ed_input_content)
    EditText edInputContent;
    @BindView(R.id.btn_send)
    Button btnSend;
    private int eventType;
    private String title;
    private MyDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_new_issue);

        ButterKnife.bind(this);
        eventType = getIntent().getIntExtra("eventType",0);
        title = eventType == 0 ? "提交报修" : "提交反馈";
        tvTitle.setText(title);
        myDialog = MyDialog.newInstance(R.layout.loading);
    }



    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;

            case R.id.btn_send:
                if (!NetworkUtil.isConnected(MainApplication.getInstance())) {
                    Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
                    return;
                }
                String content = edInputContent.getText().toString();
                if ("".equals(content)) {
                    Toast.makeText(this, "请输入你要提交的内容!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e(TAG, "onClick content: " + content + ",title:" + title);
                btnSend.setClickable(false);
                btnSend.setBackgroundResource(R.color.gray);
                myDialog.show(getFragmentManager(),"loading");

                reportRoomIssue(content);
                break;

        }
    }

    private void reportRoomIssue(String connect) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("dev_id", GlobalConfig.getGlobalConfig().getMacAddr());
            jsonObject.put("content", connect);
            jsonObject.put("dev_type", 3);
            jsonObject.put("event_type", eventType + "");
            String json = jsonObject.toString();
            final RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), json);
            Log.e(TAG, "reportRoomIssue post_json: " + json);
            RetrofitNetwork retrofitNetwork = RetrofitApi.getRetrofitNetwork();
            List<MultipartBody.Part> imgFileList = new ArrayList<>();
            int i = 0;

            retrofitNetwork.subFeedback(requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBody>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "onError reportRoomIssue e: " + e.getMessage());
                            Toast.makeText(BuildNewIssueActivity.this,"发送失败",Toast.LENGTH_SHORT).show();
                            myDialog.dismiss();
                            btnSend.setClickable(true);
                            btnSend.setBackgroundResource(R.drawable.send_bg);
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                myDialog.dismiss();
                                btnSend.setClickable(true);
                                btnSend.setBackgroundResource(R.drawable.send_bg);
                                String result = responseBody.string();
                                Log.i(TAG, "onNext reportRoomIssue  ResponseBody: " + result);
                                JSONObject jsonObject1 = new JSONObject(result);
                                int code = jsonObject1.getInt("code");
                                if (code == 0) {
                                    Toast.makeText(BuildNewIssueActivity.this, R.string.upload_success, Toast.LENGTH_SHORT).show();
                                    edInputContent.setText("");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (JSONException e) {
            Log.e(TAG, "reportRoomIssue JSONException " + e.getMessage());
            e.printStackTrace();
        } finally {
        }
    }

}
