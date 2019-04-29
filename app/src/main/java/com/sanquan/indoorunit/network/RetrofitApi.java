package com.sanquan.indoorunit.network;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.sanquan.indoorunit.util.Base64;
import com.sanquan.indoorunit.util.GlobalConfig;
import com.sanquan.indoorunit.util.MD5Util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 作者：zyf on 2018/9/21.
 * 用途：TODO
 */
public class RetrofitApi {
    private static final String TAG = "RetrofitApi";
    public static final String BASE_URL_HTTPS = "http://apptest.3qiot.com/";
    public static final String BASE_URL_HTTP = "http://apptest.3qiot.com/";
//    public static final String BASE_URL_HTTPS = "https://app.3qiot.com/";
//    public static final String BASE_URL_HTTP = "http://app.3qiot.com/";
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10,TimeUnit.SECONDS)
            .readTimeout(10,TimeUnit.SECONDS)
            .addInterceptor(new Interceptor() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public Response intercept(Chain chain) throws IOException {

                    long currtime = System.currentTimeMillis();
                    String Authorization = GlobalConfig.appkey+":"+currtime;
                    String encodeAuthorization = Base64.encode(Authorization.getBytes());
                    String encodeToken = MD5Util.encrypt(GlobalConfig.appkey+GlobalConfig.secrekey+currtime);
                    Request request = chain.request();
                    Request.Builder builder1 = request.newBuilder();
                    Request build = builder1.addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json;charset=utf-8")
                            .addHeader("Authorization", encodeAuthorization)
                            .addHeader("Token", encodeToken).build();
//                    Response response = chain.proceed(request);
//                    String result = response.body().string();
//                    Log.e(TAG, "intercept: url: "+request.url()+",result: "+result);
                    return chain.proceed(build);
                }
            })
            .sslSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory(), SSLSocketFactoryUtils.createTrustAllManager())//信任所有证书
            .hostnameVerifier(new SSLSocketFactoryUtils.TrustAllHostnameVerifier())
            .build();
    private static Retrofit mRetrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL_HTTPS)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            // 针对rxjava2.x
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    public static RetrofitNetwork getRetrofitNetwork() {
        return mRetrofit.create(RetrofitNetwork.class);
    }

    //天气这个接口好像不支持https
    private static OkHttpClient okHttpClientWeather = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10,TimeUnit.SECONDS)
            .readTimeout(10,TimeUnit.SECONDS)
            .addInterceptor(new Interceptor() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public Response intercept(Chain chain) throws IOException {

                    long currtime = System.currentTimeMillis();
                    String Authorization = GlobalConfig.appkey+":"+currtime;
                    String encodeAuthorization = Base64.encode(Authorization.getBytes());
                    String encodeToken = MD5Util.encrypt(GlobalConfig.appkey+GlobalConfig.secrekey+currtime);
                    Request request = chain.request();
                    Request.Builder builder1 = request.newBuilder();
                    Request build = builder1.addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json;charset=utf-8")
                            .addHeader("Authorization", encodeAuthorization)
                            .addHeader("Token", encodeToken).build();
//                    Response response = chain.proceed(request);
//                    String result = response.body().string();
//                    Log.e(TAG, "intercept: url: "+request.url()+",result: "+result);
                    return chain.proceed(build);
                }
            })
            .sslSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory(), SSLSocketFactoryUtils.createTrustAllManager())//信任所有证书
            .hostnameVerifier(new SSLSocketFactoryUtils.TrustAllHostnameVerifier())
            .build();


    private static Retrofit mRetrofitWeather = new Retrofit.Builder()
            .baseUrl(BASE_URL_HTTP)
            .client(okHttpClientWeather)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            // 针对rxjava2.x
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    public static RetrofitNetwork mRetrofitWeather() {
        return mRetrofitWeather.create(RetrofitNetwork.class);
    }
}
