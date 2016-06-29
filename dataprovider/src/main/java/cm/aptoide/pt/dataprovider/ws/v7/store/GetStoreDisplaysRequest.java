/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.OffsetInterface;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
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
public class GetStoreDisplaysRequest extends BaseRequestWithStore<GetStoreDisplays, GetStoreDisplaysRequest.Body> {

	private GetStoreDisplaysRequest(V7Url v7Url, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, String aptoideClientUUID, String accessToken, int verCode, String cdn) {
		super(v7Url.remove("getStoreDisplays"), new Body(aptoideClientUUID, accessToken, verCode, cdn), httpClient, converterFactory, baseHost);
	}

	private GetStoreDisplaysRequest(String storeName, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, String cdn, int versionCode, String accessToken, String aptoideId) {
		super(storeName, new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, baseHost);
	}

	private GetStoreDisplaysRequest(long storeId, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, String cdn, int vesionCode, String accessToken, String aptoideId) {
		super(storeId, new Body(aptoideId, accessToken, vesionCode, cdn), httpClient, converterFactory, baseHost);
	}

	public static GetStoreDisplaysRequest of(String storeName) {
		return new GetStoreDisplaysRequest(storeName, OkHttpClientFactory.getSingletonClient(),
				WebService.getDefaultConverter(), BASE_HOST, "pool", AptoideUtils.Core.getVerCode(),
				AptoideAccountManager.getAccessToken(), SecurePreferences.getAptoideClientUUID());
	}

	public static GetStoreDisplaysRequest ofAction(String url) {
		return new GetStoreDisplaysRequest(
				new V7Url(url),
				OkHttpClientFactory.getSingletonClient(),
				WebService.getDefaultConverter(),
				BASE_HOST,
				SecurePreferences.getAptoideClientUUID(),
				AptoideAccountManager.getAccessToken(),
				AptoideUtils.Core.getVerCode(), "pool");
	}

	@Override
	protected Observable<GetStoreDisplays> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.getStoreDisplays(url, body, bypassCache);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore implements OffsetInterface<Body> {

		private StoreContext context;
		private String lang = Api.LANG;
		private Integer limit;
		private int offset;

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn) {
			super(aptoideId, accessToken, aptoideVercode, cdn);
		}
	}
}
