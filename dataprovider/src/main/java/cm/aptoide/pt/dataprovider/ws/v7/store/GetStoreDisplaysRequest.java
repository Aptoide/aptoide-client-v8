/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetStoreDisplaysRequest extends BaseRequestWithStore<GetStoreDisplays, GetStoreDisplaysRequest.Body> {

	protected GetStoreDisplaysRequest(V7Url v7Url, boolean bypassCache) {
		super(v7Url.remove("getStoreDisplays"), bypassCache, new Body());
	}

	protected GetStoreDisplaysRequest(String storeName, boolean bypassCache) {
		super(storeName, bypassCache, new Body());
	}

	protected GetStoreDisplaysRequest(long storeId, boolean bypassCache) {
		super(storeId, bypassCache, new Body());
	}

	public static GetStoreDisplaysRequest of(String storeName, boolean bypassCache) {
		return new GetStoreDisplaysRequest(storeName, bypassCache);
	}

	public static GetStoreDisplaysRequest of(int storeId, boolean bypassCache) {
		return new GetStoreDisplaysRequest(storeId, bypassCache);
	}

	public static GetStoreDisplaysRequest ofAction(String url, boolean bypassCache) {
		return new GetStoreDisplaysRequest(new V7Url(url), bypassCache);
	}

	@Override
	protected Observable<GetStoreDisplays> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.getStoreDisplays(url, body, bypassCache);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore {

		private StoreContext context;
		private String lang = Api.LANG;
		private Integer limit;
		private Integer offset;
	}
}
