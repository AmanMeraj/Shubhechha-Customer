package com.subh.shubhechha.Retrofit;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRequest {

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {

        if (retrofit == null) {

            // ✅ Create a Logging Interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                Log.d("API_LOG", message);
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            // BODY = full logs (headers + body)

            // ✅ Create OkHttp Client
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor) // <-- log every request and response
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            // ✅ Create Gson instance
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            // ✅ Build Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://dev8.codebuzzers.net/shubhechha/api/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
    }
}
