package cm.aptoide.pt.extensions

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

@Suppress("DEPRECATION")
private const val ancientFlags =
  PackageManager.GET_META_DATA or PackageManager.GET_ACTIVITIES or PackageManager.GET_SIGNATURES

@SuppressLint("InlinedApi")
private const val oldFlags =
  PackageManager.GET_META_DATA or PackageManager.GET_ACTIVITIES or PackageManager.GET_SIGNING_CERTIFICATES

@get:RequiresApi(Build.VERSION_CODES.TIRAMISU)
private val newFlags: PackageManager.PackageInfoFlags
  get() = PackageManager.PackageInfoFlags.of(oldFlags.toLong())

fun PackageManager.getInstalledPackages(): List<PackageInfo> =
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    getInstalledPackages(newFlags)
  } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    getInstalledPackages(oldFlags)
  } else {
    getInstalledPackages(ancientFlags)
  }

fun PackageManager.getPackageInfo(packageName: String): PackageInfo? = try {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    getPackageInfo(packageName, newFlags)
  } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    getPackageInfo(packageName, oldFlags)
  } else {
    getPackageInfo(packageName, ancientFlags)
  }
} catch (_: Throwable) {
  null
}
