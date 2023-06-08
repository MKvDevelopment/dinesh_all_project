package com.workz.athome.Utils;

import com.workz.athome.Model.AdminUtils.AdminRoot;
import com.workz.athome.Model.DailyTask.TaskRoot;
import com.workz.athome.Model.UserData.Root;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface ApiService {

    @POST("getapiutils")
    Call<AdminRoot> appUtils(
            @Header("Authorization") String token
    );

    @POST("getwallet")
    Call<Root> getUserData(
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("login")
    Call<Root> loginUser(
            @Field("email") String email,
            @Field("mobile") String mobile
    );


    @FormUrlEncoded
    @POST("signup")
    Call<Root> registerUser(

            @Field("activation") String activation,
            @Field("captcha_price") String captcha_price,
            @Field("email") String email,
            @Field("mobile") String mobile,
            @Field("uid") String uid,
            @Field("userName") String userName,
            @Field("wallet") String wallet,
            @Field("instant_activation") String instant_activation,
            @Field("install") String install,
            @Field("index") String index,
            @Field("winning_balence") String winning_balence,
            @Field("remove_ads") String remove_ads,
            @Field("is_button_pressed") String is_button_pressed
    );

    @FormUrlEncoded              //passing token in header file
    @POST("updatewallet")        //post method is used to fetch data
    Call<Root> updatewallet(
            @Header("Authorization") String token,
            @Field("amount") String amount
    );
    @FormUrlEncoded              //passing token in header file
    @POST("saveinstall")        //post method is used to fetch data
    Call<Root> saveinstall(
            @Header("Authorization") String token,
            @Field("install") String install
    );
    @FormUrlEncoded              //passing token in header file
    @POST("update-remove-ads")        //post method is used to fetch data
    Call<Root> removeAds(
            @Header("Authorization") String token,
            @Field("remove_ads") String remove_ads
    );

    @FormUrlEncoded              //passing token in header file
    @POST("update-instant-wallet-activation")        //post method is used to fetch data
    Call<Root> updateInstantActivation(
            @Header("Authorization") String token,
            @Field("instant_activation") String instant_activation
    );

    @FormUrlEncoded              //passing token in header file
    @POST("update-wallet-activation")        //post method is used to fetch data
    Call<Root> updateNormalActivation(
            @Header("Authorization") String token,
            @Field("activation") String activation
    );

    @FormUrlEncoded              //passing token in header file
    @POST("update-plan-membership")        //post method is used to fetch data
    Call<Root> updateUserPlan(
            @Header("Authorization") String token,
            @Field("captcha_price") String captcha_price
    );

    @FormUrlEncoded              //passing token in header file
    @POST("updatespin")        //post method is used to fetch data
    Call<Root> updatespin(
            @Header("Authorization") String token,
            @Field("wallet") String wallet
    );

    @FormUrlEncoded              //passing token in header file
    @POST("purchasespin")        //post method is used to fetch data
    Call<Root> purchaseSpin(
            @Header("Authorization") String token,
            @Field("winning_balence") String winning_balence
    );

    @FormUrlEncoded              //passing token in header file
    @POST("update-pressed-button")        //post method is used to fetch data
    Call<Root> bonusBtn(
            @Header("Authorization") String token,
            @Field("is_button_pressed") String is_button_pressed
    );

    @POST("tasklist")        //post method is used to fetch data
    Call<TaskRoot> getTaskList(
            @Header("Authorization") String token
    );

    @FormUrlEncoded              //passing token in header file
    @POST("updatetask")        //post method is used to fetch data
    Call<TaskRoot> updateTaskData(
            @Header("Authorization") String token,
            @Field("task_no") String task_no,
            @Field("task_value") String task_value,
            @Field("wallet_amount") String wallet_amount
    );
}
