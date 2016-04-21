/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 21/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import java.util.Arrays;
import java.util.List;

import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.GetStoreResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by neuro on 19-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetStoreRequest extends V7<GetStoreResponse> {

	private final Body body = new Body();

	private GetStoreRequest() {
	}

	public static GetStoreRequest of(String storeName) {
		GetStoreRequest getStoreRequest = new GetStoreRequest();

		getStoreRequest.body.setStore_name(storeName);

		return getStoreRequest;
	}

	@Override
	protected Observable<GetStoreResponse> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.getStore(body);
	}

	public enum StoreNodes {
		meta, tabs, widgets;

		public static List<StoreNodes> list() {
			return Arrays.asList(values());
		}
	}

	public enum StoreContext {
		home, store, store_extended, community, top, top_oem, first_install,
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody {

		private String lang = Api.LANG;
		private String q = Api.Q;
		private StoreContext context = StoreContext.community;
		private Integer limitlimit;
		private Boolean mature = Api.MATURE;
		private List<StoreNodes> nodes;
		private Integer offset;
		private Integer store_id;
		private String store_name;
		private String store_pass_sha1;
		private String store_user;
		private String widget;
		private WidgetsArgs widgets_args = WidgetsArgs.createDefault();
	}
}
