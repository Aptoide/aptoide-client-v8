/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.dataprovider.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import io.realm.Realm;

/**
 * Created by neuro on 20-04-2016.
 */
public class AptoideUtils {

	public static int getVerCode() {
		Context context = DataProvider.getContext();
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			return -1;
		}
	}

	public static PackageInfo getPackageInfo(String packageName) {
		try {
			return DataProvider.getContext()
					.getPackageManager()
					.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<PackageInfo> getAllInstalledApps() {
		return DataProvider.getContext()
				.getPackageManager()
				.getInstalledPackages(PackageManager.GET_SIGNATURES);
	}

	public static List<PackageInfo> getUserInstalledApps() {
		List<PackageInfo> tmp = new LinkedList<>();

		for (PackageInfo packageInfo : getAllInstalledApps()) {
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				tmp.add(packageInfo);
			}
		}

		return tmp;
	}

	public static void checkUpdates() {
		checkUpdates(null);
	}

	public static void checkUpdates(@Nullable SuccessRequestListener<ListAppsUpdates> successRequestListener) {
		Realm realm = Database.get();
		ListAppsUpdatesRequest.of(true).execute(listAppsUpdates -> {
			for (App app : listAppsUpdates.getList()) {
				Database.save(new Update(app), realm);
			}

			if (successRequestListener != null) {
				successRequestListener.onSuccess(listAppsUpdates);
			}
			realm.close();
		}, e -> {
			realm.close();
		});
	}

	public static String getResString(@StringRes int stringResId) {
		return DataProvider.getContext().getResources().getString(stringResId);
	}
}
