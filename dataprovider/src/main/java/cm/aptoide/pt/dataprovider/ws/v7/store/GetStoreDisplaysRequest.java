/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
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
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetStoreDisplaysRequest extends BaseRequestWithStore<GetStoreDisplays, GetStoreDisplaysRequest.Body> {

	private String url;

	public GetStoreDisplaysRequest(String url, Body body, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost) {
		super(body, httpClient, converterFactory, baseHost);
		this.url = url;
	}

	public static GetStoreDisplaysRequest ofAction(String url) {
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());

		V7Url v7Url = new V7Url(url).remove("getStoreDisplays");
		Long storeId = v7Url.getStoreId();
		final StoreCredentials store;
		final Body body;
		if (storeId != null) {
			body = new Body(idsRepository.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool", Api.LANG,
					Api.isMature(), Api.Q, storeId);
			store = getStore(storeId);
		} else {
			String storeName = v7Url.getStoreName();
			body = new Body(idsRepository.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool", Api.LANG,
					Api.isMature(), Api.Q, storeName);
			store = getStore(storeName);
		}
		body.setStoreUser(store.getUsername());
		body.setStorePassSha1(store.getPasswordSha1());
		return new GetStoreDisplaysRequest(v7Url.get(), body, OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), BASE_HOST);
	}

	@Override
	protected Observable<GetStoreDisplays> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.getStoreDisplays(url, body, bypassCache);
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
