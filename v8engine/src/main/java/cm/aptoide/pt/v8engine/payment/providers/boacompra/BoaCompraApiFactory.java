/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.boacompra;

import java.io.IOException;
import java.util.Locale;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by marcelobenites on 10/7/16.
 */

final class BoaCompraApiFactory {

  public static BoaCompraApi create(String apiUrl, final BoaCompraAuthorization authorization) {

    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
      @Override public Response intercept(Chain chain) throws IOException {

        final Request request = chain.request();
        final Request.Builder requestBuilder = request.newBuilder();

        if (request.body() != null) {
          final Buffer buffer = new Buffer();
          try {
            request.body().writeTo(buffer);
            requestBuilder.addHeader("Content-MD5", buffer.md5().hex());
          } finally {
            buffer.close();
          }
          requestBuilder.addHeader("Content-Type", request.body().contentType().toString());
        }

        requestBuilder.addHeader("Authorization", authorization.generate(request));
        requestBuilder.addHeader("Accept", "application/vnd.boacompra.com.v1+json; charset=UTF-8");
        requestBuilder.addHeader("Accept-Language", Locale.getDefault().toString());

        return chain.proceed(requestBuilder.build());
      }
    }).build();

    return new Retrofit.Builder().baseUrl(apiUrl)
        .addConverterFactory(JacksonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .client(client)
        .build()
        .create(BoaCompraApi.class);
  }
}