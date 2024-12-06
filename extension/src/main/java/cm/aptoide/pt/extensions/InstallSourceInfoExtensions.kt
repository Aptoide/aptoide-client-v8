package cm.aptoide.pt.extensions

import android.content.pm.InstallSourceInfo
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

fun InstallSourceInfo?.canBeInstalledSilentlyBy(packageName: String): Boolean =
  (VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE && this?.updateOwnerPackageName == packageName)
    || (VERSION.SDK_INT >= VERSION_CODES.R && this?.installingPackageName == packageName)
