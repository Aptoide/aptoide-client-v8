package com.aptoide.android.aptoidegames.attribution.domain

import android.content.Context
import cm.aptoide.pt.feature_campaigns.AptoideMMPCampaign
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.LocalIdsRepository
import com.aptoide.android.aptoidegames.attribution.AttributionSessionPreferences
import com.aptoide.android.aptoidegames.attribution.analytics.AttributionAnalytics
import com.aptoide.android.aptoidegames.getInstallerPackageNameCompat
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

interface AttributionManager {
  /**
   * Sends the MMP attribution install event, starting the first time the app is opened, retrying
   * with exponential backoff for the first minute and then once every minute until it succeeds.
   * On success it becomes the source of truth for guest_uid, the UTM values and the oemid, and the
   * event is never sent again. On subsequent opens it only re-hydrates the in-memory guest_uid
   * used by campaign events.
   */
  suspend fun resolve()

  companion object {
    const val GUEST_UID_KEY = "GUEST_UID"
  }
}

@Singleton
class AttributionManagerImpl @Inject constructor(
  @ApplicationContext private val context: Context,
  private val attributionRepository: AttributionRepository,
  private val idsRepository: LocalIdsRepository,
  private val attributionAnalytics: AttributionAnalytics,
  private val attributionSessionPreferences: AttributionSessionPreferences,
) : AttributionManager {

  private val mutex = Mutex()

  override suspend fun resolve(): Unit = mutex.withLock {
    if (attributionSessionPreferences.hasResolvedAttribution()) {
      rehydrateGuestUid()
      return@withLock
    }

    // Captured once so that retries keep the timestamp of the first attempt.
    val timestamp = System.currentTimeMillis()
    // Until attribution succeeds, a locally generated guest_uid is stored and used everywhere
    // (campaign events, RTB, and the attribution request itself). The successful response's
    // guest_uid then replaces it as the definitive identity.
    val existingGuestUid = idsRepository.getId(AttributionManager.GUEST_UID_KEY).ifEmpty {
      generateLocalGuestUid().also {
        idsRepository.saveId(AttributionManager.GUEST_UID_KEY, it)
      }
    }
    attributionAnalytics.setGuestUIDUserProperty(existingGuestUid)
    AptoideMMPCampaign.guestUID = existingGuestUid
    var delayMs = INITIAL_RETRY_DELAY_MS
    var attempt = 0
    var isRetry = false

    while (true) {
      try {
        val model = attributionRepository.getAttribution(
          packageName = context.packageName,
          guestUid = existingGuestUid,
          timestamp = timestamp,
          vercode = BuildConfig.VERSION_CODE.toLong(),
          installerPackageName = context.packageManager
            .getInstallerPackageNameCompat(context.packageName),
        )
        applyAttribution(model)
        attributionAnalytics.sendAttributionSuccessEvent(
          data = Gson().toJson(model),
          isRetry = isRetry,
          callNumber = attempt,
        )
        // Only success resolves the attribution: the event is sent successfully at most once.
        attributionSessionPreferences.markAttributionResolved()
        break
      } catch (e: Throwable) {
        attributionAnalytics.sendAttributionFailEvent(
          errorMessage = e.message,
          errorType = e::class.simpleName,
          errorCode = (e as? HttpException)?.code(),
          isRetry = isRetry,
          callNumber = attempt,
        )
        isRetry = true
      }

      attempt++
      val backoffRemaining = BACKOFF_WINDOW_MS - (System.currentTimeMillis() - timestamp)
      if (backoffRemaining > 0) {
        delay(delayMs.coerceAtMost(backoffRemaining))
        delayMs = delayMs * 2
      } else {
        // Backoff window exhausted: keep retrying at a steady pace until it succeeds.
        delay(STEADY_RETRY_DELAY_MS)
      }
    }
  }

  private suspend fun applyAttribution(model: AttributionModel) {
    // An empty guest_uid in the response must not overwrite the locally generated one: it would
    // become the permanent identity and break every flow that waits for a non-empty guest_uid.
    model.guestUid.takeIf { it.isNotEmpty() }?.let { guestUid ->
      idsRepository.saveId(AttributionManager.GUEST_UID_KEY, guestUid)
      attributionAnalytics.setGuestUIDUserProperty(guestUid)
      AptoideMMPCampaign.guestUID = guestUid
    }
    attributionAnalytics.setAttributionUTMProperties(model)
    model.oemId?.takeIf { it.isNotEmpty() }?.let { AptoideMMPCampaign.oemid = it }
  }

  private suspend fun rehydrateGuestUid() {
    val guestUid = idsRepository.getId(AttributionManager.GUEST_UID_KEY)
    attributionAnalytics.setGuestUIDUserProperty(guestUid)
    AptoideMMPCampaign.guestUID = guestUid
  }

  // Same shape as the server-issued guest_uid: 40 lowercase hex chars.
  private fun generateLocalGuestUid(): String = ByteArray(20)
    .also { SecureRandom().nextBytes(it) }
    .joinToString("") { "%02x".format(it) }

  companion object {
    private const val BACKOFF_WINDOW_MS = 60_000L
    private const val INITIAL_RETRY_DELAY_MS = 1_000L
    private const val STEADY_RETRY_DELAY_MS = 60_000L
  }
}
