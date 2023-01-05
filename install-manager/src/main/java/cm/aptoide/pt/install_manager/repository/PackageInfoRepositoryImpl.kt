package cm.aptoide.pt.install_manager.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

internal class PackageInfoRepositoryImpl(context: Context) : PackageInfoRepository {
  private val pm = context.packageManager

  private val systemFlags = ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP

  private val oldFlags = PackageManager.GET_META_DATA or PackageManager.GET_ACTIVITIES

  private val newFlags: PackageManager.PackageInfoFlags
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    get() = PackageManager.PackageInfoFlags.of(
      (PackageManager.GET_META_DATA or PackageManager.GET_ACTIVITIES).toLong()
    )

  override suspend fun getAll(): Set<PackageInfo> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      pm.getInstalledPackages(newFlags)
    } else {
      @Suppress("DEPRECATION")
      pm.getInstalledPackages(oldFlags)
    }
      .filter(::ifNormalApp)
      .toSet()


  override suspend fun get(packageName: String): PackageInfo? = try {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      pm.getPackageInfo(packageName, newFlags)
    } else {
      @Suppress("DEPRECATION")
      pm.getPackageInfo(packageName, oldFlags)
    }
      .takeIf(::ifNormalApp)
  } catch (e: Throwable) {
    null
  }

  private fun ifNormalApp(packageInfo: PackageInfo): Boolean {
    val isNotSystem = (packageInfo.applicationInfo.flags and systemFlags) == 0
    val hasActivities = packageInfo.activities?.isNotEmpty() ?: false
    return isNotSystem and hasActivities
  }
}
