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

	private static void rollbackAction(String packageName, Rollback.Action action) {
		PackageInfo packageInfo = AptoideUtils.SystemU.getPackageInfo(packageName);

		if (packageInfo != null) {
			Rollback rollback = new Rollback(packageInfo, action);

			AptoideUtils.ThreadU.runOnUiThread(() -> {
				@Cleanup
				Realm realm = Database.get();
				Database.save(rollback, realm);
			});
		}
	}

	public static void addInstallAction(String packageName) {
		rollbackAction(packageName, Rollback.Action.INSTALL);
	}

	public static void addUninstallAction(String packageName) {
		rollbackAction(packageName, Rollback.Action.UNINSTALL);
	}

	public static void addUpdateAction(String packageName) {
		rollbackAction(packageName, Rollback.Action.UPDATE);
	}

	public static void addDowngradeAction(String packageName) {
		rollbackAction(packageName, Rollback.Action.DOWNGRADE);
	}
}
