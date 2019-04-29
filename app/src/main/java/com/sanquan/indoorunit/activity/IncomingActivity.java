package com.sanquan.indoorunit.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.app.MainApplication;
import com.sanquan.indoorunit.util.yzxutils.DfineAction;
import com.sanquan.indoorunit.util.yzxutils.UIDfineAction;
import com.yzx.api.UCSCall;
import com.yzxtcp.UCSManager;
import com.yzxtcp.listener.OnRecvTransUCSListener;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.NetWorkTools;

import org.json.JSONException;
import org.json.JSONObject;

public class IncomingActivity extends AppCompatActivity {

    private static final String TAG = "IncomingActivity";
    private static final int AUDIO_CONVERSE_CLOSE = 100;
    public static String IncomingCallId;
    private boolean inCall = false;
    private String calledUid;
    private String calledPhone;
    private String userName;
    private String phoneNumber;
    private String nickName;

    private TextView tvUserName;
    private TextView tvType;

    private int sound; // 触摸提示音的状态，0：关，1：开
    private AudioManager mAudioManager;
    private String callId = "";
    private String remoteUserId;
    private String dev_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming);
        initView();
        initData();

        IntentFilter ift = new IntentFilter();
        ift.addAction(UIDfineAction.ACTION_DIAL_STATE);
        ift.addAction(UIDfineAction.ACTION_DIAL_HANGUP);
        ift.addAction(UIDfineAction.ACTION_NETWORK_STATE);
        ift.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(br, ift);
        try {
            //如果系统触摸音是关的就不用管，开的就把它给关掉，因为在个别手机上有可能会影响音质
            mAudioManager = ((AudioManager) getSystemService(Context.AUDIO_SERVICE));
            sound = Settings.System.getInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED);
            Log.e(TAG,"AudioConverseActivity sound: " + sound);
            if(sound == 1) {
                Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
                mAudioManager.unloadSoundEffects();
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG,"SettingNotFound SOUND_EFFECTS_ENABLED ...");
        }

        if (inCall) {
            Log.e(TAG,"IncomingCallId = " + IncomingCallId + ",callId = " + getIntent().getStringExtra("callId"));
            if(getIntent().hasExtra("callId")) {
                callId = getIntent().getStringExtra("callId");
                if(callId.equals(IncomingCallId)) {
//                  sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.HUNGUP_OTHER));
                    tvType.setText("对方已挂机");
                    UCSCall.stopRinging(IncomingActivity.this);
                    handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
                    return;
                }
            }
        }

        //接收远程设备发送的id
        UCSManager.setOnRecvTransUCSListener(new OnRecvTransUCSListener(){
            @Override
            public void onRecvTranslate(final String fromUserId, final String data , String callid, String previewImgUrl) {
                Log.i("YZXMSG", "发送方UserId："+fromUserId+"，收到透传数据："+data+",Thread.name:"+Thread.currentThread().getName());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            dev_name = jsonObject.getString("dev_name");
                            tvUserName.setText(dev_name);
                            remoteUserId = fromUserId;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onReceive(int i, byte[] bytes, int i1) {
                super.onReceive(i, bytes, i1);
                Log.i("YZXMSG", "onReceive: i:"+i+",bytes:"+bytes+",i1:"+i1 );
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AUDIO_CONVERSE_CLOSE:
                    UCSCall.setSpeakerphone(IncomingActivity.this, false);
                    IncomingActivity.this.finish();
                    break;
            }
        }
    };
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(UIDfineAction.ACTION_DIAL_STATE)){//响铃还有呼叫错误时在ConnectionService中发送广播
                int state = intent.getIntExtra("state", 0);
                CustomLog.v(DfineAction.TAG_TCP, "AUDIO_CALL_STATE:"+state);
                if(UIDfineAction.dialState.keySet().contains(state)){
                    Log.e(TAG,state+UIDfineAction.dialState.get(state));
                    //获得通话状态信息
                    tvType.setText(UIDfineAction.dialState.get(state));
                }
                //直拨对方响铃即停止本地回玲
                if (state == UCSCall.CALL_VOIP_RINGING_180) {
                    UCSCall.stopCallRinging(IncomingActivity.this);
                }
                if(state == UCSCall.NOT_NETWORK){
                    tvType.setText("当前处于无网络状态");
                    UCSCall.stopRinging(IncomingActivity.this);
                    handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
                }
                if(state == UCSCall.HUNGUP_REFUSAL || state == UCSCall.HUNGUP_MYSELF
                        || state == UCSCall.HUNGUP_OTHER || state == UCSCall.HUNGUP_MYSELF_REFUSAL
                        || state == UCSCall.HUNGUP_RTP_TIMEOUT || state == UCSCall.HUNGUP_OTHER_REASON
                        || state == UCSCall.HUNGUP_GROUP){
                    CustomLog.v("收到挂断信息");
                    UCSCall.stopRinging(IncomingActivity.this);
                    handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
                }else{
                    if((state >= 300210 && state <= 300260)&&
                            (state != 300221 && state != 300222 && state !=300247)
                            || state == UCSCall.HUNGUP_NOT_ANSWER || state == UCSCall.CALL_NUMBER_IS_EMPTY) {
                        handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
                    }
                }
                if(state == UCSCall.HUNGUP_REFUSAL || state == UCSCall.HUNGUP_MYSELF
                        || state == UCSCall.HUNGUP_OTHER || state == UCSCall.HUNGUP_MYSELF_REFUSAL
                        || state == UCSCall.HUNGUP_RTP_TIMEOUT || state == UCSCall.HUNGUP_OTHER_REASON
                        || state == UCSCall.HUNGUP_GROUP){
                    Log.e(TAG,"收到挂断信息");
                    UCSCall.stopRinging(IncomingActivity.this);
                    handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
                }else{
                    if((state >= 300210 && state <= 300260)&&
                            (state != 300221 && state != 300222 && state !=300247)
                            || state == UCSCall.HUNGUP_NOT_ANSWER || state == UCSCall.CALL_NUMBER_IS_EMPTY) {
                        handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
                    }
                }
            }else if(intent.getAction().equals(UIDfineAction.ACTION_DIAL_HANGUP)){//这个也是ConnectionService回调方法onHangUp中发送广播
                handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1000);
            }
        }
    };
    private void initView(){
        tvUserName = findViewById(R.id.converse_name);
        tvType = findViewById(R.id.converse_information);
    }
    private void initData(){
        //判断是否是来电信息，默认是去电，（来电信息是由ConnectionService中的onIncomingCall回调中发送广播，开启通话界面，inCall为true）
        if(getIntent().hasExtra("inCall")){
            inCall = getIntent().getBooleanExtra("inCall", false);
            //判断网络类型，2G时提示一下
            int netstate = NetWorkTools.getCurrentNetWorkType(this);
            if (netstate == NetWorkTools.NETWORK_EDGE)
                Toast.makeText(this, "网络状态差", Toast.LENGTH_SHORT).show();
        }
        //获得要拨打的号码，智能拨打和免费通话：phoneNumber代表ClientID，直拨和回拨代表ClientID绑定的手机号码
        if(getIntent().hasExtra("phoneNumber")) {
            phoneNumber = getIntent().getStringExtra("phoneNumber");
        }
        if(getIntent().hasExtra("nickName")) {
            nickName = getIntent().getStringExtra("nickName");
        }

        if(inCall){
            //来电
            if(nickName != null && !"".equals(nickName)){
                tvUserName.setText(nickName);
            }else if(phoneNumber != null && !"".equals(phoneNumber)){
                tvUserName.setText(phoneNumber);
            }
            UCSCall.setSpeakerphone(IncomingActivity.this, true);
            UCSCall.startRinging(this, true);
        }
        Log.e(TAG, "initData calledUid: "+calledUid+",calledPhone: "+calledPhone+",userName: "+userName+",phoneNumber: "+phoneNumber+",nickName: "+nickName );
    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.audio_call_endcall: //挂断电话
                Log.e(TAG,"挂断 ...");
                UCSCall.stopRinging(IncomingActivity.this);
                UCSCall.hangUp("");
                handler.sendEmptyMessageDelayed(AUDIO_CONVERSE_CLOSE, 1500);
                break;
            case R.id.audio_call_answer: //接通电话
                Log.e("YZXMSG", "onClick phoneNumber: "+phoneNumber+",remoteUserId: "+remoteUserId );
                Intent intentVideo = new Intent(IncomingActivity.this, VideoConverseActivity.class);
                intentVideo.putExtra("phoneNumber", phoneNumber).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentVideo.putExtra("inCall", true);
                intentVideo.putExtra("nickName", nickName);
                intentVideo.putExtra("callId", callId);
                intentVideo.putExtra("remoteUserId",remoteUserId);
                intentVideo.putExtra("dev_name",dev_name);
                startActivity(intentVideo);
                this.finish();
                break;
        }
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(br);
        UCSCall.stopCallRinging(IncomingActivity.this);
        Log.e(TAG,"audioConverseActivity onDestroy() ...");
        if(sound == 1) {  // 如果系统触摸提示音是开的，前面把它给关系，现在退出页面要把它还原
            Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 1);
            mAudioManager.loadSoundEffects();
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
