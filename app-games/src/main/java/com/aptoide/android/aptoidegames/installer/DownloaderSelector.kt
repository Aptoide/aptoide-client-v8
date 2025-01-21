package com.aptoide.android.aptoidegames.installer

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.install_manager.DownloadInfo
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import com.aptoide.android.aptoidegames.installer.ff.isFetchDownloaderEnabled
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DownloaderSelector @Inject constructor(
  private val featureFlags: FeatureFlags,
  private val aptoidePackageDownloader: PackageDownloader,
  private val fetchPackageDownloader: PackageDownloader,
) : PackageDownloader {

  @OptIn(ExperimentalCoroutinesApi::class)
  override fun download(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<DownloadInfo> = flow { emit(getPackageDownloader()) }
    .flatMapLatest { it.download(packageName, installPackageInfo) }

  private suspend fun getPackageDownloader(): PackageDownloader =
    when (featureFlags.isFetchDownloaderEnabled()) {
      else -> aptoidePackageDownloader
    }
}
