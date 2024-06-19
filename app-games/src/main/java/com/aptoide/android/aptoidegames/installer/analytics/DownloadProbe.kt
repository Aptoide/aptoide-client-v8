package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.feature_campaigns.data.CampaignUrlNormalizer
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class DownloadProbe(
  private val packageDownloader: PackageDownloader,
  private val campaignRepository: CampaignRepository,
  private val campaignUrlNormalizer: CampaignUrlNormalizer,
) : PackageDownloader {

  override fun download(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> = packageDownloader.download(packageName, installPackageInfo)
    .onStart {
      try {
        installPackageInfo.payload
          ?.toAnalyticsPayload()
          ?.getCampaigns(campaignRepository, campaignUrlNormalizer)
          ?.sendInstallClickEvent()
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }

  override fun cancel(packageName: String): Boolean = packageDownloader.cancel(packageName)
}
