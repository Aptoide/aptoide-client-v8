package com.aptoide.android.aptoidegames.play_and_earn.domain.installs

import android.content.pm.PackageManager
import android.os.Build
import cm.aptoide.pt.extensions.getSignatureSha256
import com.aptoide.android.aptoidegames.getInstallerPackageNameCompat
import javax.inject.Inject

/**
 * What the phone says of an installed game, for its install report (AND-954). Either may be
 * null when it cannot be read: the report goes without it.
 * @param installer the package the phone names as the game's installer.
 * @param signature the SHA-256 of the game's current signing certificate, 64 hex characters.
 */
data class PaEInstallEvidence(
  val installer: String?,
  val signature: String?,
)

/** The games on the phone, as an install report needs them. */
interface PaEInstalledGames {
  fun isInstalled(packageName: String): Boolean

  /** What the phone says of [packageName]; null when it is not installed. */
  fun evidence(packageName: String): PaEInstallEvidence?
}

class AndroidPaEInstalledGames @Inject constructor(
  private val packageManager: PackageManager,
) : PaEInstalledGames {

  override fun isInstalled(packageName: String): Boolean = runCatching {
    packageManager.getPackageInfo(packageName, 0)
  }.isSuccess

  override fun evidence(packageName: String): PaEInstallEvidence? {
    val packageInfo = runCatching {
      packageManager.getPackageInfo(packageName, SIGNING_FLAGS)
    }.getOrNull() ?: return null
    return PaEInstallEvidence(
      installer = packageManager.getInstallerPackageNameCompat(packageName),
      signature = packageInfo.getSignatureSha256(),
    )
  }

  private companion object {
    val SIGNING_FLAGS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      PackageManager.GET_SIGNING_CERTIFICATES
    } else {
      @Suppress("DEPRECATION")
      PackageManager.GET_SIGNATURES
    }
  }
}
