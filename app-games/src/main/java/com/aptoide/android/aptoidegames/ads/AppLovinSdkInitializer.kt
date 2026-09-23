package com.aptoide.android.aptoidegames.ads

import android.content.Context
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.network.repository.AdvertisingIdsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Initializes the AppLovin MAX SDK exactly once for the whole app and lets every ad
 * placement (app open, home native, ...) suspend until that initialization is done.
 *
 * All ad unit ids are declared up front so MAX can pre-fetch their settings; placements
 * still decide individually whether they are enabled, geo-eligible, etc.
 */
@Singleton
class AppLovinSdkInitializer @Inject constructor(
  @ApplicationContext private val context: Context,
  private val advertisingIdsRepository: AdvertisingIdsRepository,
) {

  private val mutex = Mutex()
  private var initialization: CompletableDeferred<Boolean>? = null

  val hasSdkKey: Boolean
    get() = BuildConfig.APPLOVIN_SDK_KEY.isNotBlank()

  /**
   * Returns true once the SDK is initialized, false when it cannot be (missing key or the
   * SDK threw during initialization). Safe to call from several placements concurrently.
   */
  suspend fun ensureInitialized(): Boolean {
    if (!hasSdkKey) {
      Timber.w("AppLovin SDK key is missing. Ads are disabled.")
      return false
    }

    val deferred = mutex.withLock {
      initialization ?: CompletableDeferred<Boolean>().also { result ->
        initialization = result
        startInitialization(result)
      }
    }
    val initialized = deferred.await()
    if (!initialized) {
      // Allow a later placement to retry instead of caching the failure for the process lifetime.
      mutex.withLock { if (initialization === deferred) initialization = null }
    }
    return initialized
  }

  private suspend fun startInitialization(result: CompletableDeferred<Boolean>) {
    try {
      // Must stay off the main thread: the advertising id lookup throws when called on it.
      val testDeviceAdIds = if (BuildConfig.DEBUG) {
        withContext(Dispatchers.IO) { listOfNotNull(advertisingIdsRepository.advertisingId) }
      } else {
        emptyList()
      }

      withContext(Dispatchers.Main) {
        val sdk = AppLovinSdk.getInstance(context)
        if (sdk.isInitialized) {
          result.complete(true)
          return@withContext
        }

        val initConfig = AppLovinSdkInitializationConfiguration
          .builder(BuildConfig.APPLOVIN_SDK_KEY)
          .setMediationProvider(AppLovinMediationProvider.MAX)
          .setAdUnitIds(AD_UNIT_IDS)
          .setTestDeviceAdvertisingIds(testDeviceAdIds)
          .build()

        sdk.initialize(initConfig) { result.complete(true) }
      }
    } catch (throwable: Throwable) {
      Timber.e(throwable, "Failed to initialize the AppLovin SDK.")
      result.complete(false)
    }
  }

  companion object {
    val AD_UNIT_IDS: List<String> = listOf(
      BuildConfig.APP_OPEN_AD_UNIT_ID,
      BuildConfig.HOME_NATIVE_AD_UNIT_ID,
    ).filter { it.isNotBlank() }
  }
}
