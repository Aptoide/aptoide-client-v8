/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.v8engine.util;

import android.content.pm.PackageInfo;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.utils.AptoideUtils;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by neuro on 27-05-2016.
 */
public class RollbackUtils {

	public static void addUninstallAction(String packageName) {
		PackageInfo packageInfo = AptoideUtils.SystemU.getPackageInfo(packageName);

		if (packageInfo != null) {
			Rollback rollback = new Rollback(packageInfo, Rollback.Action.UNINSTALL);

			AptoideUtils.ThreadU.runOnUiThread(() -> {
				@Cleanup Realm realm = Database.get();
				Database.save(rollback, realm);
			});
		}
	}
}
