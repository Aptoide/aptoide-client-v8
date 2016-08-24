/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 27-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListAppsRequest extends BaseRequestWithStore<ListApps, ListAppsRequest.Body> {

	private String url;

	private ListAppsRequest(String url, Body body, Converter.Factory converterFactory, OkHttpClient httpClient, String baseHost) {
		super(body, httpClient, converterFactory, baseHost);
		this.url = url;
	}

	public static ListAppsRequest ofAction(String url) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());

		V7Url v7Url = new V7Url(url).remove("listApps");
		Long storeId = v7Url.getStoreId();
		final StoreCredentials store;
		final Body body;
		if (storeId != null) {
			store = getStore(storeId);
			body = new Body(storeId);
		} else {
			String storeName = v7Url.getStoreName();
			store = getStore(storeName);
			body = new Body(storeName);
		}

		body.setStoreUser(store.getUsername());
		body.setStorePassSha1(store.getPasswordSha1());

		return new ListAppsRequest(v7Url.get(), (Body) decorator.decorate(body),
				WebService.getDefaultConverter(), OkHttpClientFactory.getSingletonClient(), BASE_HOST);
	}

	@Override
	protected Observable<ListApps> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.listApps(url, body, bypassCache);
	}


	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore implements Endless {

		@Getter private Integer limit;
		@Getter @Setter private int offset;

		public Body(Long storeId) {
			super(storeId);
		}

		public Body(String storeName) {
			super(storeName);
		}
	}
}
