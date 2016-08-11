/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
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

	private String url;

	private GetStoreMetaRequest(OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, Body body) {
		super(body, httpClient, converterFactory, baseHost);
	}

	public GetStoreMetaRequest(String url, Body body, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost) {
		super(body, httpClient, converterFactory, baseHost);
		this.url = url;
	}

	public static GetStoreMetaRequest ofAction(String url) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());

		V7Url v7Url = new V7Url(url).remove("getStoreMeta");
		Long storeId = v7Url.getStoreId();
		final StoreCredentials store;
		final Body body;
		if (storeId != null) {
			body = new Body(storeId);
			store = getStore(storeId);
		} else {
			String storeName = v7Url.getStoreName();
			body = new Body(storeName);
			store = getStore(storeName);
		}
		body.setStoreUser(store.getUsername());
		body.setStorePassSha1(store.getPasswordSha1());
		return new GetStoreMetaRequest(v7Url.get(), (Body) decorator.decorate(body), OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(),
				BASE_HOST);
	}

	public static GetStoreMetaRequest of(String storeName, String username, String passwordSha1) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());

		final Body body = new Body(storeName);
		body.setStoreUser(username);
		body.setStorePassSha1(passwordSha1);
		return new GetStoreMetaRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), BASE_HOST, (Body) decorator.decorate(body));
	}

	public static GetStoreMetaRequest of(String storeName) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());

		final StoreCredentials store = getStore(storeName);
		final Body body = new Body(storeName);
		body.setStoreUser(store.getUsername());
		body.setStorePassSha1(store.getPasswordSha1());
		return new GetStoreMetaRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), BASE_HOST, (Body) decorator.decorate(body));
	}

	@Override
	protected Observable<GetStoreMeta> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.getStoreMeta(url != null ? url : "", body, bypassCache);
	}

	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore {

		public Body(Long storeId) {
			super(storeId);
		}

		public Body(String storeName) {
			super(storeName);
		}
	}
}
