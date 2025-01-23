package com.aptoide.android.aptoidegames.apkfy.analytics

import cm.aptoide.pt.feature_apkfy.domain.ApkfyManager
import cm.aptoide.pt.feature_apkfy.domain.ApkfyModel
import cm.aptoide.pt.feature_campaigns.AptoideMMPCampaign
import com.aptoide.android.aptoidegames.IdsRepository
import com.google.gson.Gson
import retrofit2.HttpException

class ApkfyManagerProbe(
  private val apkfyManager: ApkfyManager,
  private val apkfyAnalytics: ApkfyAnalytics,
  private val idsRepository: IdsRepository,
) : ApkfyManager {

  companion object {
    private const val GUEST_UID_KEY = "GUEST_UID"
  }

  override suspend fun getApkfy(): ApkfyModel? {
    try {
      if (idsRepository.getId(GUEST_UID_KEY).isEmpty()) {
        return apkfyManager.getApkfy()
          ?.also(apkfyAnalytics::setApkfyUTMProperties)
          ?.also {
            idsRepository.saveId(GUEST_UID_KEY, it.guestUid)
          }
          ?.also {
            apkfyAnalytics.sendApkfySuccessEvent(data = Gson().toJson(it))
          }
          .also {
            // TODO: improve this logic
            AptoideMMPCampaign.guestUID = it?.guestUid ?: idsRepository.getId(GUEST_UID_KEY)
          }
      } else {
        AptoideMMPCampaign.guestUID = idsRepository.getId(GUEST_UID_KEY)
        return null
      }
    } catch (e: Throwable) {
      apkfyAnalytics.sendApkfyFailEvent(
        errorMessage = e.message,
        errorType = e::class.simpleName,
        errorCode = (e as? HttpException)?.code()
      )
      return null
    }
  }
}
