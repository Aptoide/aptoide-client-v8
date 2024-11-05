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
import java.security.MessageDigest

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
    getInstalledPackages(oldFlags)
  }

fun PackageManager.getPackageInfo(packageName: String): PackageInfo? = try {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    getPackageInfo(packageName, newFlags)
  } else {
    getPackageInfo(packageName, oldFlags)
  }
} catch (_: Throwable) {
  null
}

fun PackageInfo.ifNormalApp(): Boolean {
  val isNotSystem = applicationInfo?.run { flags and systemFlags == 0 } == true
  val hasActivities = activities?.isNotEmpty() == true
  return isNotSystem and hasActivities
}

fun PackageInfo.ifNormalAppOrGame(): Boolean {
  val isNotSystem = applicationInfo?.run { flags and systemFlags == 0 } == true
  val hasActivities = activities?.isNotEmpty() == true
  val isGame = applicationInfo?.category == ApplicationInfo.CATEGORY_GAME
  return (isNotSystem or isGame) and hasActivities
}

fun PackageInfo.getAppSize(): Long {
  val apkFile = applicationInfo?.getApkSize() ?: 0
  val splitsSize = applicationInfo?.getSplitsSize() ?: 0
  val obbSize = File(Environment.getDataDirectory().path + "Android/Obb/" + packageName).length()
  return apkFile + splitsSize + obbSize
}

fun ApplicationInfo.getApkSize(): Long = publicSourceDir.let(::File).length()

fun ApplicationInfo.getSplitsSize(): Long = splitPublicSourceDirs
  ?.map(::File)
  ?.map(File::length)
  ?.plus(0) // to make list non empty
  ?.reduce { acc, it -> acc + it }
  ?: 0

fun ApplicationInfo.loadIconDrawable(packageManager: PackageManager): Drawable? = runCatching {
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
}.getOrNull()

fun PackageManager.getSignature(packageName: String): String = runCatching {
  val signature = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    getPackageInfo(
      packageName,
      PackageManager.GET_SIGNING_CERTIFICATES
    ).signingInfo!!.run { signingCertificateHistory.last() ?: apkContentsSigners.first() }
  } else {
    @Suppress("DEPRECATION")
    getPackageInfo(
      packageName,
      PackageManager.GET_SIGNATURES
    ).signatures!!.first()
  }.toByteArray()
  MessageDigest.getInstance("SHA1").run {
    update(signature)
    digest()
      .map { it.toInt() and 0xff }
      .map(Integer::toHexString)
      .map { if (it.length == 1) "0$it" else it }
      .joinToString(":")
  }
}.getOrElse { "" }
