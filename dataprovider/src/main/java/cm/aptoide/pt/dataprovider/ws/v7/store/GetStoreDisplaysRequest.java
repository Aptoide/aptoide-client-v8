/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 12/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
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
public class GetStoreDisplaysRequest extends V7<GetStoreDisplays, GetStoreDisplaysRequest.Body> {

	private final String url;

	private GetStoreDisplaysRequest(boolean bypassCache) {
		this("", bypassCache);
	}

	private GetStoreDisplaysRequest(String url, boolean bypassCache) {
		super(bypassCache, new Body());
		this.url = url.replace("getStoreDisplays", "");
	}

	public static GetStoreDisplaysRequest of(String storeName, boolean bypassCache) {
		GetStoreDisplaysRequest getStoreDisplaysRequest = new GetStoreDisplaysRequest(bypassCache);

		getStoreDisplaysRequest.body.setStoreName(storeName);

		return getStoreDisplaysRequest;
	}

	public static GetStoreDisplaysRequest of(int storeId, boolean bypassCache) {
		GetStoreDisplaysRequest getStoreDisplaysRequest = new GetStoreDisplaysRequest(bypassCache);

		getStoreDisplaysRequest.body.setStoreId(storeId);

		return getStoreDisplaysRequest;
	}

	public static GetStoreDisplaysRequest ofAction(String url, boolean bypassCache) {
		return new GetStoreDisplaysRequest(url, bypassCache);
	}

	@Override
	protected Observable<GetStoreDisplays> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.getStoreDisplays(url, body, bypassCache);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody {

		private StoreContext context;
		private String lang = Api.LANG;
		private Integer limit;
		private Integer offset;
		private Integer storeId;
		private String storeName;
		private String storePassSha1;
		private String storeUser;
	}
}
