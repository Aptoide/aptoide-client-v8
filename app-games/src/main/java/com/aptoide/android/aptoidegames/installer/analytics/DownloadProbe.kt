package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.feature_campaigns.data.CampaignUrlNormalizer
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import java.util.concurrent.CancellationException

class DownloadProbe(
  private val packageDownloader: PackageDownloader,
  private val genericAnalytics: GenericAnalytics,
  private val campaignRepository: CampaignRepository,
  private val campaignUrlNormalizer: CampaignUrlNormalizer,
) : PackageDownloader {

  override fun download(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> {
    val analyticsPayload = installPackageInfo.payload.toAnalyticsPayload()
    return packageDownloader.download(packageName, installPackageInfo)
      .onStart {
        genericAnalytics.sendDownloadStartedEvent(
          packageName = packageName,
          analyticsPayload = analyticsPayload
        )
        try {
          analyticsPayload
            ?.getCampaigns(campaignRepository, campaignUrlNormalizer)
            ?.sendInstallClickEvent()
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
      .onCompletion {
        when (it) {
          is CancellationException -> genericAnalytics.sendDownloadCancelEvent(
            packageName = packageName,
            analyticsPayload = analyticsPayload
          )

          null -> genericAnalytics.sendDownloadCompletedEvent(
            packageName = packageName,
            analyticsPayload = analyticsPayload
          )

          else -> genericAnalytics.sendDownloadErrorEvent(
            packageName = packageName,
            analyticsPayload = analyticsPayload,
            errorMessage = it.message
          )
        }
      }
  }

  override fun cancel(packageName: String): Boolean {
    val cancelled = packageDownloader.cancel(packageName)

    //if it was not cancelled by the Package Downloader, it means that it is on queue,
    // so the analytics won't be called after completion on the download method.
    // We need to call it here
    if (!cancelled) genericAnalytics.sendDownloadCancelEvent(
      packageName = packageName,
      analyticsPayload = null
    )

    return cancelled
  }
}
