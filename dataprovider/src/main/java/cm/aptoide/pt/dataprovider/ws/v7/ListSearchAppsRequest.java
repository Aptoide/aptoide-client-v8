/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import java.util.List;

import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v7.ListSearchApps;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import rx.Observable;

/**
 * Created by neuro on 26-04-2016.
 */
public class ListSearchAppsRequest extends V7<ListSearchApps> {

	@Delegate(types = Body.class) private final Body body = new Body();

	private ListSearchAppsRequest() {

	}

	public static ListSearchAppsRequest of(String query) {
		ListSearchAppsRequest listSearchAppsRequest = new ListSearchAppsRequest();
		listSearchAppsRequest.body.setQuery(query);

		return listSearchAppsRequest;
	}

	@Override
	protected Observable<ListSearchApps> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.listSearchApps(body);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody {

		private String lang = Api.LANG;
		private Integer limit;
		private boolean mature;
		private Integer offset;
		private String q = Api.Q;
		private String query;
		private List<Integer> storeIds;
		//		Doesn't make sense without stores_auth_map
//		private List<String> storeNames;
		//  stores_auth_map implementation required
	}
}
