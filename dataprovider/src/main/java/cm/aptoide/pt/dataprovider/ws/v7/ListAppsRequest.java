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
public class ListAppsRequest extends V7<ListApps> {

	private final Body body = new Body();

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
		private Integer store_id;
		private String store_name;
		private String store_pass_sha1;
		private String store_user;
		private Subgroups subgroups;

		public enum Sort {
			latest, downloads, downloads7d, downloads30d, pdownloads, pdownloads7d, pdownloads30d, trending7d, trending30d, rating, alpha,
		}

		public enum Subgroups {
			highlighted, normal,
		}
	}
}
