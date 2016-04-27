/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import java.util.Arrays;
import java.util.List;

import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.GetStore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by neuro on 19-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetStoreRequest extends V7<GetStore> {

	private final Body body = new Body();

	private GetStoreRequest() {
	}

	public static GetStoreRequest of(String storeName) {
		GetStoreRequest getStoreRequest = new GetStoreRequest();

		getStoreRequest.body.setStoreName(storeName);

		return getStoreRequest;
	}

	@Override
	protected Observable<GetStore> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.getStore(body);
	}

	public enum StoreNodes {
		meta, tabs, widgets;

		public static List<StoreNodes> list() {
			return Arrays.asList(values());
		}
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody {

		private StoreContext context;
		private String lang = Api.LANG;
		private Integer limit;
		private Boolean mature = Api.MATURE;
		private List<StoreNodes> nodes;
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
