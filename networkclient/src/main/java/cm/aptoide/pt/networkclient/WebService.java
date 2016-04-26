/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.networkclient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
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

	protected final static OkHttpClient client = OkHttpClientFactory.newClient();
	protected final static Converter.Factory factory = createConverter();

	private Class<T> clazz;
	private Observable<T> service;

	protected WebService(Class<T> clazz) {
		this.clazz = clazz;
	}

	protected static Converter.Factory createConverter() {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

		return JacksonConverterFactory.create(objectMapper);
	}

	protected abstract String getBaseHost();

	protected Observable<T> getService() {
		return service == null ? createService() : service;
	}

	private Observable<T> createService() {
		return Observable.fromCallable(() -> new Retrofit.Builder().baseUrl(getBaseHost()).client(client).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).addConverterFactory
				(factory).build().create(clazz));
	}

	protected abstract Observable<U> loadDataFromNetwork(T t);

	public Observable<U> observe() {
		return getService().flatMap(this::loadDataFromNetwork).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public void execute(SuccessRequestListener<U> successRequestListener) {
		execute(successRequestListener, defaultErrorRequestListener());
	}

	public void execute(SuccessRequestListener<U> successRequestListener, ErrorRequestListener errorRequestListener) {
		observe().subscribe(successRequestListener::onSuccess, throwable -> errorRequestListener.onError((HttpException) throwable));
	}

	protected ErrorRequestListener defaultErrorRequestListener() {
		return (e) -> {
			// TODO: Implementar
			System.out.println("Erro");
		};
	}
}
