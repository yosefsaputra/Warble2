package mpc.utexas.edu.warble2.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yosef on 11/6/2017.
 */

public class RetrofitClient {
    private static String identifier = "RetrofitClient";
    private static String TAG = "RetrofitClient";
    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitClient(String baseUrl){
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
