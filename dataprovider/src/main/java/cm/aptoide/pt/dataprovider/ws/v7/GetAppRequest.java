/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@EqualsAndHashCode(callSuper = true)
public class GetAppRequest extends V7<GetApp, GetAppRequest.Body> {

	private GetAppRequest(OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, Body body) {
		super(body, httpClient, converterFactory, baseHost);
	}

	public static GetAppRequest of(String packageName) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());

		boolean forceServerRefresh = ManagerPreferences.getAndResetForceServerRefresh();

		return new GetAppRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), BASE_HOST, (Body) decorator.decorate(new Body(packageName,
				forceServerRefresh)));
	}

	public static GetAppRequest of(long appId) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());

		boolean forceServerRefresh = ManagerPreferences.getAndResetForceServerRefresh();

		return new GetAppRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), BASE_HOST, (Body) decorator.decorate(new Body(appId,
				forceServerRefresh)));
	}

	@Override
	protected Observable<GetApp> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.getApp(body, bypassCache);
	}

	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody {

		@Getter private Long appId;
		@Getter private String packageName;
		@Getter private boolean refresh;

		public Body(Long appId, Boolean refresh) {
			this.appId = appId;
			this.refresh = refresh;
		}

		public Body(String packageName, Boolean refresh) {
			this.packageName = packageName;
			this.refresh = refresh;
		}
	}
}
