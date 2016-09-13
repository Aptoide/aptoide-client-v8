/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@EqualsAndHashCode(callSuper = true)
public class GetAppRequest extends V7<GetApp,GetAppRequest.Body> {

	private GetAppRequest(OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, Body body) {
		super(body, httpClient, converterFactory, baseHost);
	}

	public static GetAppRequest of(String packageName) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),
				SecurePreferencesImplementation
				.getInstance());
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());

		boolean forceServerRefresh = ManagerPreferences.getAndResetForceServerRefresh();

		return new GetAppRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), BASE_HOST, (Body) decorator.decorate(new Body
				(packageName, forceServerRefresh)));
	}

	public static GetAppRequest of(long appId) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),
				SecurePreferencesImplementation
				.getInstance());
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());

		boolean forceServerRefresh = ManagerPreferences.getAndResetForceServerRefresh();

		return new GetAppRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), BASE_HOST, (Body) decorator.decorate(new Body
				(appId, forceServerRefresh)));
	}

	public static GetAppRequest ofMd5(String md5) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),
				SecurePreferencesImplementation
				.getInstance());
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());

		boolean forceServerRefresh = ManagerPreferences.getAndResetForceServerRefresh();

		return new GetAppRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), BASE_HOST, (Body) decorator.decorate(new Body
				(forceServerRefresh, md5)));
	}

	public static GetAppRequest of(long appId, String storeName) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),
				SecurePreferencesImplementation
				.getInstance());

		boolean forceServerRefresh = ManagerPreferences.getAndResetForceServerRefresh();

		Body body = new Body(appId, forceServerRefresh);
		body.setStoreUser(getStoreOnRequest(storeName).getUsername());
		body.setStorePassSha1(getStoreOnRequest(storeName).getPasswordSha1());

		return new GetAppRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), BASE_HOST, (Body) decorator.decorate(body));
	}

	@Override
	protected Observable<GetApp> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.getApp(body, bypassCache);
	}

	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithApp {

		@Getter private Long appId;
		@Getter private String packageName;
		@Getter private boolean refresh;
		@Getter @JsonProperty("apk_md5sum") private String md5;

		public Body(Long appId, Boolean refresh) {
			this.appId = appId;
			this.refresh = refresh;
		}

		public Body(String packageName, Boolean refresh) {
			this.packageName = packageName;
			this.refresh = refresh;
		}

		public Body(Boolean refresh, String md5) {
			this.md5 = md5;
			this.refresh = refresh;
		}
	}
}
