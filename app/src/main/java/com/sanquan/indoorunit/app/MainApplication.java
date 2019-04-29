package com.sanquan.indoorunit.app;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.service.ConnectionService;
import com.sanquan.indoorunit.util.NetworkUtil;
import com.yzxIM.IMApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhuqian
 */
public class MainApplication extends IMApplication {

	private static final String TAG = "MainApplication";
	private static MainApplication mInstance;
	public static NotificationManager nm;

	//当前显示的Activity
	private Activity resumeActivity;

	private Map<String, Thread> downLoadThreads;

	public String targetId = "";


	//存放下载线程
	public synchronized void putThread(String msgId, Thread t){
		if(downLoadThreads == null){
			downLoadThreads = Collections.synchronizedMap(new HashMap<String, Thread>());
		}
		downLoadThreads.put(msgId, t);
		Log.i(TAG, "putThread msgId = "+msgId);
	}
	//获取下载线程
	public synchronized Thread getThread(String msgId){
		if(msgId != null && downLoadThreads != null){
			Log.i(TAG, "getThread msgId = "+msgId);
			return downLoadThreads.get(msgId);
		}
		return null;
	}
	public synchronized Thread removeThread(String msgId){
		if(msgId != null && downLoadThreads != null && downLoadThreads.containsKey(msgId)){
			Log.i(TAG, "removeThread msgId = "+msgId);
			return downLoadThreads.remove(msgId);
		}
		return null;
	}

	public Activity getResumeActivity() {
		return resumeActivity;
	}

	public void setResumeActivity(Activity resumeActivity) {
		this.resumeActivity = resumeActivity;
	}

	public void onCreate() {
		super.onCreate();
		activityList = new ArrayList<>();
		mInstance = this;
		registerActivityLifecycleCallbacks(activityLifecycleCallbacks);

	}

	public static MainApplication getInstance() {
		return mInstance;
	}


	public void setDecorVie(Activity activity){
		if (Build.VERSION.SDK_INT < 16) {
			activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}else {
			View decorView = activity.getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}

	private ArrayList<Activity> activityList;
	ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
		@Override
		public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
			activityList.add(activity);
		}

		@Override
		public void onActivityStarted(Activity activity) {


		}

		@Override
		public void onActivityResumed(Activity activity) {

		}

		@Override
		public void onActivityPaused(Activity activity) {
		}

		@Override
		public void onActivityStopped(Activity activity) {

		}

		@Override
		public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

		}

		@Override
		public void onActivityDestroyed(Activity activity) {
			if (activityList.contains(activity)) {
				activityList.remove(activity);
			}
		}
	};

	public ArrayList<Activity> getActivityList() {
		return activityList;
	}
}
