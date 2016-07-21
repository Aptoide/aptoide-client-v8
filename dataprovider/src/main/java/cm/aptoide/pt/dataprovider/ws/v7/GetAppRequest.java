/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 21/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
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

	public static GetAppRequest of(long appId) {
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());

		boolean forceServerRefresh = ManagerPreferences.getAndResetForceServerRefresh();

		return new GetAppRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), BASE_HOST, new Body(idsRepository
				.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool", Api.LANG, Api.isMature(), Api.Q,
				appId, forceServerRefresh));
	}

	@Override
	protected Observable<GetApp> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.getApp(body, bypassCache);
	}

	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody {

		@Getter private Long appId;
		@Getter private Boolean refresh;

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn, String lang, boolean mature, String q, Long appId, Boolean refresh) {
			super(aptoideId, accessToken, aptoideVercode, cdn, lang, mature, q);
			this.appId = appId;
			this.refresh = refresh;
		}
	}
}
