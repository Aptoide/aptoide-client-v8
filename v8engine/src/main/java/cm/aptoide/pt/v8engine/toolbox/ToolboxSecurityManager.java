/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/07/2016.
 */

package cm.aptoide.pt.v8engine.toolbox;

import android.content.pm.PackageManager;
import android.content.pm.Signature;

/**
 * Created by marcelobenites on 7/7/16.
 */
public class ToolboxSecurityManager {

	private final PackageManager packageManager;

	public ToolboxSecurityManager(PackageManager packageManager) {
		this.packageManager = packageManager;
	}

	public boolean checkSignature(int uid, String packageName)  {
		return packageManager.checkSignatures(getPackageName(uid), packageName) >= 0;
	}

	public boolean checkSignature(int uid, String signature, String packageName) {
		String uidPackageName = getPackageName(uid);
		return signature.equals(getSignature(uidPackageName)) && packageName.equals(uidPackageName);
	}

	private String getSignature(String packageName) {
		try {
			Signature[] signatures = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures;
			if (signatures != null) {
				return signatures[0].toCharsString();
			}
		} catch (PackageManager.NameNotFoundException ignored) {}
		return null;
	}

	private String getPackageName(int uid) {
		String[] packagesForUid = packageManager.getPackagesForUid(uid);
		if (packagesForUid != null) {
			return packagesForUid[0];
		}
		return null;
	}
}
