package cm.aptoide.pt.extensions

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Environment
import androidx.core.content.pm.PackageInfoCompat
import java.io.File
import java.security.MessageDigest

private const val systemFlags =
  ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP

val PackageInfo.compatVersionCode: Long get() = PackageInfoCompat.getLongVersionCode(this)

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

/**
 * The SHA-256 of the app's current signing certificate, as 64 lower-case hex characters with no
 * separators (AND-954), or null when it cannot be read. The certificate is picked the way
 * [getSignature] means to: after a key rotation the newest one of the history; for an app signed
 * by several signers, which has no history, the first signer.
 */
fun PackageInfo.getSignatureSha256(): String? = runCatching {
  val signature = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    signingInfo!!.run { signingCertificateHistory?.lastOrNull() ?: apkContentsSigners.first() }
  } else {
    @Suppress("DEPRECATION")
    signatures!!.first()
  }
  certificateSha256(signature.toByteArray())
}.getOrNull()

/** The SHA-256 of [certificate] as 64 lower-case hex characters with no separators. */
fun certificateSha256(certificate: ByteArray): String =
  MessageDigest.getInstance("SHA-256").digest(certificate)
    .joinToString("") { "%02x".format(it.toInt() and 0xff) }
