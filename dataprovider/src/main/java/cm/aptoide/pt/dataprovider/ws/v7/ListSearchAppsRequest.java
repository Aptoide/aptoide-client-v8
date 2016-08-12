/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 05/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.StoreUtils;
import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 26-04-2016.
 */
public class ListSearchAppsRequest extends V7<ListSearchApps, ListSearchAppsRequest.Body> {

	private ListSearchAppsRequest(OkHttpClient httpClient, Converter.Factory converterFactory, Body body, String baseHost) {
		super(body, httpClient, converterFactory, baseHost);
	}

	public static ListSearchAppsRequest of(String query, String storeName) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());

		List<String> stores = Collections.singletonList(storeName);

		Map<String, List<String>> subscribedStoresAuthMap = StoreUtils.getSubscribedStoresAuthMap();
		if (subscribedStoresAuthMap != null && subscribedStoresAuthMap.containsKey(storeName)) {
			Map<String, List<String>> storesAuthMap = new HashMap<>();
			storesAuthMap.put(storeName, subscribedStoresAuthMap.get(storeName));
			return new ListSearchAppsRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), (Body) decorator.decorate(new Body(Endless
					.DEFAULT_LIMIT,
					query, storesAuthMap,
					stores, false)), BASE_HOST);
		}
		return new ListSearchAppsRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), (Body) decorator.decorate( new Body(Endless
				.DEFAULT_LIMIT, query,
				stores, false)), BASE_HOST);
	}

	public static ListSearchAppsRequest of(String query, boolean addSubscribedStores) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());

		if (addSubscribedStores) {
			return new ListSearchAppsRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), (Body) decorator.decorate(new Body(
					Endless.DEFAULT_LIMIT, query, StoreUtils
					.getSubscribedStoresIds(), StoreUtils
					.getSubscribedStoresAuthMap(), false)), BASE_HOST);
		} else {
			return new ListSearchAppsRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), (Body) decorator.decorate( new Body(Endless
					.DEFAULT_LIMIT,
					query, false)), BASE_HOST);
		}
	}

	public static ListSearchAppsRequest of(String query, boolean addSubscribedStores, boolean trustedOnly) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());

		if (addSubscribedStores) {
			return new ListSearchAppsRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), (Body) decorator.decorate(new Body(Endless
					.DEFAULT_LIMIT,
					query, StoreUtils
					.getSubscribedStoresIds(), StoreUtils
					.getSubscribedStoresAuthMap(), trustedOnly)), BASE_HOST);
		} else {
			return new ListSearchAppsRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), (Body) decorator.decorate( new Body(Endless
					.DEFAULT_LIMIT,
					query, trustedOnly)),
					BASE_HOST);
		}
	}

	@Override
	protected Observable<ListSearchApps> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.listSearchApps(body, bypassCache);
	}


	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody implements Endless {

		@Getter private Integer limit;
		@Getter @Setter private int offset;
		@Getter private String query;
		@Getter private List<Long> storeIds;
		@Getter private List<String> storeNames;
		@Getter private Map<String, List<String>> storesAuthMap;
		@Getter private Boolean trusted;

		public Body(Integer limit, String query, List<Long> storeIds, Map<String,List<String>> storesAuthMap, Boolean trusted) {
			this.limit = limit;
			this.query = query;
			this.storeIds = storeIds;
			this.storesAuthMap = storesAuthMap;
			this.trusted = trusted;
		}

		public Body(Integer limit, String query, List<String> storeNames, Boolean trusted) {
			this.limit = limit;
			this.query = query;
			this.storeNames = storeNames;
			this.trusted = trusted;
		}

		public Body(Integer limit, String query, Map<String,List<String>> storesAuthMap, List<String> storeNames, Boolean trusted) {
			this.limit = limit;
			this.query = query;
			this.storeNames = storeNames;
			this.trusted = trusted;
		}

		public Body(Integer limit, String query, Boolean trusted) {
			this.limit = limit;
			this.query = query;
			this.trusted = trusted;
		}
	}
}
