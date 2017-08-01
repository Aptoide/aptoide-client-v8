/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/07/2016.
 */

package cm.aptoide.pt.toolbox;

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

  public boolean checkSignature(int uid, String signature, String packageName) {
    final String uidPackageName = getPackageName(uid);
    return signature.equals(getSignature(uidPackageName)) && packageName.equals(uidPackageName);
  }

  private String getPackageName(int uid) {
    final String[] packagesForUid = packageManager.getPackagesForUid(uid);
    if (packagesForUid != null) {
      return packagesForUid[0];
    }
    return null;
  }

  private String getSignature(String packageName) {
    try {
      final Signature[] signatures =
          packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures;
      if (signatures != null) {
        return signatures[0].toCharsString();
      }
    } catch (PackageManager.NameNotFoundException ignored) {
    }
    return null;
  }
}
