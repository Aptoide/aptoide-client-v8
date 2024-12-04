package cm.aptoide.pt.extensions

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Environment
import java.io.File
import java.security.MessageDigest

private const val systemFlags =
  ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP

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

fun PackageInfo.getSignature(): String? = runCatching {
  val signature = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    signingInfo!!.run { signingCertificateHistory.last() ?: apkContentsSigners.first() }
  } else {
    @Suppress("DEPRECATION")
    signatures!!.first()
  }.toByteArray()
  MessageDigest.getInstance("SHA1").run {
    update(signature)
    digest()
      .map { it.toInt() and 0xff }
      .map(Integer::toHexString)
      .map { if (it.length == 1) "0$it" else it }
      .joinToString(":")
  }
}.getOrNull()
