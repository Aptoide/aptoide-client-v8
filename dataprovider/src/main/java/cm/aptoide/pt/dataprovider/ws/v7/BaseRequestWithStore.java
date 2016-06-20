/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.DataProvider;
import io.realm.Realm;
import lombok.Cleanup;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by neuro on 23-05-2016.
 */
public abstract class BaseRequestWithStore<U, B extends BaseBodyWithStore> extends V7<U, B> {

	protected final String url;

	protected BaseRequestWithStore(V7Url v7Url, boolean bypassCache, B body, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost) {
		super(bypassCache, body, httpClient, converterFactory, baseHost);
		setStoreIdentifierFromUrl(v7Url);
		url = v7Url.get();
	}

	protected BaseRequestWithStore(String storeName, boolean bypassCache, B body, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost) {
		super(bypassCache, body, httpClient, converterFactory, baseHost);
		body.setStoreName(storeName);
		url = "";
	}

	protected BaseRequestWithStore(long storeId, boolean bypassCache, B body, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost) {
		super(bypassCache, body, httpClient, converterFactory, baseHost);
		body.setStoreId(storeId);
		url = "";
	}

	@Override
	protected void onLoadDataFromNetwork() {
		super.onLoadDataFromNetwork();
		setPrivateCredentials();
	}

	protected void setPrivateCredentials() {
		@Cleanup Realm realm = Database.get(DataProvider.getContext());

		Store store = null;

		if (body.getStoreId() != null) {
			store = Database.StoreQ.get(body.getStoreId(), realm);
		}
		else if (body.getStoreName() != null) {
			store = Database.StoreQ.get(body.getStoreName(), realm);
		}

		if (store != null && store.getUsername() != null && store.getPasswordSha1() != null) {
			body.setStoreUser(store.getUsername()).setStorePassSha1(store.getPasswordSha1());
		}
	}

	private void setStoreIdentifierFromUrl(V7Url v7Url) {
		Long storeId = v7Url.getStoreId();
		if (storeId != null) {
			body.setStoreId(storeId);
		}
		else {
			body.setStoreName(v7Url.getStoreName());
		}
	}
}
