package com.aptoide.android.aptoidegames.feature_rtb.repository

import cm.aptoide.pt.environment_info.DeviceInfo
import cm.aptoide.pt.feature_apps.data.File
import cm.aptoide.pt.feature_apps.data.emptyApp
import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_campaigns.CampaignImpl
import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.feature_campaigns.CampaignTuple
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.IdsRepository
import com.aptoide.android.aptoidegames.apkfy.analytics.ApkfyManagerProbe
import com.aptoide.android.aptoidegames.feature_rtb.data.RTBApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AptoideRTBRepository @Inject constructor(
  private val rtbApi: RTBApi,
  private val scope: CoroutineScope,
  private val deviceInfo: DeviceInfo,
  private val idsRepository: IdsRepository,
  private val campaignRepository: CampaignRepository,
) : RTBRepository {

  private val rtbCache: MutableMap<String, List<RTBApp>> = mutableMapOf()

  override suspend fun getRTBApps(placement: String): List<RTBApp> =
    withContext(scope.coroutineContext) {
      rtbCache[placement]?.let {
        return@withContext it
      }

      val guestUid = idsRepository.observeId(ApkfyManagerProbe.GUEST_UID_KEY)
        .first { it.isNotEmpty() }

      val result = rtbApi.getApps(
        RTBRequest(
          listOf(
            Placement(placement, 20)
          ),
          guestUid,
          BuildConfig.APPLICATION_ID,
          deviceInfo.getDeviceLanguage(),
          Device(
            os = "Android",
            os_version = deviceInfo.getAndroidRelease(),
            model = deviceInfo.getModel()
          ),
          Screen(
            deviceInfo.getScreenWidth(),
            deviceInfo.getScreenHeight(),
            deviceInfo.getScreenDensity()
          ),
        )
      )
      val apps = if (result.isEmpty()) {
        emptyList()
      } else {
        result.map { it.toDomainModel(campaignRepository) }
      }
      rtbCache[placement] = apps
      return@withContext apps
    }

  override fun getCachedCampaigns(packageName: String): CampaignImpl? {
    return rtbCache.values
      .flatten()
      .find { it.app.packageName == packageName }
      ?.app?.campaigns
  }
}

private fun RTBResponse.toDomainModel(campaignRepository: CampaignRepository): RTBApp {
  return RTBApp(
    app = emptyApp.copy(
      name = this.appName,
      icon = this.creative.asset,
      isAppCoins = this.billingProvider?.contains("aptoide") ?: false,
      file = File(md5 = "", size = -1, path = "", path_alt = ""),
      packageName = this.packageName,
      rating = Rating(this.rating, 0, emptyList()),
      pRating = Rating(this.rating, 0, emptyList()),
      campaigns = this.tracking.aptoideMmp?.mapRTBMMPCampaigns(campaignRepository, this.campaignId)
    ),
    adUrl = this.tracking.adsNetwork?.click,
    adTimeout = this.tracking.adsNetwork?.timeout,
    isAptoideInstall = this.installFrom?.contains("aptoide") ?: true
  )
}

private fun AptoideMmp.mapRTBMMPCampaigns(
  campaignRepository: CampaignRepository,
  campaignId: String
): CampaignImpl? {
  return CampaignImpl(
    impressions = this.impression?.let { listOf(CampaignTuple("aptoide-mmp", this.impression)) }
      ?: emptyList(),
    clicks = this.click?.let { listOf(CampaignTuple("aptoide-mmp", this.click)) } ?: emptyList(),
    downloads = this.download?.let { listOf(CampaignTuple("aptoide-mmp", this.download)) }
      ?: emptyList(),
    campaignId = campaignId,
    repository = campaignRepository
  )
}
