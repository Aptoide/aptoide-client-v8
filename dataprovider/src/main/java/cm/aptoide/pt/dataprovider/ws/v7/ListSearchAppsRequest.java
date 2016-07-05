/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 04/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.StoreUtils;
import cm.aptoide.pt.model.v7.ListSearchApps;
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
 * Created by neuro on 26-04-2016.
 */
public class ListSearchAppsRequest extends V7<ListSearchApps, ListSearchAppsRequest.Body> {

	private ListSearchAppsRequest(OkHttpClient httpClient, Converter.Factory converterFactory, String aptoideId, String accessToken, int versionCode, String cdn) {
		super(new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, BASE_HOST);
	}

	public static ListSearchAppsRequest of(String query, String storeName) {
		LinkedList<String> stores = new LinkedList<>();
		stores.add(storeName);

		ListSearchAppsRequest of = of(query, false);
		of.body.setStoreNames(stores);
		Map<String, List<String>> subscribedStoresAuthMap = StoreUtils.getSubscribedStoresAuthMap();
		if (subscribedStoresAuthMap != null && subscribedStoresAuthMap.containsKey(storeName)) {
			Map<String, List<String>> storesAuthMap = new HashMap<>();
			storesAuthMap.put(storeName, subscribedStoresAuthMap.get(storeName));
			of.body.setStoresAuthMap(storesAuthMap);
		}

		return of;
	}

	public static ListSearchAppsRequest of(String query) {
		return of(query, true);
	}

	public static ListSearchAppsRequest of(String query, boolean addSubscribedStores) {
		ListSearchAppsRequest listSearchAppsRequest = new ListSearchAppsRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), SecurePreferences
				.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool");

		listSearchAppsRequest.body.setQuery(query);
		if (addSubscribedStores) {
			listSearchAppsRequest.body.setStoreIds(StoreUtils.getSubscribedStoresIds());
			Map<String, List<String>> storesAuthMap = StoreUtils.getSubscribedStoresAuthMap();
			listSearchAppsRequest.body.setStoresAuthMap(storesAuthMap != null ? storesAuthMap : null);
		}

		return listSearchAppsRequest;
	}

	@Override
	protected Observable<ListSearchApps> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.listSearchApps(body, bypassCache);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody implements OffsetInterface<Body> {

		private String lang = Api.LANG;
		private Integer limit = getDefaultLimit();
		private boolean mature;
		private int offset;
		private String q = Api.Q;
		private String query;
		private List<Long> storeIds;
		// Ideally, should never be used
		private List<String> storeNames;
		private Map<String, List<String>> storesAuthMap;
		private Boolean trusted;

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn) {
			super(aptoideId, accessToken, aptoideVercode, cdn);
		}
	}
}
