/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
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
public class GetStoreMetaRequest extends BaseRequestWithStore<GetStoreMeta, GetStoreMetaRequest.Body> {

	protected GetStoreMetaRequest(String storeName, boolean bypassCache, OkHttpClient httpClient, Converter.Factory
			converterFactory) {
		super(storeName, bypassCache, new Body(), httpClient, converterFactory);
	}

	protected GetStoreMetaRequest(long storeId, boolean bypassCache, OkHttpClient httpClient, Converter.Factory
			converterFactory) {
		super(storeId, bypassCache, new Body(), httpClient, converterFactory);
	}

	public static GetStoreMetaRequest of(String storeName, boolean bypassCache) {
		return new GetStoreMetaRequest(storeName, bypassCache, WebService.getDefaultHttpClient(), WebService.getDefaultConverter());
	}

	public static GetStoreMetaRequest of(int storeId, boolean bypassCache) {
		return new GetStoreMetaRequest(storeId, bypassCache, WebService.getDefaultHttpClient(), WebService.getDefaultConverter());
	}

	@Override
	protected Observable<GetStoreMeta> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.getStoreMeta(body, bypassCache);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore {
	}
}
