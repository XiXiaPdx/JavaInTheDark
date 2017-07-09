package com.blueoxgym.javainthedark.MusicMatch;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.blueoxgym.javainthedark.Constants.MUSIC_MATCH_BASEURL;

/**
 * Created by macbook on 7/8/17.
 */

public class ServiceGenerator {

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(MUSIC_MATCH_BASEURL)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    );

    private static Retrofit retrofit = builder.build();

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY);

    public static <S> S createService(
            Class<S> serviceClass) {
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }
        return retrofit.create(serviceClass);
    }


}
