package com.socialmediasaver.status.api;

import com.socialmediasaver.status.model.InstagramSearch.InstaSearchUserDetailsModel;
import com.socialmediasaver.status.model.InstagramSearch.InstagramSearchModel;
import com.socialmediasaver.status.model.TwitterResponse;
import com.socialmediasaver.status.model.story.FullDetailModel;
import com.socialmediasaver.status.model.story.StoryModel;
import com.google.gson.JsonObject;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface APIServices {
    @GET
    Observable<JsonObject> callResult(@Url String Value, @Header("Cookie") String cookie, @Header("User-Agent") String userAgent);

    @FormUrlEncoded
    @POST
    Observable<TwitterResponse> callTwitter(@Url String Url, @Field("id") String id);


    @GET
    Observable<StoryModel> getStoriesApi(@Url String Value, @Header("Cookie") String cookie, @Header("User-Agent") String userAgent);
    @GET
    Observable<InstaSearchUserDetailsModel> getDetails(@Url String Value, @Header("Cookie") String cookie, @Header("User-Agent") String userAgent);

    @GET
    Observable<FullDetailModel> getFullDetailInfoApi(@Url String Value, @Header("Cookie") String cookie, @Header("User-Agent") String userAgent);

    @GET
    Observable<InstagramSearchModel> getsearch(@Url String Value, @Header("Cookie") String cookie, @Header("User-Agent") String userAgent);


}