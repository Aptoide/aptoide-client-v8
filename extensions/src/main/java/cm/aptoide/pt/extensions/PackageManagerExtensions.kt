package cm.aptoide.pt.extensions

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

private const val systemFlags =
  ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP

private const val oldFlags = PackageManager.GET_META_DATA or PackageManager.GET_ACTIVITIES

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private val newFlags: PackageManager.PackageInfoFlags =
  PackageManager.PackageInfoFlags.of(oldFlags.toLong())

fun PackageManager.getInstalledPackages(): List<PackageInfo> =
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    getInstalledPackages(newFlags)
  } else {
    @Suppress("DEPRECATION")
    getInstalledPackages(oldFlags)
  }

fun PackageManager.getPackageInfo(packageName: String): PackageInfo? = try {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    getPackageInfo(packageName, newFlags)
  } else {
    @Suppress("DEPRECATION")
    getPackageInfo(packageName, oldFlags)
  }
} catch (e: Throwable) {
  null
}

fun PackageInfo.ifNormalApp(): Boolean {
  val isNotSystem = (applicationInfo.flags and systemFlags) == 0
  val hasActivities = activities?.isNotEmpty() ?: false
  return isNotSystem and hasActivities
}
