/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
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
public class GetStoreWidgetsRequest extends BaseRequestWithStore<GetStoreWidgets, GetStoreWidgetsRequest.Body> {

	private final String url;

	private GetStoreWidgetsRequest(String url, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, Body body) {
		super(body, httpClient, converterFactory, baseHost);
		this.url = url;
	}

	public static GetStoreWidgetsRequest ofAction(String url) {
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		V7Url v7Url = new V7Url(url).remove("getStoreWidgets");
		Long storeId = v7Url.getStoreId();
		final StoreCredentials store;
		final Body body;
		if (storeId != null) {
			store = getStore(storeId);
			body = new Body(idsRepository.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool",
					Api.LANG, Api.MATURE, Api.Q, storeId, WidgetsArgs.createDefault());
		} else {
			String storeName = v7Url.getStoreName();
			store = getStore(storeName);
			body = new Body(idsRepository.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool",
					Api.LANG, Api.MATURE, Api.Q, storeName, WidgetsArgs.createDefault());
		}

		body.setStoreUser(store.getUsername());
		body.setStorePassSha1(store.getPasswordSha1());

		return new GetStoreWidgetsRequest(v7Url.get(), OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), BASE_HOST, body);
	}

	@Override
	protected Observable<GetStoreWidgets> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.getStoreWidgets(url, body, bypassCache);
	}

	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore {

		private WidgetsArgs widgetsArgs;

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn, String lang, boolean mature, String q, Long storeId,
		            WidgetsArgs widgetsArgs) {
			super(aptoideId, accessToken, aptoideVercode, cdn, lang, mature, q, storeId);
			this.widgetsArgs = widgetsArgs;
		}

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn, String lang, boolean mature, String q, String storeName,
		            WidgetsArgs widgetsArgs) {
			super(aptoideId, accessToken, aptoideVercode, cdn, lang, mature, q, storeName);
			this.widgetsArgs = widgetsArgs;
		}
	}
}
