/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v7.ListApps;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by neuro on 27-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListAppsRequest extends BaseRequestWithStore<ListApps, ListAppsRequest.Body> {

	protected ListAppsRequest(V7Url v7Url, boolean bypassCache) {
		super(v7Url.remove("listApps"), bypassCache, new Body());
	}

	protected ListAppsRequest(String storeName, boolean bypassCache) {
		super(storeName, bypassCache, new Body());
	}

	protected ListAppsRequest(long storeId, boolean bypassCache) {
		super(storeId, bypassCache, new Body());
	}

	public static ListAppsRequest ofAction(String url, boolean bypassCache) {
		return new ListAppsRequest(new V7Url(url), bypassCache);
	}

	@Override
	protected Observable<ListApps> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.listApps(url, body, bypassCache);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore implements OffsetInterface<Body> {

		private String lang = Api.LANG;
		private Integer limit;
		private boolean mature;
		private int offset;
		private Order order;
		private String q = Api.Q;
		private Sort sort;
		private Subgroups subgroups;

		public enum Sort {
			latest, downloads, downloads7d, downloads30d, pdownloads, pdownloads7d, pdownloads30d,
			trending7d, trending30d, rating, alpha,
		}

		public enum Subgroups {
			highlighted, normal,
		}
	}
}
