/*
 * Copyright (c) 2016.
 * Modified on 20/07/2016.
 */

package cm.aptoide.pt.dataprovider;

import cm.aptoide.pt.dataprovider.interfaces.ErrorRequestListener;
import cm.aptoide.pt.dataprovider.interfaces.SuccessRequestListener;
import cm.aptoide.pt.logger.Logger;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Base class for webservices
 *
 * @param <T> Interface used to execute the request.
 * @param <U> Returning type of the request.
 */
public abstract class WebService<T, U> {

  public static final String BYPASS_HEADER_KEY = "X-Bypass-Cache";
  public static final String BYPASS_HEADER_VALUE = "true";

  private static Converter.Factory defaultConverterFactory;

  protected final Converter.Factory converterFactory;
  private final Class<T> clazz;

  private final String baseHost;
  private final OkHttpClient httpClient;

  private Retrofit retrofit;

  protected WebService(Class<T> clazz, OkHttpClient httpClient, Converter.Factory converterFactory,
      String baseHost) {
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.clazz = clazz;
    this.baseHost = baseHost;
  }

  public static Converter.Factory getDefaultConverter() {
    if (defaultConverterFactory == null) {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
      objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
      objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

      DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
      objectMapper.setDateFormat(df);
      defaultConverterFactory = JacksonConverterFactory.create(objectMapper);
    }
    return defaultConverterFactory;
  }

  public Observable<U> observe() {
    return observe(false);
  }

  public Observable<U> observe(boolean bypassCache) {
    return createServiceObservable().flatMap(response -> prepareAndLoad(response, bypassCache))
        .subscribeOn(Schedulers.io());
  }

  private Observable<T> createServiceObservable() {
    return Observable.fromCallable(() -> createService());
  }

  private Observable<U> prepareAndLoad(T t, boolean bypassCache) {
    onLoadDataFromNetwork();
    return loadDataFromNetwork(t, bypassCache);
  }

  private T createService() {
    return getRetrofit(httpClient, converterFactory, RxJavaCallAdapterFactory.create(),
        baseHost).create(clazz);
  }

  private void onLoadDataFromNetwork() {
  }

  protected abstract Observable<U> loadDataFromNetwork(T t, boolean bypassCache);

  private Retrofit getRetrofit(OkHttpClient httpClient, Converter.Factory converterFactory,
      CallAdapter.Factory factory, String baseHost) {
    if (retrofit == null) {
      retrofit = new Retrofit.Builder().baseUrl(baseHost)
          .client(httpClient)
          .addCallAdapterFactory(factory)
          .addConverterFactory(converterFactory)
          .build();
    }
    return retrofit;
  }

  public void execute(SuccessRequestListener<U> successRequestListener) {
    execute(successRequestListener, false);
  }

  public void execute(SuccessRequestListener<U> successRequestListener, boolean bypassCache) {
    execute(successRequestListener, defaultErrorRequestListener(), bypassCache);
  }

  public void execute(SuccessRequestListener<U> successRequestListener,
      ErrorRequestListener errorRequestListener, boolean bypassCache) {
    observe(bypassCache).observeOn(AndroidSchedulers.mainThread())
        .subscribe(successRequestListener, err -> errorRequestListener.onError(err));
  }

  private ErrorRequestListener defaultErrorRequestListener() {

    return (Throwable e) -> {
      // TODO: Implementar
      Logger.e(ErrorRequestListener.class.getName(), "Erro por implementar");
      e.printStackTrace();
    };
  }

  public void execute(SuccessRequestListener<U> successRequestListener,
      ErrorRequestListener errorRequestListener) {
    execute(successRequestListener, errorRequestListener, false);
  }

  protected boolean isNoNetworkException(Throwable throwable) {
    return throwable instanceof UnknownHostException;
  }
}
