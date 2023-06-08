package com.workz.athome.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

   // private static final String BASE_URL = "https://workathome.divasdoor.com/api/";
    private static final String BASE_URL = "https://captchahomejob.com/api/";

    public static Retrofit getRetrofit(){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


}
