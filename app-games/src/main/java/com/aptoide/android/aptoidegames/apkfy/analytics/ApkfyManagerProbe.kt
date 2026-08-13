package com.aptoide.android.aptoidegames.apkfy.analytics

import cm.aptoide.pt.feature_apkfy.domain.ApkfyManager
import cm.aptoide.pt.feature_apkfy.domain.ApkfyModel
import com.aptoide.android.aptoidegames.apkfy.ApkfySessionPreferences
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException

class ApkfyManagerProbe(
  private val apkfyManager: ApkfyManager,
  private val apkfyAnalytics: ApkfyAnalytics,
  private val apkfySessionPreferences: ApkfySessionPreferences,
) : ApkfyManager {

  companion object {
    private const val MAX_APKFY_ATTEMPTS = 3
    private const val APKFY_RETRY_DELAY_MS = 5000L
  }

  private val mutex = Mutex()
  private var hasResolvedApkfyModel = false
  private var cachedApkfyModel: ApkfyModel? = null

  override suspend fun getApkfy(): ApkfyModel? = mutex.withLock {
    if (hasResolvedApkfyModel) {
      return@withLock cachedApkfyModel
    }

    //TODO: improve this logic. Move the retries outside this probe.
    if (!apkfySessionPreferences.hasResolvedApkfySession()) {
      var isRetry = false
      var apkfyModel: ApkfyModel? = null
      var attempts = 0

      while (attempts < MAX_APKFY_ATTEMPTS) {
        // Apkfy call repeated at most 3 times, to make sure there is no apkfy app associated.
        try {
          apkfyModel = apkfyManager.getApkfy()
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
        if (attempts < MAX_APKFY_ATTEMPTS) {
          delay(APKFY_RETRY_DELAY_MS)
        }
      }

      hasResolvedApkfyModel = true
      cachedApkfyModel = apkfyModel
      apkfySessionPreferences.markApkfySessionResolved()

      return apkfyModel
    } else {
      return null
    }
  }
}
