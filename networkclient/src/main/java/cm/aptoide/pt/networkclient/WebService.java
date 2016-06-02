/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.networkclient;

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
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
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

	protected final static OkHttpClient client = OkHttpClientFactory.newClient();
	@Getter protected static ObjectMapper objectMapper;
	protected final static Converter.Factory factory = createConverter();
	@Getter @Setter protected boolean bypassCache;
	private Class<T> clazz;
	private Observable<T> service;

	protected WebService(Class<T> clazz) {
		this.clazz = clazz;
	}

	protected WebService(Class<T> clazz, boolean bypassCache) {
		this.clazz = clazz;
		this.bypassCache = bypassCache;
	}

	protected static Converter.Factory createConverter() {

		objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		objectMapper.setDateFormat(df);

		return JacksonConverterFactory.create(objectMapper);
	}

	protected abstract String getBaseHost();

	protected Observable<T> getService() {
		return service == null ? createServiceObservable() : service;
	}

	private Observable<T> createServiceObservable() {
		return Observable.fromCallable(this::createService);
	}

	protected T createService() {
		return new Retrofit.Builder().baseUrl(getBaseHost()).client(client).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).addConverterFactory(factory).build().create(clazz);
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
		observe().subscribe(successRequestListener::onSuccess, errorRequestListener::onError);
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
