/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 12/05/2016.
 */

package cm.aptoide.pt.dataprovider.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

import cm.aptoide.pt.dataprovider.DataProvider;

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

	public static List<PackageInfo> getInstalledApps() {
		return DataProvider.getContext()
				.getPackageManager()
				.getInstalledPackages(PackageManager.GET_SIGNATURES);
	}
}
