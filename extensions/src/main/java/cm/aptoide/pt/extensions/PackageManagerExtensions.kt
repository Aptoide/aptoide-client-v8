package cm.aptoide.pt.extensions

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import java.io.File

private const val systemFlags =
  ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP

private const val oldFlags = PackageManager.GET_META_DATA or PackageManager.GET_ACTIVITIES

@get:RequiresApi(Build.VERSION_CODES.TIRAMISU)
private val newFlags: PackageManager.PackageInfoFlags
  get() = PackageManager.PackageInfoFlags.of(oldFlags.toLong())

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

fun PackageInfo.getAppSize(): Long {
  val apkFile = applicationInfo.getApkSize()
  val splitsSize = applicationInfo.getSplitsSize()
  val obbSize = File(Environment.getDataDirectory().path + "Android/Obb/" + packageName).length()
  return apkFile + splitsSize + obbSize
}

fun ApplicationInfo.getApkSize(): Long {
  return File(this.publicSourceDir).length()
}

fun ApplicationInfo.getSplitsSize(): Long {
  val splitsFolder = this.splitPublicSourceDirs
  var splitsSize = 0L
  if (splitsFolder?.isNotEmpty() == true) {
    splitsFolder.iterator().forEach {
      splitsSize += File(it).length()
    }
  }
  return splitsSize
}

fun ApplicationInfo.loadIconDrawable(packageManager: PackageManager): Drawable =
  loadUnbadgedIcon(packageManager)
    .let { drawable ->
      if (drawable is AdaptiveIconDrawable) {
        InsetDrawable(
          LayerDrawable(listOf(drawable.background, drawable.foreground).toTypedArray()),
          -27f / 108f
        )
      } else {
        drawable
      }
    }
