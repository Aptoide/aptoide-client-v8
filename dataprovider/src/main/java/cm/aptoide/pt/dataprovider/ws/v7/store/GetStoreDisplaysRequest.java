/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.OffsetInterface;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
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

	protected GetStoreDisplaysRequest(V7Url v7Url, boolean bypassCache, OkHttpClient httpClient, Converter.Factory
			converterFactory) {
		super(v7Url.remove("getStoreDisplays"), bypassCache, new Body(), httpClient, converterFactory);
	}

	protected GetStoreDisplaysRequest(String storeName, boolean bypassCache, OkHttpClient httpClient, Converter
			.Factory converterFactory) {
		super(storeName, bypassCache, new Body(), httpClient, converterFactory);
	}

	protected GetStoreDisplaysRequest(long storeId, boolean bypassCache, OkHttpClient httpClient, Converter.Factory
			converterFactory) {
		super(storeId, bypassCache, new Body(), httpClient, converterFactory);
	}

	public static GetStoreDisplaysRequest of(String storeName, boolean bypassCache) {
		return new GetStoreDisplaysRequest(storeName, bypassCache, WebService.getDefaultHttpClient(), WebService.getDefaultConverter());
	}

	public static GetStoreDisplaysRequest of(int storeId, boolean bypassCache) {
		return new GetStoreDisplaysRequest(storeId, bypassCache, WebService.getDefaultHttpClient(), WebService.getDefaultConverter());
	}

	public static GetStoreDisplaysRequest ofAction(String url, boolean bypassCache) {
		return new GetStoreDisplaysRequest(new V7Url(url), bypassCache, WebService.getDefaultHttpClient(), WebService.getDefaultConverter());
	}

	@Override
	protected Observable<GetStoreDisplays> loadDataFromNetwork(Interfaces interfaces) {
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
	}
}
