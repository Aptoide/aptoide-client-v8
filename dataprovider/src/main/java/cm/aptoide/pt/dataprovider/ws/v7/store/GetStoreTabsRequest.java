/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.model.v7.store.GetStoreTabs;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetStoreTabsRequest extends BaseRequestWithStore<GetStoreTabs, GetStoreTabsRequest.Body> {

	public GetStoreTabsRequest(String storeName, boolean bypassCache) {
		super(storeName, bypassCache, new Body());
	}

	public GetStoreTabsRequest(long storeId, boolean bypassCache) {
		super(storeId, bypassCache, new Body());
	}

	public static GetStoreTabsRequest of(String storeName, boolean bypassCache) {
		return new GetStoreTabsRequest(storeName, bypassCache);
	}

	public static GetStoreTabsRequest of(int storeId, boolean bypassCache) {
		return new GetStoreTabsRequest(storeId, bypassCache);
	}

	@Override
	protected Observable<GetStoreTabs> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.getStoreTabs(body, bypassCache);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore {

		private String lang = Api.LANG;
	}
}
