/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/06/2016.
 */

package cm.aptoide.pt.networkclient;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
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

	private static OkHttpClient defaultHttpClient;
	private static Converter.Factory defaultConverterFactory;
	private static Retrofit retrofit;

	protected boolean bypassCache;
	protected final Converter.Factory converterFactory;

	private final Class<T> clazz;
	private final String baseHost;
	private final OkHttpClient httpClient;

	private Observable<T> service;

	private static Retrofit getRetrofit(OkHttpClient httpClient, Converter.Factory converterFactory, CallAdapter.Factory factory, String baseHost) {
		if (retrofit == null) {
			retrofit =  new Retrofit.Builder().baseUrl(baseHost)
					.client(httpClient)
					.addCallAdapterFactory(factory)
					.addConverterFactory(converterFactory)
					.build();
		}
		return retrofit;
	}

	public static Converter.Factory getDefaultConverter() {
		if (defaultConverterFactory == null) {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
			objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
			objectMapper.setDateFormat(df);
			defaultConverterFactory = JacksonConverterFactory.create(objectMapper);
		}
		return defaultConverterFactory;
	}

	public static OkHttpClient getDefaultHttpClient() {
		if (defaultHttpClient == null) {
			defaultHttpClient = OkHttpClientFactory.newClient();
		}
		return defaultHttpClient;
	}

	protected WebService(Class<T> clazz, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost) {
		this.httpClient = httpClient;
		this.converterFactory= converterFactory;
		this.clazz = clazz;
		this.baseHost = baseHost;
	}

	protected WebService(Class<T> clazz, boolean bypassCache, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost) {
		this.httpClient = httpClient;
		this.converterFactory= converterFactory;
		this.clazz = clazz;
		this.bypassCache = bypassCache;
		this.baseHost = baseHost;
	}

	protected Observable<T> getService() {
		return service == null ? createServiceObservable() : service;
	}

	private Observable<T> createServiceObservable() {
		return Observable.fromCallable(this::createService);
	}

	protected T createService() {
		return getRetrofit(httpClient, converterFactory, RxJavaCallAdapterFactory.create(), baseHost).create(clazz);
	}

	protected abstract Observable<U> loadDataFromNetwork(T t);

	private Observable<U> prepareAndLoad(T t) {
		onLoadDataFromNetwork();
		return loadDataFromNetwork(t);
	}

	protected void onLoadDataFromNetwork() {
	}

	public Observable<U> observe() {
		return getService().flatMap(this::prepareAndLoad)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	public void execute(SuccessRequestListener<U> successRequestListener) {
		execute(successRequestListener, defaultErrorRequestListener());
	}

	public void execute(SuccessRequestListener<U> successRequestListener, ErrorRequestListener errorRequestListener) {
		observe().subscribe(successRequestListener::call, errorRequestListener::onError);
	}

	protected ErrorRequestListener defaultErrorRequestListener() {
		return (Throwable e) -> {
			// TODO: Implementar
			System.out.println("Erro por implementar");
			e.printStackTrace();
		};
	}

	protected boolean isNoNetworkException(Throwable throwable) {
		return throwable instanceof UnknownHostException;
	}
}
