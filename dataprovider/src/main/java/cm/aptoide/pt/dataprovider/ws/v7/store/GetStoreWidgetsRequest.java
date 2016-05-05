/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 05/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.GetStoreWidgets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetStoreWidgetsRequest extends V7<GetStoreWidgets> {

	private final Body body = new Body();
	private final String url;

	private GetStoreWidgetsRequest() {
		this("");
	}

	private GetStoreWidgetsRequest(String url) {
		this.url = url.replace("getStoreWidgets", "");
	}


	public static GetStoreWidgetsRequest of(String storeName) {
		GetStoreWidgetsRequest getStoreDisplaysRequest = new GetStoreWidgetsRequest();

		getStoreDisplaysRequest.body.setStoreName(storeName);

		return getStoreDisplaysRequest;
	}

	public static GetStoreWidgetsRequest of(int storeId) {
		GetStoreWidgetsRequest getStoreDisplaysRequest = new GetStoreWidgetsRequest();

		getStoreDisplaysRequest.body.setStoreId(storeId);

		return getStoreDisplaysRequest;
	}

	public static GetStoreWidgetsRequest ofAction(String url) {
		return new GetStoreWidgetsRequest(url);
	}

	@Override
	protected Observable<GetStoreWidgets> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.getStoreWidgets(url, body);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody {

		private StoreContext context;
		private String lang = Api.LANG;
		private Integer limit;
		private Boolean mature = Api.MATURE;
		private Integer offset;
		private String q = Api.Q;
		private Integer storeId;
		private String storeName;
		private String storePassSha1;
		private String storeUser;
		private String widget;
		private WidgetsArgs widgetsArgs = WidgetsArgs.createDefault();
	}
}
