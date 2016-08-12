/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/07/2016.
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
import cm.aptoide.pt.model.v7.store.GetStore;
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
 * Created by neuro on 19-04-2016.
 */

@EqualsAndHashCode(callSuper = true)
public class GetStoreRequest extends BaseRequestWithStore<GetStore, GetStoreRequest.Body> {

	private final String url;

	private GetStoreRequest(String url, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, Body body) {
		super(body, httpClient, converterFactory, baseHost);
		this.url = url;
	}

	public static GetStoreRequest of(String storeName, StoreContext storeContext) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());

		final StoreCredentials store = getStore(storeName);
		final Body body = new Body(storeName, WidgetsArgs.createDefault());

		body.setContext(storeContext);
		body.setStoreUser(store.getUsername());
		body.setStorePassSha1(store.getPasswordSha1());

		return new GetStoreRequest("", OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), BASE_HOST, (Body) decorator.decorate(body));
	}

	public static GetStoreRequest ofAction(String url) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());

		V7Url v7Url = new V7Url(url).remove("getStore");
		Long storeId = v7Url.getStoreId();
		final StoreCredentials store;
		final Body body;
		if (storeId != null) {
			store = getStore(storeId);
			body = new Body(storeId, WidgetsArgs
					.createDefault());
		} else {
			String storeName = v7Url.getStoreName();
			store = getStore(storeName);
			body = new Body(storeName, WidgetsArgs
					.createDefault());
		}
		body.setStoreUser(store.getUsername());
		body.setStorePassSha1(store.getPasswordSha1());
		return new GetStoreRequest(v7Url.get(), OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), BASE_HOST, (Body) decorator.decorate(body));
	}

	@Override
	protected Observable<GetStore> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.getStore(url, body, bypassCache);
	}

	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore {

		@Getter @Setter private StoreContext context;
		@Getter private WidgetsArgs widgetsArgs;

		public Body(Long storeId,
		            WidgetsArgs widgetsArgs) {
			super(storeId);
			this.widgetsArgs = widgetsArgs;
		}

		public Body(String storeName,
		            WidgetsArgs widgetsArgs) {
			super(storeName);
			this.widgetsArgs = widgetsArgs;
		}
	}
}
