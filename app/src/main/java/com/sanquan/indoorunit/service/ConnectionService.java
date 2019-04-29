package com.sanquan.indoorunit.service;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;


import com.google.gson.Gson;
import com.sanquan.indoorunit.activity.IncomingActivity;
import com.sanquan.indoorunit.activity.LoginActivity;
import com.sanquan.indoorunit.app.MainApplication;
import com.sanquan.indoorunit.bean.LoginBean;
import com.sanquan.indoorunit.util.AESUtil;
import com.sanquan.indoorunit.util.GlobalConfig;
import com.sanquan.indoorunit.util.yzxutils.DfineAction;
import com.sanquan.indoorunit.util.yzxutils.UIDfineAction;
import com.sanquan.indoorunit.activity.VideoConverseActivity;
import com.yzx.api.CallType;
import com.yzx.api.UCSCall;
import com.yzx.api.UCSCameraType;
import com.yzx.api.UCSService;

import com.yzx.listenerInterface.CallStateListener;
import com.yzx.listenerInterface.ConnectionListener;

import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;
import com.yzxtcp.tools.CustomLog;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 后台服务/连接控制器
 */
public class ConnectionService extends Service implements ConnectionListener,CallStateListener, ILoginListener {

	private final static int CMD_REBOOT_DEV = 8;
	private final static int CMD_DISABLE_DEV = 9;

    private final String playFileName = "/sdcard/playout_t.pcm";
	private FileOutputStream fosPlay = null;
	private byte[] testData;
	private int dataLen = 0;
	private int readIndex = 0;
	// 是否是重呼，默认是false不重呼，true为重呼
	private AtomicBoolean isRecall = new AtomicBoolean(false);
	// 重呼超时定时器
	private Timer recallTimer;
	// 重呼间隔时间
	private Timer recallIntervalTimer;
	// 呼叫类型
	private int callType;
	// 呼叫clientId或者手机号码
	private String uid;
	// 挂断原因码
	private int hangupReason;

	private final static int GRAY_SERVICE_ID = 1001;
	private MqttClient mClient;
	private boolean stop = false;
	public static final int CHECK_MQTT = 100;

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case CHECK_MQTT:
					new Thread(new Runnable() {
						@Override
						public void run() {
							if (mClient != null && !mClient.isConnected()){
								mClient = initClient(serverUri, clientId, mqttCallback, initMqttConnectionOptions(), topics);
							}
							handler.sendEmptyMessageDelayed(CHECK_MQTT,1000*15);
						}
					}).start();
					break;
			}
		}
	};
	private String clientId,serverUri,topics;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		super.onCreate();

		//添加连接监听器
		UCSService.addConnectionListener(ConnectionService.this);
		//添加电话监听器
		UCSCall.addCallStateListener(ConnectionService.this);
		//关闭未接听时视频预览
		UCSCall.setCameraPreViewStatu(ConnectionService.this,false); // 开启了视频预览，就不能处理拨打视频电话进行预览图片上传操作
		// 控制是否开启外部音频传输 true:开启；false：不开启
		UCSCall.setExtAudioTransEnable(this,false);
		//初始化SDK
		UCSService.init(this,true);
		//UCSService.init(this,false);//关掉系统日志打印, modified by zhj 20160121
		//初始化action动作
		UIDfineAction.initAction(ConnectionService.this.getPackageName());

		IntentFilter ift = new IntentFilter();
        ift.addAction(UIDfineAction.ACTION_DIAL);
        ift.addAction(UIDfineAction.ACTION_CALL_STOP_RECALL_TIMER);
        registerReceiver(br, ift);
        UCSManager.setTransAckData("2016-06-24 13:44:42: TRACE_CALL_BACK:  summary:TraceLog   detail:CALL_LOG:CALLID:67894056190787oJnYa2016-06-24 13:44:42: TRACE_CALL_BACK:  summary:TraceLog   detail:CALL_LOG:CALLID:67894056190787oJnYa2016-06-24 13:44:42: TRACE_CALL_BACK:  summary:TraceLog   detail:CALL_LOG:CALLID:67894056190787oJnYa2016-06-24 13:44:42: TRACE_CALL_BACK:  summary:TraceLog   detail:CALL_LOG:CALLID:67894056190787oJnYa2016-06-24 13:44:42: TRACE_CALL_BACK:  summary:TraceLog   detail:CALL_LOG:CALLID:67894056190787oJnYa");
        init();
        handler.sendEmptyMessageDelayed(CHECK_MQTT,1000*60);
	}


	private MqttCallbackExtended mqttCallback = new MqttCallbackExtended() {
		@Override
		public void connectComplete(boolean reconnect, String serverURI) {
			Log.i(TAG, "connect Complete. reconnect:" + reconnect);
		}

		@Override
		public void connectionLost(Throwable cause) {
			cause.printStackTrace();
			Log.i(TAG, "connection Lost " + cause.getMessage() + "," + cause.getLocalizedMessage());
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			String result = new String(message.getPayload());
			Log.d(TAG, "MSG:" + AESUtil.aesDecrypt(result,GlobalConfig.getGlobalConfig().getDev_secret()));
			handleMessage(new String(message.getPayload()));

		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
			Log.i(TAG, "delivery Complete ");//即服务器成功delivery消息
		}
	};

	private void handleMessage(String msg){
		if (msg == null || msg.isEmpty())
			return;
		stopSelf();
		ArrayList<Activity> activityList = MainApplication.getInstance().getActivityList();
		for (Activity activity : activityList) {
			activity.finish();
		}
		Intent intent = new Intent(this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		Log.e(TAG, "handleMessage: 收到消息" );
//		JSONObject jsonObj = null;
//		Log.e(TAG, "handleMessage msg: "+msg );
//		try {
//			jsonObj = new JSONObject(msg);
//		//	int cmd = jsonObj.getInt("cmd");
//
////			switch (cmd) {
////				case CMD_REBOOT_DEV:
////
////					break;
////				case CMD_DISABLE_DEV:   //删除设备，禁用启用设备
//////					if (mqttEventListener != null) {//此处crash，启动服务会重启app
//////						mqttEventListener.onDisableDevice();
//////					}
////                    stopSelf();
////                    ArrayList<Activity> activityList = MainApplication.getInstance().getActivityList();
////                    for (Activity activity : activityList) {
////                        activity.finish();
////                    }
////                    Intent intent = new Intent(this, LoginActivity.class);
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                    startActivity(intent);
////                    Log.e(TAG, "handleMessage: 收到消息" );
////					break;
////			}
//		}catch (JSONException e) {
//			e.printStackTrace();
//		}
	}

	public void init() {
		LoginBean.InfoBean infoBean = new Gson().fromJson(GlobalConfig.getGlobalConfig().getLoginJson(), LoginBean.InfoBean.class);
		clientId = GlobalConfig.getGlobalConfig().getMacAddr();
		serverUri = infoBean.getMqtt_server();
		topics = infoBean.getMqtt_topic();
		Log.d(TAG, "run: mqtt started server url:" + serverUri + ",topic:" + topics);
		new Thread(new Runnable() {
			@Override
			public void run() {
				mClient = initClient(serverUri, clientId, mqttCallback, initMqttConnectionOptions(), topics);
			}
		}).start();
	}

	private MqttClient initClient(String serverURI, String clientId, MqttCallback callback, MqttConnectOptions options, String subscribeTopics) {
		MqttClient client = null;
		try {
			MemoryPersistence persistence = new MemoryPersistence();
			client = new MqttClient(serverURI, clientId, persistence);
			client.setCallback(callback);//设置回调函数
			client.connect(options);//连接broker
			client.subscribe(subscribeTopics, 1);//设置监听的topic
		} catch (MqttException e) {
			e.printStackTrace();
			Log.e(TAG, "failed to connect mqtt e: " + e.getMessage());
			Log.e(TAG, "failed to connect mqtt:" + e.getMessage());
		}
		return client;
	}

	private MqttConnectOptions initMqttConnectionOptions() {
		MqttConnectOptions mOptions = new MqttConnectOptions();
		mOptions.setAutomaticReconnect(false);//断开后，是否自动连接
		mOptions.setCleanSession(true);//是否清空客户端的连接记录。若为true，则断开后，broker将自动清除该客户端连接信息
		mOptions.setConnectionTimeout(60);//设置超时时间，单位为秒
		mOptions.setUserName("admin");//设置用户名。跟Client ID不同。用户名可以看做权限等级
		mOptions.setPassword("sanquanvideo".toCharArray());//设置登录密码
		mOptions.setKeepAliveInterval(60);//心跳时间，单位为秒。即多长时间确认一次Client端是否在线
		//mOptions.setMaxInflight(10);//允许同时发送几条消息（未收到broker确认信息）
		//mOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);//选择MQTT版本
		return mOptions;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(Build.VERSION.SDK_INT < 18) {
			startForeground(GRAY_SERVICE_ID, new Notification());	// API < 18 ，此方法能有效隐藏Notification上的图标
		} else {
			Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private static final String TAG = "ConnectionService";
	private BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e(TAG, "onReceive: action "+intent.getAction());
			if(intent.getAction().equals(UIDfineAction.ACTION_DIAL)){
				callType = intent.getIntExtra("type", 1);
				uid = intent.getStringExtra(UIDfineAction.CALL_UID);
				String phone = intent.getStringExtra(UIDfineAction.CALL_PHONE);
				if (phone == null || phone.length() == 0) {
				    //整合版本账号即手机号
				    phone = uid;
				}
				Log.e(TAG, "onReceive: DfineAction.TAG_TCP, \"》》》》》》》》》》》  ... "+callType);
				final String recall_timer = getSharedPreferences("YZX_DEMO_DEFAULT", 0).getString("recall_timer", "60");
				Log.e(TAG, "onReceive recall_timer: "+recall_timer );
				boolean avPreviewImg = getSharedPreferences("YZX_DEMO_DEFAULT", 0).getBoolean("YZX_PREVIEW_IMG", false);
				CustomLog.v("previewImg:" + avPreviewImg);
				switch(callType){
				case 1:	// 免费
					dial(CallType.VOIP, uid, "", Integer.valueOf(recall_timer) * 1000);
					break;
				case 2:	// 直拨
					dial(CallType.DIRECT, phone, "", Integer.valueOf(recall_timer) * 1000);
					break;
				case 3: // 视频

						dial(CallType.VIDEO, uid, "", Integer.valueOf(recall_timer) * 1000);

					break;

				}
			} else if(intent.getAction().equals(UIDfineAction.ACTION_CALL_STOP_RECALL_TIMER)) {
				// 停止重呼定时器
				boolean isStopRecall = intent.getBooleanExtra("isStopRecall", true);
				if(isStopRecall) {
					stopRecallTimer();
				}
			}
		}
	};



	/**
	 * @Description 拨打电话
	 * @param callType	拨打类型
	 * @param calledNumner	用户clientId或者手机号码
	 * @param userData	拨打电话时的透传数据
	 * @param timeoutSec	重呼时间
	 * @date 2016-9-6 上午9:55:22
	 * @author xhb
	 * @return void    返回类型
	 */
	private void dial(CallType callType, String calledNumner, String userData, int timeoutSec) {
		if(callType == CallType.DIRECT) {	// 直拨就不进行重呼
			UCSCall.dial(callType, calledNumner, "");
		} else {	// voip音视频进行重呼工作
			boolean avRecall = getSharedPreferences("YZX_DEMO_DEFAULT", 0).getBoolean("YZX_AVDRECALL", true);
			if(false) {
				if(timeoutSec >= 90000) { // 最大重呼时间90秒
					timeoutSec = 90000;
				} else if(timeoutSec <= 30000) { // 最小重呼时间30秒
					timeoutSec = 30000;
				}
				startRecallTimer(timeoutSec);
				isRecall.set(true); // 设置进行重呼
			}
			UCSCall.dial(callType, calledNumner, "");
		}
	}

	/**
	 * @Description 开启重呼超时定时器，超过超时时间就挂断
	 * @param timeoutSec	超时时间
	 * @date 2016-9-6 上午10:14:07
	 * @author xhb
	 * @return void    返回类型
	 */
	private void startRecallTimer(int timeoutSec) {
		CustomLog.v("startRecallTimer ... ");
		stopRecallTimer();
		if(recallTimer == null) {
			recallTimer = new Timer();
		}
		recallTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				isRecall.set(false); // 重呼结束恢复默认值
				stopRecallIntervalTimer();
				stopRecallTimer();
				sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", hangupReason));
				UCSCall.hangUp(""); // 挂断电话
			}
		}, timeoutSec);
	}

	private void stopRecallTimer() {
		if(recallTimer != null) {
			CustomLog.v("stopRecallTimer ...");
			isRecall.set(false);
			stopRecallIntervalTimer();
			recallTimer.cancel();
			recallTimer = null;
		}
	}

	/**
	 * @Description 重呼间隔时间，设置为3秒
	 * @date 2016-9-6 上午10:29:19
	 * @author xhb
	 * @return void    返回类型
	 */
	private void startRecallIntervalTimer() {
		CustomLog.v("startRecallIntervalTimer ...");
		stopRecallIntervalTimer();
		if(recallIntervalTimer == null) {
			recallIntervalTimer = new Timer();
		}
		recallIntervalTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				switch (callType) {
				case 1: // 音频
					UCSCall.dial(CallType.VOIP, uid, "");
					break;
				case 3:	// 视频
					UCSCall.dial(CallType.VIDEO, uid, "");
					break;
				}
			}
		}, 3000);
	}

	private void stopRecallIntervalTimer() {
		if(recallIntervalTimer != null) {
			CustomLog.v("stopRecallIntervalTimer ...");
			recallIntervalTimer.cancel();
			recallIntervalTimer = null;
		}
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy: 服务终止");
		CustomLog.i(DfineAction.TAG_TCP,"onDestroy ... ");
		handler.removeMessages(CHECK_MQTT);
		unregisterReceiver(br);
		//断开云联接
        UCSService.uninit(); // 注释这个是因为如果多次在后台操作 启用和禁用 禁用时调用改方法，导致崩溃Receiver not registered 是云之讯内部的错误 ，但是不知道注释了会不会对下次链接有影响
		android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy();
	}

	//连接失败或断线回调
	@Override
	public void onConnectionFailed(UcsReason reason) {
		CustomLog.i(DfineAction.TAG_TCP,"CONNECTION_FAILED:"+reason.getReason());
		if(reason.getMsg().length() > 0){
			CustomLog.i(DfineAction.TAG_TCP,"CONNECTION_FAILED:"+reason.getMsg());
		}
		if(reason.getReason() == 300207) {	// 踢线
			sendBroadcast(new Intent(UIDfineAction.ACTION_NET_ERROR_KICKOUT));
		}
	}

	//连接成功回调
	@Override
	public void onConnectionSuccessful() {
		CustomLog.v("connetion successful .... ");

		//重新启动后720p默认未打开，登录成功后恢复上次的设置状态
		boolean b720p = getSharedPreferences("YZX_DEMO_DEFAULT", 0).getBoolean("YZX_720P", false);
		UCSCall.set720pEnable(b720p);
	}

	private int secondDuration = 0;//通话持续时间：秒
	private int minuteDuration = 0;//通话持续时间：分
	private int hourDuration = 0;//通话持续时间：时
	private Timer timer = null;
	/**
	 * 通话走时
	 * @author: xiaozhenhua
	 * @data:2014-6-24 上午10:19:56
	 */
	public void startCallTimer(){
		stopCallTimer();
		if(timer == null){
			timer = new Timer();
		}
		secondDuration = 0; //秒
		minuteDuration = 0; //分
		hourDuration = 0;   //时
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				StringBuffer timer = new StringBuffer();
				secondDuration++;
				if (secondDuration >= 60) {
					minuteDuration++;
					secondDuration = 0;
				}
				if (minuteDuration >= 60) {
					hourDuration++;
					minuteDuration = 0;
				}
				if (hourDuration != 0) {
					if (hourDuration < 10) {
						timer.append(0);
					}
					timer.append(hourDuration);
					timer.append(":");
				}
				if (minuteDuration < 10) {
					timer.append(0);
				}
				timer.append(minuteDuration);
				timer.append(":");
				if (secondDuration < 10) {
					timer.append(0);
				}
				timer.append(secondDuration);
//				CustomLog.i(DfineAction.TAG_TCP,"timer:"+timer.toString());
				sendBroadcast(new Intent(UIDfineAction.ACTION_CALL_TIME).putExtra("callduration", hourDuration * 3600 + minuteDuration * 60 + secondDuration).putExtra("timer", timer.toString()));
			}
		}, 0, 1000);
	}

	public void stopCallTimer(){
		if (timer != null){
			CustomLog.i(DfineAction.TAG_TCP,"cancel() timer");
			timer.cancel();
			timer=null;
		}
	}


	//对方正在响铃回调
	@Override
	public void onAlerting(String arg0) {
		CustomLog.i(DfineAction.TAG_TCP,"onAlerting CURRENT_ID:"+arg0);
		stopRecallTimer();
		sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", UCSCall.CALL_VOIP_RINGING_180));
	}

	//对方接通回调
	@Override
	public void onAnswer(String callId) {
		CustomLog.i(DfineAction.TAG_TCP,"onAnswer CURRENT_ID:"+callId);
		stopRecallTimer();
		sendBroadcast(new Intent(UIDfineAction.ACTION_ANSWER).putExtra("callId", callId));
		startCallTimer();
	}

	//拨打失败回调，请打印出错误码reason.getReason()，官网查询错误码含义
	@Override
	public void onDialFailed(String arg0, UcsReason reason) {
		CustomLog.i("onDialFailed CURRENT_ID:"+arg0+"          SERVICE:"+reason.getReason()+"   MSG:"+reason.getMsg());
		voipSwitch(reason);
	}

	private void voipSwitch(UcsReason reason){
		stopCallTimer();
		stopRecallIntervalTimer();
		hangupReason = reason.getReason();
		switch(reason.getReason()){
		case 300006:	// 被叫号码为空
		case 300210:	// 服务器错误
		case 300211:	// 余额不足
		case 300212:	// 对方正忙
		case 300213:	// 对方拒绝接听
		case 300215: 	// 被叫号码错误
		case 300244: 	// 呼叫ID不存在
		case 300245: 	// 用户ID不存在
		case 300216:	// 主叫账户被冻结
		case 300217:	// 被叫账户被冻结
		case 300218:	// 主叫账户过期
		case 300219:	// 不能拨打自己绑定号码
		case 300220:	// 被叫无应答(应答超时)
		case 300222:	// 转直拨
		case 300223:	// 鉴权失败(TCP未认证)
		case 300224:	// 其他原因错误
		case 300225:	// 自己挂断电话
		case 300226:	// 对方挂断电话
		case 300267:	// 2G时不允许拨打电话
		case 300248:	// 自己拒绝
		case 300249:	// 不支持视频电话
		case 300318:	// 无网络
		case 300227:	// RTP超时电话被挂断
		case 300228:	// 其他原因电话被挂断
		case 300250:	// 呼叫失败（频繁呼叫已被列入黑名单）
		case 300252:	// 消息头解析错误
		case 300253:	// 消息体解析错误
		case 300254:	// 会话已存在
		case 300255:	// 未知错误
		case 300256:	// TCP异常挂断
		case 300257:	// 落地线路无法接通
		case 300258:	// 网络类型不支持
		case 300260:	// 同振呼叫 被叫号码超过五个
		case 300261:	// 同振挂断
			stopRecallTimer();
			sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", reason.getReason()));
			break;
		case 300214: 	// 被叫号码不在线
		case 300221:	// 对方无人应答
		case 300259:	// 信令超时
		case 300251:	// 消息路由不可达
			if(isRecall.get()) {
				// TODO 要进行重呼
				startRecallIntervalTimer();
			} else {
				sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", reason.getReason()));
			}
            break;
		default: // 第三方AS服务器返回的自定义错误码
			stopRecallTimer();
			if(reason.getReason() >= 10000 && reason.getReason() <= 20000){//透传错误码
				CustomLog.i(DfineAction.TAG_TCP,"KC_REASON:"+reason.getReason());
				sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_HANGUP));
			}else if(reason.getReason() >= 300233 && reason.getReason() <= 300243){
				sendBroadcast(new Intent(UIDfineAction.ACTION_DIAL_STATE).putExtra("state", reason.getReason()));
			}
			break;
		}
	}

	@Override
	public void onHangUp(String callId, UcsReason reason) {
		CustomLog.i("onHangUp CURRENT_ID:"+callId+"SERVICE:"+reason.getReason());
		VideoConverseActivity.IncomingCallId = callId;
		UCSCall.stopCallRinging(ConnectionService.this);
		voipSwitch(reason);
	}

	@Override
	public void onConnecting(String callId) {
		UCSCall.startCallRinging(this,"dialling_tone.pcm");
	}

	/**
	 * 接收新消息
	 * nickName 显示主叫昵称
	 * userdata 暂时没用到
	 */
	@Override
	public void onIncomingCall(String callId, String callType, String callerNumber , String nickName, String userdata) {
		Log.i("YZXMSG", "onIncomingCall: 收到新的来电 callType: " + callType + ",callerNumber: " + callerNumber + ",callId: " + callId + ",nickName: " + nickName + ",userdata: " + userdata);
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(ConnectionService.this, IncomingActivity.class);
		intent.putExtra("phoneNumber", callerNumber);
		intent.putExtra("inCall", true);
		intent.putExtra("callId", callId);
		startActivity(intent);
	}


	@Override
	public void onNetWorkState(int reason, String message) {
		sendBroadcast(new Intent(UIDfineAction.ACTION_NETWORK_STATE).putExtra("state", reason)
								.putExtra("videomsg", message));
	}

	@Override
	public void onDTMF(int dtmfCode) {
		CustomLog.d(DfineAction.TAG_TCP,"DTMF:"+dtmfCode);
	}

	@Override
	public void onCameraCapture(String filePaht) {
		CustomLog.d(DfineAction.TAG_TCP,"CAMERACAPTURE:"+filePaht);
	}

	/**
	 * 对方视频模式回调
	 */
	@Override
	public void onRemoteCameraMode(UCSCameraType type) {
		if(type == UCSCameraType.REMOTECAMERA) {
			sendBroadcast(new Intent(UIDfineAction.ACTION_NETWORK_STATE).putExtra("state", 10));
		}
	}

	@Override
	public void singlePass(int arg0) {

	}

	@Override
	public void onDecryptStream(byte[] inMsg, byte[] outMsg, int inLen, int[] outLen) {

	}

	@Override
	public void onEncryptStream(byte[] inMsg, byte[] outMsg, int inLen, int[] outLen) {

	}

	/**
	 * @Description 使用指定参数值对外部音频播放设备进行初始化(拨打时回调)
	 * @param sample_rate	采样率
	 * @param bytes_per_sample	采样深度,每个样点的字节数
	 * @param num_of_channels	通道数
	 * @date 2016-3-30 下午4:08:36
	 * @author xhb
	 * @return void    返回类型
	 */
	@Override
	public void initPlayout(int sample_rate, int bytes_per_sample, int num_of_channels) {
		CustomLog.i("initPlayout sample_rate = " + sample_rate +
				" bytes_per_sample = " + bytes_per_sample +
				" num_of_channels = " + num_of_channels);
		try {
			fosPlay = new FileOutputStream(playFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description 使用指定参数值对外部音频设备的采集进行初始化(接通时回调)
	 * @param sample_rate	采样率
	 * @param bytes_per_sample	采样深度,每个样点的字节数
	 * @param num_of_channels	通道数
	 * @date 2016-3-30 下午4:10:04
	 * @author xhb
	 * @return void    返回类型
	 */
	@Override
	public void initRecording(int sample_rate, int bytes_per_sample, int num_of_channels) {
		CustomLog.i("initRecording sample_rate = " + sample_rate +
				" bytes_per_sample = " + bytes_per_sample +
				" num_of_channels = " + num_of_channels);
		testData = convertStream2byteArrry("cuiniao.pcm");
		dataLen = testData.length;
		//Log.i(TAG, "testData len = " + dataLen);
		readIndex = 0;
	}

	/**
	 * @Description 从外部音频设备读取指定数据长度的采集PCM码流
	 * @param inData	需填充为从外部音频设备采集的码流
	 * @param inSize	数据长度
	 * @return	0：成功；-1：失败
	 * @date 2016-3-30 下午4:15:23
	 * @author xhb
	 * @return int    返回类型
	 */
	@Override
	public int readRecordingData(byte[] inData, int inSize) {
//		CustomLog.i("readRecordingData inSize = " + inSize);
		if ( dataLen > inSize )	{
			if ( readIndex + inSize > dataLen ) {
				readIndex = 0;
			}
			System.arraycopy(testData, readIndex, inData, 0, inSize);
			readIndex += inSize;
			return 0;
		}
		return -1;
	}

	/**
	 * @Description 将解码后指定数据长度的PCM码流给外部设备进行播放
	 * @param outData	解码后给外部音频设备播放的码流
	 * @param outSize	数据长度
	 * @return	0：成功；-1：失败
	 * @date 2016-3-30 下午4:11:50
	 * @author xhb
	 * @return int    返回类型
	 */
	@Override
	public int writePlayoutData(byte[] outData, int outSize) {
//		CustomLog.i("writePlayoutData outSize = " + outSize);
		if ( fosPlay != null ) {
			// recording to files
			try {
				fosPlay.write(outData, 0, outSize);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}
		return -1;
	}

    public byte[] convertStream2byteArrry(String filepath) {
    	InputStream inStream = null;
        try {
            inStream = this.getResources().getAssets().open(filepath);
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        int length = 0;
        try {
        	length = inStream.available();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        byte[] buffer = new byte[length];
        try {
			inStream.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
//        Log.i(TAG, "buffer.length = " + buffer.length);
        return buffer;
    }

	@Override
	public void onLogin(UcsReason reason) {
		// 4、最后进行相关数据的保存
	}

	@Override
	public void onTransPreviewImg(String callId, byte[] bytes, int code) {
		CustomLog.v("onTransPreviewImg ...");
		sendStickyBroadcast(new Intent(UIDfineAction.ACTION_PREVIEW_IMG_STATE).putExtra("previewImgData", bytes));
	}

}
