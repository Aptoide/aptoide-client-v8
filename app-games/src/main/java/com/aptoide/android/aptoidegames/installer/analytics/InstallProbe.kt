package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.feature_campaigns.data.CampaignUrlNormalizer
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion

class InstallProbe(
  private val packageInstaller: PackageInstaller,
  private val campaignRepository: CampaignRepository,
  private val campaignUrlNormalizer: CampaignUrlNormalizer,
) : PackageInstaller {

  override fun install(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> = packageInstaller.install(packageName, installPackageInfo)
    .onCompletion {
      when (it) {
        null -> {
          installPackageInfo.payload
            ?.toAnalyticsPayload()
            ?.getCampaigns(campaignRepository, campaignUrlNormalizer)
            ?.sendSuccessfulInstallEvent()
        }

        else -> Unit
      }
    }

  override fun uninstall(packageName: String): Flow<Int> = packageInstaller.uninstall(packageName)

  override fun cancel(packageName: String): Boolean = packageInstaller.cancel(packageName)
}
