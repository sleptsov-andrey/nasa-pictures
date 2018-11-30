package ru.kingbird.nasapictures.data.remote;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RestApi {

    public static final String URL = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/latest_photos";
    public static final String API_KEY = "BgWN0cXSyVXLDPbzUV8tRAEVHtBG05WztxIEbJv0";

    public static final int TIMEOUT_IN_SECONDS = 45;
    private static RestApi sRestApi;

    private final OkHttpClient httpClient;
    private final Request request;

    public static synchronized RestApi getInstance() {
        if (sRestApi == null) {
            sRestApi = new RestApi();
        }
        return sRestApi;
    }

    private RestApi() {
        httpClient = buildOkHttpClient();
        request = buildRequest();
    }

    @NonNull
    private OkHttpClient buildOkHttpClient() {

        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    @NonNull
    private Request buildRequest() {

        HttpUrl url = HttpUrl.parse(URL).newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .build();

        return new Request.Builder()
                .url(url)
                .build();
    }

    @NonNull
    public Call loadLatestPhotos(){
        return httpClient.newCall(request);
    }

}
