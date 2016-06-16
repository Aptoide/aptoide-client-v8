/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
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
 * Created by neuro on 19-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetStoreMetaRequest extends BaseRequestWithStore<GetStoreMeta, GetStoreMetaRequest.Body> {

	private GetStoreMetaRequest(String storeName, boolean bypassCache, OkHttpClient httpClient, Converter.Factory
			converterFactory, String baseHost, String aptoideId, String accessToken, int versionCode, String cdn) {
		super(storeName, bypassCache, new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, baseHost);
	}

	private GetStoreMetaRequest(long storeId, boolean bypassCache, OkHttpClient httpClient, Converter.Factory
			converterFactory, String baseHost, String aptoideId, String accessToken, int versionCode, String cdn) {
		super(storeId, bypassCache, new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, baseHost);
	}

	public static GetStoreMetaRequest of(String storeName, boolean bypassCache) {
		return new GetStoreMetaRequest(storeName, bypassCache, OkHttpClientFactory.getSingletoneClient(), WebService
				.getDefaultConverter(), BASE_HOST, SecurePreferences.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool");
	}

	public static GetStoreMetaRequest of(int storeId, boolean bypassCache) {
		return new GetStoreMetaRequest(storeId, bypassCache, OkHttpClientFactory.getSingletoneClient(), WebService
				.getDefaultConverter(), BASE_HOST, SecurePreferences.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool");
	}

	@Override
	protected Observable<GetStoreMeta> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.getStoreMeta(body, bypassCache);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore {

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn) {
			super(aptoideId, accessToken, aptoideVercode, cdn);
		}
	}
}
