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

	public BaseRequestWithStore(B body, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost) {
		super(body, httpClient, converterFactory, baseHost);
	}

	protected static Store getStore(Long storeId) {
		@Cleanup Realm realm = Database.get(DataProvider.getContext());

		if (storeId != null) {
			return Database.StoreQ.get(storeId, realm);
		}
		return null;
	}

	protected static Store getStore(String storeName) {
		@Cleanup Realm realm = Database.get(DataProvider.getContext());
		if (storeName != null) {
			return Database.StoreQ.get(storeName, realm);
		}
		return null;
	}
}