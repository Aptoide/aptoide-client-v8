/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v7.ListSearchApps;
import io.realm.Realm;
import lombok.Cleanup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by neuro on 26-04-2016.
 */
public class ListSearchAppsRequest extends V7<ListSearchApps, ListSearchAppsRequest.Body> {

	private ListSearchAppsRequest(boolean bypassCache) {
		super(bypassCache, new Body());
	}

	public static ListSearchAppsRequest of(String query, boolean subscribedStores) {
		return of(query, subscribedStores, false);
	}

	public static ListSearchAppsRequest of(String query, boolean subscribedStores, boolean bypassCache) {
		ListSearchAppsRequest listSearchAppsRequest = new ListSearchAppsRequest(bypassCache);

		listSearchAppsRequest.body.setQuery(query);
		if (subscribedStores) {
			@Cleanup Realm realm = Database.get();
			LinkedList<Long> ids = new LinkedList<>();
			for (Store store : Database.StoreQ.getAll(realm)) {
				ids.add(store.getStoreId());
			}
			listSearchAppsRequest.body.setStoreIds(ids);
		}

		return listSearchAppsRequest;
	}

	@Override
	protected Observable<ListSearchApps> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.listSearchApps(body, bypassCache);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody implements OffsetInterface<Body> {

		private String lang = Api.LANG;
		private Integer limit;
		private boolean mature;
		private int offset;
		private String q = Api.Q;
		private String query;
		private List<Long> storeIds;
		//		Doesn't make sense without stores_auth_map
//		private List<String> storeNames;
		//  stores_auth_map implementation required
		private Boolean trusted;
	}
}
