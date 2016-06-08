/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/06/2016.
 */

package cm.aptoide.pt.dataprovider.util;

import android.support.annotation.Nullable;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by neuro on 20-04-2016.
 */
public class DataproviderUtils {

	public static void checkUpdates() {
		checkUpdates(null);
	}

	public static void checkUpdates(@Nullable SuccessRequestListener<ListAppsUpdates> successRequestListener) {
		ListAppsUpdatesRequest.of(true).execute(listAppsUpdates -> {
			@Cleanup Realm realm = Database.get();
			for (App app : listAppsUpdates.getList()) {
				Database.save(new Update(app), realm);
			}

			if (successRequestListener != null) {
				successRequestListener.call(listAppsUpdates);
			}
		}, Throwable::printStackTrace);
	}
}
