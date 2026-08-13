package com.aptoide.android.aptoidegames.feature_rtb.repository

import cm.aptoide.pt.extensions.ifNormalAppOrGame
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.LocalIdsRepository
import com.aptoide.android.aptoidegames.attribution.domain.AttributionManager
import kotlinx.coroutines.flow.first

class AptoideInstalledPackagesRTBRepository(
  private val rtbCollectorApi: RTBCollectorApi,
  private val installManager: InstallManager,
  private val aptoideIdsRepository: LocalIdsRepository,
  private val featureFlags: FeatureFlags,
) : InstalledPackagesRTBRepository {

  override suspend fun syncInstalledPackages() {
    val guestUid = aptoideIdsRepository.observeId(AttributionManager.GUEST_UID_KEY)
      .first { it.isNotEmpty() }

    val limit = featureFlags
      .getFlagAsString(FLAG_INSTALLED_PACKAGES_LIMIT, DEFAULT_LIMIT.toString())
      .toIntOrNull()
      ?.coerceAtLeast(0)
      ?: DEFAULT_LIMIT

    val packages = installManager.installedApps
      .mapNotNull { it.packageInfo }
      .filter { it.ifNormalAppOrGame() }
      .sortedByDescending { it.firstInstallTime }
      .take(limit)
      .map {
        InstalledPackage(
          package_name = it.packageName,
          install_unix_time = it.firstInstallTime,
        )
      }

    rtbCollectorApi.sendInstalledPackages(
      InstalledPackagesRequest(guest_uid = guestUid, packages = packages)
    )
  }

  companion object {
    private const val FLAG_INSTALLED_PACKAGES_LIMIT = "rtb_installed_packages_limit"
    private const val DEFAULT_LIMIT = 100
  }
}
