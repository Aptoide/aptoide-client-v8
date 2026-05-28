package com.aptoide.android.aptoidegames.installer.autoopen

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoOpenAfterInstallExperiment @Inject constructor(
  private val featureFlags: FeatureFlags,
  private val genericAnalytics: GenericAnalytics,
) {

  private val fetchMutex = Mutex()

  @Volatile
  private var cachedDefaultOn: Boolean? = null

  @Volatile
  private var activationEventSent = false

  suspend fun isDefaultOn(): Boolean = resolve() ?: false

  fun sendActivationEvent() {
    if (activationEventSent) return
    val defaultOn = cachedDefaultOn ?: return
    activationEventSent = true

    val variant = if (defaultOn) VARIANT_ON else VARIANT_OFF
    Timber.d("Auto-open experiment activation: variant=$variant")
    genericAnalytics.logEvent(
      name = ACTIVATION_EVENT,
      params = mapOf("variant" to variant)
    )
  }

  private suspend fun resolve(): Boolean? = cachedDefaultOn ?: fetchMutex.withLock {
    cachedDefaultOn?.let { return it }

    val flag = withTimeoutOrNull(FETCH_TIMEOUT_MS) { featureFlags.getFlag(FLAG_KEY) }
    if (flag == null) {
      Timber.w("Auto-open experiment flag missing or timed out")
      null
    } else {
      flag.also { cachedDefaultOn = it }
    }
  }

  companion object {
    private const val FLAG_KEY = "exp_auto_open_after_install"
    private const val VARIANT_ON = "on"
    private const val VARIANT_OFF = "off"
    private const val ACTIVATION_EVENT = "exp_auto_open_after_install_activated"
    private const val FETCH_TIMEOUT_MS = 3_000L
  }
}
