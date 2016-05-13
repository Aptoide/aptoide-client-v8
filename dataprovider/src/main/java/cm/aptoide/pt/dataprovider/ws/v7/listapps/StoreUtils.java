/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 12/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.model.v7.store.Store;

/**
 * Created by neuro on 11-05-2016.
 */
@Deprecated
public class StoreUtils {

	@Deprecated
	public static List<Store> getSubscribedStores() {
		LinkedList<Store> stores = new LinkedList<>();

		stores.add(new Store().setAppearance(new Store.Appearance("default", "void"))
				.setName("apps")
				.setId(15)
				.setAvatar("http://pool.img.aptoide" +
						".com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar" + ".jpg"));

		return stores;
	}
}
