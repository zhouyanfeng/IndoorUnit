package com.sanquan.indoorunit.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.app.MainApplication;
import com.sanquan.indoorunit.util.GlobalConfig;
import com.sanquan.indoorunit.util.yzxutils.CameraWindow;
import com.sanquan.indoorunit.util.yzxutils.UIDfineAction;
import com.sanquan.indoorunit.view.YZXDragLinearLayout;
import com.yzx.api.CallType;
import com.yzx.api.RotateType;
import com.yzx.api.UCSCall;
import com.yzx.api.UCSCameraType;
import com.yzx.api.UCSFrameType;
import com.yzx.api.VideoExternFormat;
import com.yzx.tools.FileTools;
import com.yzxtcp.UCSManager;
import com.yzxtcp.listener.OnSendTransRequestListener;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.NetWorkTools;
import com.yzxtcp.tools.tcp.packet.common.UCSTransStock;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VideoConverseActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "VideoConverseActivity";

    private LinearLayout remotelayout;
    private YZXDragLinearLayout locallayout;
    private ImageButton video_call_answer;
    private ImageButton video_call_hangup;
    private ImageButton converse_call_mute;
    private ImageButton converse_call_video;
    private ImageButton converse_call_speaker;
    private ImageButton converse_call_switch;
    private LinearLayout layout_video_function;
    private TextView converse_information;
    private TextView network_information;
    private TextView converse_client;
    private ImageView converse_network;
    private TextView network_tip;
    private LinearLayout ll_video_network_time;
    private TextView converse_time;
    private String phoneNumber;
    public String calledUid;
    public String calledPhone;
    public String nickName;
    private boolean open_headset = false;
    private boolean closeVideo = false;
    private boolean inCall;  // 判断是否是来电信息(true)，默认是去电(false)
    private boolean speakerPhoneState = false;

    private String timer; // 通话时间
    public static String IncomingCallId; // 来电时的callId，作用是防止有些时间挂断电话的信令来了，但是通话界面还没有开启，这个时候在来来电信令，电话就不挂断。
    private boolean incallAnswer = false;
    private static final int VIDIO_CONVERSE_CLOSE = 2; // 关闭界面
    private static final int VIDEO_NETWORK_STATE = 3; // 更新网络状态
    private static final int MSG_REFRESH_VIDEO_TIMER_TEXT = 5;// 刷新视频计时
    private int sound; // 触摸提示音的状态，0：关，1：开
    private boolean bOnCreate = true;// 界面激活是否来自OnCreate
    private AudioManager mAudioManager;
    private boolean bShowNetMsg = false;
    private boolean bConverseClose = false;// 会话界面是否已关闭（挂断后的关闭）
    private volatile boolean error_kickout = false; // 是否被踢线
    private final static int useExternCapture = 0;// 0: not used 1: I420 2:h264   // 这个判断是干嘛用的
    private static int landscape = 0; // do not change this value,it auto check
    // in code //0:portrait 1:landscape

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            Log.e(TAG, "dispatchMessage: msg.what " + msg.what);
            switch (msg.what) {
                case 0:
                    if (closeVideo) { // 本地摄像头已关闭
                        //UCSCall.switchVideoMode(UCSCameraType.REMOTECAMERA);
                        UCSCall.refreshCamera(UCSCameraType.REMOTECAMERA, UCSFrameType.ORIGINAL);
                    } else {
                        converse_call_video.setBackgroundResource(R.drawable.converse_video);
                        if (TextUtils.isEmpty((CharSequence) msg.obj)) {//本端是被叫，已接听
                            UCSCall.refreshCamera(UCSCameraType.ALL, UCSFrameType.ORIGINAL);
                        } else {//界面由后台回到前台
                            UCSCall.refreshCamera(UCSCameraType.BACKGROUNDCAMERA, UCSFrameType.ORIGINAL);
                        }
                    }
                    Log.e(TAG, "Video status refresh end....");
                    break;
                case 1:
                    if (!bConverseClose) {
                        UCSCall.closeCamera(UCSCameraType.BACKGROUNDCAMERA);
                        Log.e(TAG, "Video status close ....");
                    }
                    break;
                case VIDIO_CONVERSE_CLOSE: // 关闭界面
                    bConverseClose = true;
                    IncomingCallId = ""; //关闭界面时 恢复该标志位
                    VideoConverseActivity.this.finish();
                    break;
                case VIDEO_NETWORK_STATE: // 更新网络状态
                    switch (msg.arg1) {
                        /*
                         * 0 nice, very good 1 well, good 2 general 3 poor 4 bad
                         */
                        case 0:
                        case 1:
                            converse_network.setBackgroundResource(R.drawable.video_signal3);
                            break;
                        case 2:
                            converse_network.setBackgroundResource(R.drawable.video_signal2);
                            break;
                        case 3:
                            converse_network.setBackgroundResource(R.drawable.video_signal1);
                            break;
                        case 4:
                            converse_network.setBackgroundResource(R.drawable.video_signal0);
                            break;

                        case 10:
                            // 对端视频处于远程视频模式
                            converse_network.setBackgroundResource(R.drawable.video_signal3);
                            break;
                        default:
                            break;
                    }
                    //网络状态太差时给出提示
                    if (msg.arg1 == 4)
                        network_tip.setText("当前通话网络状况不佳");
                    else
                        network_tip.setText("");
                    //显示实时通话质量数据
                    if (bShowNetMsg) {
                        network_information.setVisibility(View.VISIBLE);
                        //整理数据格式使界面显示更清晰
                        //network_information.setText((String) msg.obj + "\n" + UCSCall.getCpsParamterDebug(VideoConverseActivity.this));
                        if (msg.obj != null)
                            network_information.setText(((String) msg.obj).replace("\n ", "\n").replace("  ", " ").replace(", ", "\n") + "\n\n" + UCSCall.getCpsParamterDebug(VideoConverseActivity.this));
                    }
                    break;
                case MSG_REFRESH_VIDEO_TIMER_TEXT:
                    mHandler.removeMessages(MSG_REFRESH_VIDEO_TIMER_TEXT);
                    if (converse_time != null) {
                        converse_time.setText(timer);
                    }
                    break;
            }
        }
    };
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e(TAG, "onReceive: action: " + intent.getAction());
            if (intent.getAction().equals(UIDfineAction.ACTION_DIAL_STATE)) {
                int state = intent.getIntExtra("state", 0);
                Log.e(TAG, "onReceive VIDEO_CALL_STATE " + state);
                if (UIDfineAction.dialState.keySet().contains(state)) {
                    if (state == 300226) {
                        ll_video_network_time.setVisibility(View.GONE);
                        converse_information.setVisibility(View.VISIBLE);
                    }
                    converse_information.setText(UIDfineAction.dialState.get(state)); // 视频状态的说明
                }
                if (state == UCSCall.HUNGUP_REFUSAL || state == UCSCall.HUNGUP_MYSELF || state == UCSCall.HUNGUP_OTHER
                        || state == UCSCall.HUNGUP_MYSELF_REFUSAL || state == UCSCall.HUNGUP_RTP_TIMEOUT
                        || state == UCSCall.HUNGUP_OTHER_REASON || state == UCSCall.HUNGUP_GROUP) {
                    UCSCall.stopCallRinging(VideoConverseActivity.this);
                    mHandler.sendEmptyMessageDelayed(VIDIO_CONVERSE_CLOSE, 1000);
                } else {
                    if ((state >= 300210 && state <= 300260) && (state != 300221 && state != 300222 && state != 300247)
                            || state == UCSCall.HUNGUP_NOT_ANSWER) {
                        mHandler.sendEmptyMessageDelayed(VIDIO_CONVERSE_CLOSE, 1000);
                    }
                }
                // 本方是主叫 对方正在响铃
                if (!inCall && state == UCSCall.CALL_VOIP_RINGING_180) {
                    UCSCall.refreshCamera(UCSCameraType.LOCALCAMERA, UCSFrameType.ORIGINAL);
                }
                if (state == UCSCall.NOT_NETWORK) {
                    converse_information.setText("当前处于无网络状态");
                    UCSCall.stopRinging(VideoConverseActivity.this);
                    UCSCall.stopCallRinging(VideoConverseActivity.this);
                    mHandler.sendEmptyMessageDelayed(VIDIO_CONVERSE_CLOSE, 1000);
                }
            } else if (intent.getAction().equals(UIDfineAction.ACTION_ANSWER)) {
                //室内机呼叫中心机时必须旋转 不然图像出问题 只要是设计到中心机的 都需要调用该方法，而且呼叫和被呼叫时调用的位置不同
                if (!inCall)
                    UCSCall.videoUpdateLocalRotation(0, 270);
                VideoConverseActivity.this.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                converse_information.setVisibility(View.GONE);
                ll_video_network_time.setVisibility(View.VISIBLE);
                incallAnswer = true;
                UCSCall.setSpeakerphone(VideoConverseActivity.this, true); //扬声器状态
                converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker_down);
                UCSCall.stopCallRinging(VideoConverseActivity.this); //停止播放去电铃声
                UCSCall.stopRinging(VideoConverseActivity.this); //停止播放来电铃声
                locallayout.setVisibility(View.GONE);
                remotelayout.setVisibility(View.VISIBLE);
                converse_call_mute.setVisibility(View.VISIBLE);
                converse_call_video.setVisibility(View.VISIBLE);
                converse_call_speaker.setVisibility(View.VISIBLE);
                converse_call_switch.setVisibility(View.VISIBLE);
                layout_video_function.setVisibility(View.VISIBLE);
                video_call_answer.setVisibility(View.GONE);
                open_headset = true;
                if (!UCSCall.isCameraPreviewStatu(VideoConverseActivity.this)) {  //查询是否开启视频来电预览
                    if (!inCall) {
                        // 本方是主叫，对方已接听
                        UCSCall.switchVideoMode(UCSCameraType.ALL);
                    } else {
                        // 刷新摄像头发送和接收，重要，一定要加这个
                        // mHandler.sendEmptyMessageDelayed(0, 1000);
                        mHandler.sendEmptyMessage(0); // 这里的操作可能是要刷新远程和本地视频画面
                    }
                }

                // 接通电话后按钮变为可用
                converse_call_mute.setClickable(true);
                converse_call_video.setClickable(true);
                converse_call_speaker.setClickable(true);
                converse_call_switch.setClickable(true);
                remotelayout.setClickable(true);
            } else if (intent.getAction().equals(UIDfineAction.ACTION_CALL_TIME)) {
                timer = intent.getStringExtra("timer");
                mHandler.sendEmptyMessage(MSG_REFRESH_VIDEO_TIMER_TEXT);
            } else if (intent.getAction().equals(UIDfineAction.ACTION_NETWORK_STATE)) {
                Log.e(TAG, "onReceive: ACTION_NETWORK_STATE");
                int state = intent.getIntExtra("state", 0);
                String videoMsg = intent.getStringExtra("videomsg");
                Message msg = mHandler.obtainMessage();
                msg.what = VIDEO_NETWORK_STATE;
                msg.arg1 = state;
                msg.obj = videoMsg;
                mHandler.sendMessageDelayed(msg, 0);
                // CustomLog.v("-----------------------"+state);
            } else if (intent.getAction().equals("android.intent.action.HEADSET_PLUG")) {
                // 发现个别手机会接通电话前收到这个广播并把扬声器打开了，所以open_headset作为判断必须接通后再接收耳机插拔广播才有效
                if (intent.getIntExtra("state", 0) == 1 && open_headset) {
                    Log.e(TAG, "Speaker false");
                    converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker);
                    speakerPhoneState = UCSCall.isSpeakerphoneOn(VideoConverseActivity.this);
                    UCSCall.setSpeakerphone(VideoConverseActivity.this, false);
                } else if (intent.getIntExtra("state", 0) == 0 && open_headset) {
                    Log.e(TAG, "headset unplug");
                    if (speakerPhoneState) {
                        CustomLog.e("Speaker true");
                        converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker_down);
                        UCSCall.setSpeakerphone(VideoConverseActivity.this, true);
                    }
                }
            } else if (intent.getAction().equals(UIDfineAction.ACTION_NET_ERROR_KICKOUT)) {
                // 踢线广播
                error_kickout = true;
            }
        }
    };


    private TextView tvRemoteDevId;
    private String remoteUserId;
    private ImageView imgRemoteOpenDoor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomLog.v("1 onCreate callback....");
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            landscape = 0; // 竖屏
        } else {
            landscape = 1; // 横屏
        }
        try {
            // 如果系统触摸音是关的就不用管，开的就把它给关掉，因为在个别手机上有可能会影响音质
            mAudioManager = ((AudioManager) getSystemService(Context.AUDIO_SERVICE));
            sound = Settings.System.getInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED);
            if (sound == 1) {
                Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
                mAudioManager.unloadSoundEffects();
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "SettingNotFound SOUND_EFFECTS_ENABLED ...");
        }

        // 设置窗体始终点亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_converse);


        initview();
        initData();
        initListener();
        IntentFilter ift = new IntentFilter();
        ift.addAction(UIDfineAction.ACTION_DIAL_STATE);
        ift.addAction(UIDfineAction.ACTION_ANSWER);
        ift.addAction(UIDfineAction.ACTION_CALL_TIME);
        ift.addAction(UIDfineAction.ACTION_NETWORK_STATE);
        ift.addAction(UIDfineAction.ACTION_NET_ERROR_KICKOUT);
        ift.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(br, ift);


        UCSCall.initCameraConfig(VideoConverseActivity.this, remotelayout, locallayout);
        ll_video_network_time.setVisibility(View.GONE);
        if (useExternCapture > 0) {
            UCSCall.setVideoExternCapture(VideoExternFormat.h264, true);
        }
        if (inCall) {// 来电
            // 判断网络类型，2G时提示一下
            int netstate = NetWorkTools.getCurrentNetWorkType(this);
            if (netstate == NetWorkTools.NETWORK_EDGE)
                Toast.makeText(this, "网络状态差", Toast.LENGTH_SHORT).show();

            video_call_hangup.setVisibility(View.VISIBLE);
            video_call_answer.setVisibility(View.GONE);
            converse_information.setText("视频电话来电");
            UCSCall.setSpeakerphone(VideoConverseActivity.this, true);
            Log.e(TAG, "IncomingCallId = " + IncomingCallId + ",callId = " + getIntent().getStringExtra("callId"));
            if (getIntent().hasExtra("callId")) {
                if (getIntent().getStringExtra("callId").equals(IncomingCallId)) {  //这里是判断对方打过来之后又挂断的情况
                    converse_information.setVisibility(View.VISIBLE);
                    converse_information.setText("对方挂断电话");
                    UCSCall.stopRinging(VideoConverseActivity.this);
                    mHandler.sendEmptyMessageDelayed(VIDIO_CONVERSE_CLOSE, 1000);
                    return;
                }
            }
        } else {
            CameraWindow.show(VideoConverseActivity.this);
            video_call_answer.setVisibility(View.GONE);
            video_call_hangup.setVisibility(View.VISIBLE);
            layout_video_function.setVisibility(View.INVISIBLE);
            converse_call_switch.setVisibility(View.INVISIBLE);
            dial();
            converse_information.setText("正在呼叫");
            locallayout.setVisibility(View.GONE); //室内机没有本地照相机 所以把本地的预览给关掉
            remotelayout.setVisibility(View.VISIBLE);
        }
        // 默认一开始使用前摄像头 0：后摄像头,1:前摄像头
        if (UCSCall.getCameraNum() > 1) {
            UCSCall.switchCameraDevice(1, RotateType.DEFAULT);
        } else {
            UCSCall.switchCameraDevice(0, RotateType.DEFAULT);
        }
        // 通话接通前按钮不可用
        converse_call_mute.setClickable(false);
        converse_call_video.setClickable(false);
        converse_call_speaker.setClickable(false);
        // converse_call_switch.setClickable(false);
        remotelayout.setClickable(false);

        if (inCall) {
            video_call_answer.performClick();
        }
    }

    private void dial() {
        if (phoneNumber != null && phoneNumber.length() > 0) {
            UCSCall.setSpeakerphone(VideoConverseActivity.this, false);
            UCSCall.dial(CallType.VIDEO, phoneNumber, "");
        }
        UCSManager.sendTransData(phoneNumber, new UCSTransStock() {
            @Override
            public String onTranslate() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("dev_id","室内机");
                    jsonObject.put("dev_name",GlobalConfig.getGlobalConfig().getName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject.toString();
            }
        }, new OnSendTransRequestListener() {
            @Override
            public void onSuccess(String s, String s1) {

            }

            @Override
            public void onError(int i, String s) {

                Log.e("YZXMSG", "onError OnSendTransRequestListener i:"+i+",s:"+s);
            }
        });
    }


    private void initview() {
        tvRemoteDevId = findViewById(R.id.tv_remote_open);
        imgRemoteOpenDoor = findViewById(R.id.img_remote_open_door);
        converse_client = (TextView) findViewById(R.id.converse_client);
        converse_information = (TextView) findViewById(R.id.converse_information);
        network_information = (TextView) findViewById(R.id.network_information);
        ll_video_network_time = (LinearLayout) findViewById(R.id.ll_video_network_time);
        converse_network = (ImageView) findViewById(R.id.converse_network);
        network_tip = (TextView) findViewById(R.id.network_tip);
        converse_time = (TextView) findViewById(R.id.converse_time);
        converse_call_switch = (ImageButton) findViewById(R.id.converse_call_switch);
        remotelayout = (LinearLayout) findViewById(R.id.remotelayout);
        locallayout = (YZXDragLinearLayout) findViewById(R.id.locallayout);
        converse_call_mute = (ImageButton) findViewById(R.id.converse_call_mute);
        converse_call_video = (ImageButton) findViewById(R.id.converse_call_video);
        converse_call_speaker = (ImageButton) findViewById(R.id.converse_call_speaker);
        video_call_answer = (ImageButton) findViewById(R.id.video_call_answer);
        video_call_hangup = (ImageButton) findViewById(R.id.video_call_hangup);
        layout_video_function = (LinearLayout) findViewById(R.id.ll_video_function);

        //720P时本地预览的宽高设置成16比9
        if (getSharedPreferences("YZX_DEMO_DEFAULT", 0).getBoolean("YZX_720P", false)) {
            RelativeLayout.LayoutParams para = (RelativeLayout.LayoutParams) locallayout.getLayoutParams();
            para.height = para.width * 16 / 9;
            locallayout.setLayoutParams(para);
        }

    }

    /**
     * @return void 返回类型
     * @Description 初始化数据
     * @date 2015-12-15 下午2:57:53
     * @author xhb
     */
    private void initData() {
        // 判断是否是来电信息，默认是去电，（来电信息是由ConnectionService中的onIncomingCall回调中发送广播，开启通话界面，inCall为true）
        if (getIntent().hasExtra("inCall")) {
            inCall = getIntent().getBooleanExtra("inCall", false);
        }
        // 获得要拨打的号码，智能拨打和免费通话：phoneNumber代表ClientID，直拨和回拨代表ClientID绑定的手机号码
        if (getIntent().hasExtra("nickName")) { //这个是门禁机的名字
            nickName = getIntent().getStringExtra("nickName");
        }
        if (getIntent().hasExtra("phoneNumber")) {
            phoneNumber = getIntent().getStringExtra("phoneNumber");
        }


        if (nickName != null && !"".equals(nickName)) {
            converse_client.setText(nickName);

        } else {
            converse_client.setText(phoneNumber);
        }

        remoteUserId = getIntent().getStringExtra("remoteUserId");
        if (remoteUserId != null) {
            tvRemoteDevId.setText(remoteUserId);
            imgRemoteOpenDoor.setImageResource(R.mipmap.remote_open_door);
        }
        String dev_name = getIntent().getStringExtra("dev_name");
        if (dev_name != null) {
            tvRemoteDevId.setText(dev_name);
            imgRemoteOpenDoor.setImageResource(R.mipmap.remote_open_door);
        }
    }

    private void initListener() {
        converse_call_mute.setOnClickListener(this); // 静音
        converse_call_video.setOnClickListener(this); // 摄像头开关
        converse_call_speaker.setOnClickListener(this); // 扬声器
        converse_call_switch.setOnClickListener(this); // 切换摄像头
        video_call_answer.setOnClickListener(this); // 接通
        video_call_hangup.setOnClickListener(this); // 挂断
        remotelayout.setOnClickListener(this);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locallayout.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        locallayout.setLayoutParams(layoutParams);
        locallayout.setOnSingleTouchListener(new YZXDragLinearLayout.IOnSingleTouchListener() {

            @Override
            public void onSingleTouch() {
                // TODO Auto-generated method stub
                if (bShowNetMsg) { // 当前为打开状态则单击关闭
                    bShowNetMsg = !bShowNetMsg;
                    network_information.setText("");
                    network_information.setVisibility(View.GONE);
                    clickList.clear();
                    return;
                }

                clickList.add(SystemClock.uptimeMillis());
                if (clickList.size() == 5) {
                    // 5次连击打开
                    if (clickList.get(clickList.size() - 1) - clickList.get(0) < 2000) {
                        clickList.clear();
                        bShowNetMsg = true;
                        network_information.setText("");
                        network_information.setVisibility(View.VISIBLE);
                    } else {
                        Long tmp = clickList.get(clickList.size() - 1);
                        clickList.clear();
                        clickList.add(tmp);
                    }
                }
            }
        });
    }


    ArrayList<Long> clickList = new ArrayList<Long>(); // 用于打开网络信息的连击动作时间列表
    boolean isOpenBr = true;


    /**
     * @author zhangbin
     * @2016-1-14 @
     * @descript:切换视频通过中视图的显示
     */
    private void toggleVideoView() {
        // 切换显示视频功能按键 显示视频切换按键 挂机按键
        if (layout_video_function.getVisibility() == View.VISIBLE) {
            layout_video_function.setVisibility(View.INVISIBLE);
            converse_call_switch.setVisibility(View.INVISIBLE);
            video_call_hangup.setVisibility(View.INVISIBLE);
        } else {
            layout_video_function.setVisibility(View.VISIBLE);
            converse_call_switch.setVisibility(View.VISIBLE);
            video_call_hangup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        CameraWindow.dismiss();

        if (sound == 1) { // 如果系统触摸提示音是开的，前面把它给关系，现在退出页面要把它还原
            Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 1);
            mAudioManager.loadSoundEffects();
        }

        // mHandler.sendEmptyMessageDelayed(1, 100);//
        // 挂断时不调用关闭摄像头操作,因为挂断时会默认把摄像头关闭
        UCSCall.stopRinging(VideoConverseActivity.this);
        UCSCall.stopCallRinging(VideoConverseActivity.this);
        unregisterReceiver(br);
        // mSensor.unregisterListener(lsn);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause: ");
        if (!error_kickout) { // 如果是踢线，则不需要关闭视频，因为hangup默认就是关闭视频，防止出现anr异常
            // 直接关闭视频，不再延时
            mHandler.sendEmptyMessage(1);
            // mSensor.unregisterListener(lsn);
        }
        super.onPause();
    }


    @Override
    protected void onResume() {
        Log.e(TAG, "onResume: " + bOnCreate);
        if (!bOnCreate) {
            Message msg = mHandler.obtainMessage();
            msg.what = 0;
            msg.obj = "onResume";
            mHandler.sendMessage(msg);
        } else {
            bOnCreate = false;
        }
        super.onResume();

    }

    @Override
    protected void onRestart() {
        // mHandler.sendEmptyMessageDelayed(0, 1000);
        Log.e(TAG, "1 onRestart callback....");
        super.onRestart();
    }


    @Override
    public void finish() {
        super.finish();
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

    // 远程开门
    public void remoteOpenDoor(View view) {
        boolean isFastClick = isFastClick(5000);
        if (!isFastClick) {
            imgRemoteOpenDoor.setImageResource(R.mipmap.clicked_open_door);
            Log.e("YZXMSG", "remoteOpenDoor: remoteUserId " + remoteUserId);
            UCSManager.sendTransData(remoteUserId, new UCSTransStock() {
                @Override
                public String onTranslate() {
                    return GlobalConfig.getGlobalConfig().getMacAddr();
                }
            }, new OnSendTransRequestListener() {
                @Override
                public void onSuccess(String s, String s1) {
                    Log.i("YZXMSG", "onSuccess OnSendTransRequestListener s:" + s + "s1:" + s1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                imgRemoteOpenDoor.setImageResource(R.mipmap.remote_open_door);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(VideoConverseActivity.this, R.string.open_door_success, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(int i, String s) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                imgRemoteOpenDoor.setImageResource(R.mipmap.remote_open_door);
                                Toast.makeText(VideoConverseActivity.this, R.string.open_door_failed, Toast.LENGTH_SHORT).show();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Log.e("YZXMSG", "onError OnSendTransRequestListener i:" + i + ",s:" + s);
                }
            });
        }
    }

    private static long lastClickTime = 0;

    private boolean isFastClick(long ClickIntervalTime) {
        long ClickingTime = System.currentTimeMillis();
        Log.e("click", "isFastClick lastClickTime: " + lastClickTime + ",ClickingTime: " + ClickingTime + "," + (ClickingTime - lastClickTime));
        if (ClickingTime - lastClickTime < ClickIntervalTime) {
            return true;
        }
        lastClickTime = ClickingTime;
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.converse_call_mute: // 静音
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                String remoteName = "remote_" + "15219483291" + "_" + format.format(new Date()) + ".jpg";
                UCSCall.videoCapture(UCSCameraType.REMOTECAMERA, remoteName, FileTools.getSdCardFilePath());
                if (UCSCall.isMicMute()) {
                    converse_call_mute.setBackgroundResource(R.drawable.converse_mute);
                } else {
                    converse_call_mute.setBackgroundResource(R.drawable.converse_mute_down);
                }
                UCSCall.setMicMute(!UCSCall.isMicMute());
                break;
            case R.id.converse_call_video: // 摄像头开关
                if (closeVideo) {
                    // 开启自己的摄像头
                    closeVideo = false;
                    converse_call_video.setBackgroundResource(R.drawable.converse_video);
                    UCSCall.switchVideoMode(UCSCameraType.ALL);
                } else {
                    // 关闭自己的摄像头
                    closeVideo = true;
                    converse_call_video.setBackgroundResource(R.drawable.converse_video_down);
                    UCSCall.switchVideoMode(UCSCameraType.REMOTECAMERA);
                }
                break;
            case R.id.converse_call_speaker: // 扬声器
                if (UCSCall.isSpeakerphoneOn(VideoConverseActivity.this)) {
                    converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker);
                } else {
                    converse_call_speaker.setBackgroundResource(R.drawable.converse_speaker_down);
                }
                UCSCall.setSpeakerphone(VideoConverseActivity.this, !UCSCall.isSpeakerphoneOn(VideoConverseActivity.this));
                break;
            case R.id.converse_call_switch: // 切换摄像头
                Log.e(TAG, "当前摄像头：" + UCSCall.getCurrentCameraIndex());
                if (UCSCall.getCurrentCameraIndex() == 0) { // 横屏状态是RETATE_0
                    if (UCSCall.getCameraNum() > 1) {
                        UCSCall.switchCameraDevice(1, RotateType.DEFAULT);
                    }
                } else {
                    UCSCall.switchCameraDevice(0, RotateType.DEFAULT);
                }
                break;
            case R.id.video_call_answer: // 接通
                Log.e(TAG, "接通电话");
                converse_information.setVisibility(View.GONE);
                ll_video_network_time.setVisibility(View.VISIBLE);
                incallAnswer = true;
                UCSCall.stopRinging(VideoConverseActivity.this); //停止播放来电铃声
                UCSCall.answer("");  //接听来电
//                UCSCall.videoUpdateLocalRotation(0,90);
                UCSCall.setSpeakerphone(VideoConverseActivity.this, false);
                break;
            case R.id.video_call_hangup: // 挂断
                Log.e(TAG, "界面挂断电话");
                UCSCall.stopRinging(VideoConverseActivity.this);
                UCSCall.hangUp("");
                sendBroadcast(new Intent(UIDfineAction.ACTION_CALL_STOP_RECALL_TIMER).putExtra("isStopRecall", true));
                mHandler.sendEmptyMessageDelayed(VIDIO_CONVERSE_CLOSE, 1000);
                break;
            case R.id.remotelayout:
                toggleVideoView();
                break;

            case R.id.img_rotate:
                if (isRotate) {
                    UCSCall.videoUpdateLocalRotation(0, 270);
                } else {
                    UCSCall.videoUpdateLocalRotation(0, 90);
                }
                isRotate = !isRotate;
                break;
        }
    }


    private boolean isRotate = true;
}
