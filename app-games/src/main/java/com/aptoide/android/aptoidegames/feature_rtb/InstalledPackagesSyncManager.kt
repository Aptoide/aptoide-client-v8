package com.aptoide.android.aptoidegames.feature_rtb

import com.aptoide.android.aptoidegames.feature_rtb.repository.InstalledPackagesRTBRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstalledPackagesSyncManager @Inject constructor(
  private val repository: InstalledPackagesRTBRepository,
) {

  suspend fun initialize() {
    try {
      repository.syncInstalledPackages()
    } catch (e: Throwable) {
      Timber.w(e, "InstalledPackagesSyncManager: sync failed")
    }
  }
}
