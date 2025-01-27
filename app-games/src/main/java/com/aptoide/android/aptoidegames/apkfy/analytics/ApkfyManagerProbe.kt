package com.aptoide.android.aptoidegames.apkfy.analytics

import cm.aptoide.pt.feature_apkfy.domain.ApkfyManager
import cm.aptoide.pt.feature_apkfy.domain.ApkfyModel
import cm.aptoide.pt.feature_campaigns.AptoideMMPCampaign
import com.aptoide.android.aptoidegames.IdsRepository
import com.google.gson.Gson
import kotlinx.coroutines.delay
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
    //TODO: improve this logic. Move the retries outside this probe.
    if (idsRepository.getId(GUEST_UID_KEY).isEmpty()) {
      var isRetry = false
      var apkfyModel: ApkfyModel? = null
      var attempts = 0

      while (attempts < 3) { //Apkfy call repeated at most 3 times, to make sure there is no apkfy app associated
        try {
          apkfyModel = apkfyManager.getApkfy()
            ?.also(apkfyAnalytics::setApkfyUTMProperties)
            ?.also { idsRepository.saveId(GUEST_UID_KEY, it.guestUid) }
            ?.also { apkfyAnalytics.setGuestUIDUserProperty(it.guestUid) }
            .also {
              // TODO: improve this logic
              AptoideMMPCampaign.guestUID = it?.guestUid ?: idsRepository.getId(GUEST_UID_KEY)
            }
            ?.also {
              apkfyAnalytics.sendApkfySuccessEvent(
                data = Gson().toJson(it),
                isRetry = isRetry,
                callNumber = attempts
              )
            }

          if (apkfyModel != null && apkfyModel.hasApkfy()) {
            break
          }

          isRetry = false
        } catch (e: Throwable) {
          apkfyAnalytics.sendApkfyFailEvent(
            errorMessage = e.message,
            errorType = e::class.simpleName,
            errorCode = (e as? HttpException)?.code(),
            isRetry = isRetry,
            callNumber = attempts
          )
          isRetry = true
        }

        attempts++
        delay(5000L)
      }

      return apkfyModel
    } else {
      idsRepository.getId(GUEST_UID_KEY).let {
        apkfyAnalytics.setGuestUIDUserProperty(it)
        AptoideMMPCampaign.guestUID = it
      }
      return null
    }
  }
}
