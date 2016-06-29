/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.model.v7.store.GetStoreTabs;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetStoreTabsRequest extends BaseRequestWithStore<GetStoreTabs, GetStoreTabsRequest.Body> {

	public GetStoreTabsRequest(String storeName, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, String aptoideId, String accessToken, int versionCode, String cdn) {
		super(storeName, new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, baseHost);
	}

	public GetStoreTabsRequest(long storeId, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, String aptoideId, String accessToken, int versionCode, String cdn) {
		super(storeId, new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, baseHost);
	}

	public static GetStoreTabsRequest of(String storeName) {
		return new GetStoreTabsRequest(storeName, OkHttpClientFactory.getSingletonClient(), WebService
				.getDefaultConverter(), BASE_HOST, SecurePreferences.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool");
	}

	@Override
	protected Observable<GetStoreTabs> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.getStoreTabs(body, bypassCache);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore {

		private String lang = Api.LANG;

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn) {
			super(aptoideId, accessToken, aptoideVercode, cdn);
		}
	}
}
