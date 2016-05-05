/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
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
public class ListAppsRequest extends V7<ListApps> {

	private final Body body = new Body();

	private ListAppsRequest() {

	}

	@Override
	protected Observable<ListApps> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.listApps(body);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody {

		private String lang = Api.LANG;
		private Integer limit;
		private boolean mature;
		private Integer offset;
		private Order order;
		private String q = Api.Q;
		private Sort sort;
		private Integer storeId;
		private String storeName;
		private String storePassSha1;
		private String storeUser;
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
