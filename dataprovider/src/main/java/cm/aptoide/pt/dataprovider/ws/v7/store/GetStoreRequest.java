/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import java.util.Arrays;
import java.util.List;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.OffsetInterface;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetStore;
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
public class GetStoreRequest extends BaseRequestWithStore<GetStore, GetStoreRequest.Body> {

	private GetStoreRequest(V7Url v7Url, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, String aptoideId, String accessToken, int versionCode, String cdn) {
		super(v7Url.remove("getStore"), new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, baseHost);
	}

	private GetStoreRequest(String storeName, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, String aptoideId, String accessToken, int versionCode, String cdn) {
		super(storeName, new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, baseHost);
	}

	private GetStoreRequest(long storeId, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, String aptoideId, String accessToken, int versionCode, String cdn) {
		super(storeId, new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, baseHost);
	}

	public static GetStoreRequest of(String storeName) {
		return new GetStoreRequest(storeName, OkHttpClientFactory.getSingletonClient(),
				WebService.getDefaultConverter(), BASE_HOST, SecurePreferences.getAptoideClientUUID(),
				AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool");
	}

	public static GetStoreRequest of(String storeName, StoreContext storeContext) {
		GetStoreRequest getStoreRequest = new GetStoreRequest(storeName, OkHttpClientFactory.getSingletonClient(),
				WebService.getDefaultConverter(), BASE_HOST, SecurePreferences.getAptoideClientUUID(),
				AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool");

		getStoreRequest.body.setContext(storeContext);

		return getStoreRequest;
	}

	public static GetStoreRequest ofAction(String url) {
		return new GetStoreRequest(new V7Url(url), OkHttpClientFactory.getSingletonClient(),
				WebService.getDefaultConverter(), BASE_HOST, SecurePreferences.getAptoideClientUUID(),
				AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool");
	}

	@Override
	protected Observable<GetStore> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.getStore(url, body, bypassCache);
	}

	public enum StoreNodes {
		meta, tabs, widgets;

		public static List<StoreNodes> list() {
			return Arrays.asList(values());
		}
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore implements OffsetInterface<Body> {

		private StoreContext context;
		private String lang = Api.LANG;
		private Integer limit;
		private Boolean mature = Api.MATURE;
		private List<StoreNodes> nodes;
		private int offset;
		private String q = Api.Q;
		private String widget;
		private WidgetsArgs widgetsArgs = WidgetsArgs.createDefault();

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn) {
			super(aptoideId, accessToken, aptoideVercode, cdn);
		}
	}
}
