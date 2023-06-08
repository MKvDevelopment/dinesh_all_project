package com.socialmediasaver.status.retrofit;


import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AbhiAndroid
 */
public class Api {
    Retrofit retrofit = null;
    private static final int connectTimeOut = 3, readTimeOut = 4, writeTimeOut = 180;


    public  Retrofit getClient(final String token_) {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        OkHttpClient client = new OkHttpClient.Builder().
                connectTimeout(connectTimeOut, TimeUnit.MINUTES)



                //.cookieJar(new JavaNetCookieJar(cookieManager))
                .readTimeout(readTimeOut,TimeUnit.MINUTES).addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        String token = "Bearer "+ token_;
                        String userAgent = System.getProperty("http.agent");
                        Request request = null;
                        if (token != null) {
                            request = original.newBuilder()
                                    .addHeader("x-rapidapi-host", "instagram-scraper-2022.p.rapidapi.com")
                                    .header("User-Agent",userAgent)
                                    .header("Content-Type","text/html")
                                    .header("Accept", "application/json")
                                    .header("X-Requested-With", "ANDROID")
                                    .header("x-rapidapi-key", "9737316f07msh5fffdb77ac17f8ap157e06jsnc3fbd593a06e")
                                    .method(original.method(), original.body())
                                    .build();
                            return chain.proceed(request);
                        }

                        return chain.proceed(original);
                    }
                })
                .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
                .build();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(ApiConstant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client);

        retrofit=builder.build();

        return retrofit;
    }

    public  Retrofit getClient(String BASE_URL,boolean for_MethodOverloading_Concept_barkaraar_Rakhne_K_liye) {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        OkHttpClient client = new OkHttpClient.Builder().
                connectTimeout(connectTimeOut, TimeUnit.MINUTES)



                //.cookieJar(new JavaNetCookieJar(cookieManager))
                .readTimeout(readTimeOut,TimeUnit.MINUTES).addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        String token = "Bearer "+ BASE_URL;
                        String userAgent = System.getProperty("http.agent");
                        Request request = null;
//                        if (token != null) {
//                            request = original.newBuilder()
//
//                                    .addHeader("Authorization", token)
//                                    .header("User-Agent",userAgent)
//                                    .header("Content-Type","text/html")
//                                    .header("Accept", "application/json")
//                                    .header("X-Requested-With", "ANDROID")
//                                    .method(original.method(), original.body())
//                                    .build();
//                            return chain.proceed(request);
//                        }

                        return chain.proceed(original);
                    }
                })
                .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
                .build();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(ApiConstant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client( new OkHttpClient().newBuilder()
                        .cookieJar(new SessionCookieJar()).build());

        retrofit=builder.build();

        return retrofit;
    }

    private static class SessionCookieJar implements CookieJar {

        private List<Cookie> cookies;

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (url.encodedPath().endsWith("login")) {
                this.cookies = new ArrayList<>(cookies);
            }
        }




        @NonNull
        @Override
        public List<Cookie> loadForRequest(@NonNull HttpUrl httpUrl) {
            if (!httpUrl.encodedPath().endsWith("login") && cookies != null) {
                return cookies;
            }
            return Collections.emptyList();
        }
    }




}
