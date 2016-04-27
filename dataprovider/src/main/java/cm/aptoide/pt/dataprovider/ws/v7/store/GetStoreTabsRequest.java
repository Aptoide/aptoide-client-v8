/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
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
public class GetStoreTabsRequest extends V7<GetStoreTabs> {

	private final Body body = new Body();

	public static GetStoreTabsRequest of(String storeName) {
		GetStoreTabsRequest getStoreDisplaysRequest = new GetStoreTabsRequest();

		getStoreDisplaysRequest.body.setStore_name(storeName);

		return getStoreDisplaysRequest;
	}

	public static GetStoreTabsRequest of(int storeId) {
		GetStoreTabsRequest getStoreDisplaysRequest = new GetStoreTabsRequest();

		getStoreDisplaysRequest.body.setStore_id(storeId);

		return getStoreDisplaysRequest;
	}

	@Override
	protected Observable<GetStoreTabs> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.getStoreTabs(body);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody {

		private String lang = Api.LANG;
		private Integer store_id;
		private String store_name;
		private String store_pass_sha1;
		private String store_user;
	}
}
