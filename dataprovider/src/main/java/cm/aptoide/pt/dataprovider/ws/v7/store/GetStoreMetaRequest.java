/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 19-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetStoreMetaRequest extends BaseRequestWithStore<GetStoreMeta,GetStoreMetaRequest.Body> {

	private GetStoreMetaRequest(OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, Body body) {
		super(body, httpClient, converterFactory, baseHost);
	}

	public static GetStoreMetaRequest of(String storeName, String username, String passwordSha1) {
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());

		final Body body = new Body(idsRepository.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool",
				Api.LANG, Api.MATURE, Api.Q, storeName);
		body.setStoreUser(username);
		body.setStorePassSha1(passwordSha1);
		return new GetStoreMetaRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), BASE_HOST, body);
	}

	public static GetStoreMetaRequest of(String storeName) {
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());

		final StoreCredentials store = getStore(storeName);
		final Body body = new Body(idsRepository.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool",
				Api.LANG, Api.MATURE, Api.Q, storeName);
		body.setStoreUser(store.getUsername());
		body.setStorePassSha1(store.getPasswordSha1());
		return new GetStoreMetaRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), BASE_HOST, body);
	}

	@Override
	protected Observable<GetStoreMeta> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.getStoreMeta(body, bypassCache);
	}

	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore {

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn, String lang, boolean mature, String q, Long storeId) {
			super(aptoideId, accessToken, aptoideVercode, cdn, lang, mature, q, storeId);
		}

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn, String lang, boolean mature, String q, String storeName) {
			super(aptoideId, accessToken, aptoideVercode, cdn, lang, mature, q, storeName);
		}
	}
}
