package com.sanquan.indoorunit.network;

import com.sanquan.indoorunit.bean.AppHomeInfoBean;
import com.sanquan.indoorunit.bean.CenterMachinesBean;
import com.sanquan.indoorunit.bean.FeedbackBean;
import com.sanquan.indoorunit.bean.LoginBean;
import com.sanquan.indoorunit.bean.OpenDoorRecordBean;
import com.sanquan.indoorunit.bean.WeatherBean;


import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 作者：zyf on 2018/9/21.
 * 用途：TODO
 */
public interface  RetrofitNetwork {


    //1.获得天气数据
    @GET("api.php/api/door/getweather")
    Observable<WeatherBean> getWeatherBean(@Query("community_id") String community_id, @Query("dev_id") String dev_id);

    // 2.登录接口
    @POST("api.php/api/device/login")
    Observable<LoginBean> login(@Body RequestBody login);

    // 3.室内机心跳接口
    @POST("api.php/api/device/heartbeat")
    Observable<ResponseBody> heartbeat(@Body RequestBody heartbeat);

    //4.室内机开门指令

    // 5.上报房屋维修和意见反馈
    @Multipart
    @POST("index.php/api/Feedback/subFeedback")
    Observable<ResponseBody> subFeedback(@Part("meta") RequestBody reportRoomIssue);


    // 6.获取意见反馈信息数据和进度
    @POST("index.php/api/Feedback/getFeedback")
    Observable<FeedbackBean> getFeedback(@Body RequestBody subFeedback);

    //9.获取app首页信息
    @POST("users/user/appHome")
    Observable<AppHomeInfoBean> getNoticesList(@Body RequestBody appHome);

    //10 开门记录
    @POST("api.php/api/device/getUnlockLogs")
    Observable<OpenDoorRecordBean> getUnlockLogs(@Body RequestBody getUnlockLogs);

    //11.1APP远程升级
    @POST("api.php/api/door/apkupdate")
    Observable<ResponseBody> checkUpgrade(@Body RequestBody checkUpgrade);

    //12 获取中心机列表
    @POST("index.php/api/center/getCenterList")
    Observable<CenterMachinesBean> getCenterList(@Body RequestBody checkUpgrade);}
