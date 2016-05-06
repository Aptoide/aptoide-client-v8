/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 05/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.Order;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.ListStores;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by neuro on 27-04-2016.
 */
public class ListStoresRequest extends V7<ListStores> {

	private final Body body = new Body();
	private final String url;

	private ListStoresRequest() {
		this("");
	}

	private ListStoresRequest(String url) {
		this.url = url.replace("listStores", "");
	}

	public static ListStoresRequest of() {
		return new ListStoresRequest();
	}

	public static ListStoresRequest ofAction(String url) {
		return new ListStoresRequest(url);
	}

	@Override
	protected Observable<ListStores> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.listStores(url, body);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody {

		private Group group;
		private Integer limit;
		private Integer offset;
		private Order order;
		private Sort sort;

		public enum Group {
			featured
		}

		public enum Sort {
			latest, downloads, downloads7d, downloads30d, trending7d, trending30d,
		}
	}
}
