package com.eram.aban;


import com.eram.aban.model.Shamed;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public class AbanRepository {

    private final AbanInterface abanInterface;

    public AbanRepository() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.base_url)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient)
                .build();
        abanInterface = retrofit.create(AbanInterface.class);
    }

    public Observable<Shamed> getShamedInfo(String packageName) {
        return abanInterface.getShamed(packageName);
    }

    interface AbanInterface {
        @GET("shamed")
        Observable<Shamed> getShamed(@Query("package_name") String packageName);
    }


}
