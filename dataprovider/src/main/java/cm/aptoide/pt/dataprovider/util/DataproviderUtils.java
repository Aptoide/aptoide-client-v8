/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/06/2016.
 */

package cm.aptoide.pt.dataprovider.util;

import android.support.annotation.Nullable;

import java.util.List;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by neuro on 20-04-2016.
 */
public class DataproviderUtils {

	private static final String TAG = DataproviderUtils.class.getName();

	public static void checkUpdates() {
		checkUpdates(null);
	}

	public static void checkUpdates(@Nullable SuccessRequestListener<ListAppsUpdates> successRequestListener) {
		ListAppsUpdatesRequest.of(true).execute(listAppsUpdates -> {

			if(listAppsUpdates!=null) {
				List<App> apps = listAppsUpdates.getList();
				if(apps!=null && apps.size()>0) {
					@Cleanup Realm realm = Database.get();
					for (App app : apps) {
						Database.save(new Update(app), realm);
					}

					if (successRequestListener != null) {
						successRequestListener.call(listAppsUpdates);
					}
					return;
				}
			}

			Logger.w(TAG, "List app updates response was null or empty.");

		}, Throwable::printStackTrace);
	}

	/*
	public static void checkUpdates(@Nullable SuccessRequestListener<ListAppsUpdates> successRequestListener) {
		ListAppsUpdatesRequest.of().execute(listAppsUpdates -> {
			@Cleanup Realm realm = Database.get();
			for (App app : listAppsUpdates.getList()) {
				Database.save(new Update(app), realm);
			}

			if (successRequestListener != null) {
				successRequestListener.call(listAppsUpdates);
			}
		}, Throwable::printStackTrace, true);
	}
	 */
}
