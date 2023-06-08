package com.socialmediasaver.status.retrofit;

import com.socialmediasaver.status.model.InstagramSearch.InstaSearchUserDetailsModel;
import com.socialmediasaver.status.model.InstagramSearch.InstagramSearchModel;
import com.socialmediasaver.status.model.InstagramUrlSearchModel;
import com.socialmediasaver.status.model.story.StoryModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("topsearch/")
    Call<InstagramSearchModel> getusers(@Header("Cookie") String sessionIdAndToken, @Query("query") String query);

    @GET("feed/reels_tray/")
    Call<StoryModel> getusersStories(@Header("Cookie") String sessionIdAndToken);
    @GET("ig/reel/")
    Call<InstagramUrlSearchModel> getURLVideo(@Query("shortcode") String shortcode);
    @GET("ig/tv_info/")
    Call<InstagramUrlSearchModel> getURLVideoTV(@Query("shortcode") String shortcode);
    @GET("/ig/post_info/")
    Call<InstagramUrlSearchModel> getPVideoUrl(@Query("shortcode") String shortcode);


    @GET(".")
    Call<InstaSearchUserDetailsModel> getusersDetails(@Header("Cookie") String sessionIdAndToken,@Query("__a") String query);
}
