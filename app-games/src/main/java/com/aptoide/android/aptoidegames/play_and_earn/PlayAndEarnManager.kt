package com.aptoide.android.aptoidegames.play_and_earn

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.device_info.DeviceCountryProvider
import com.aptoide.android.aptoidegames.device_info.DeviceSecurityChecker
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPreferencesRepository
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.hasOverlayPermission
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.hasUsageStatsPermissionStatus
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PlayAndEarnManager @Inject constructor(
  @ApplicationContext private val context: Context,
  private val featureFlags: FeatureFlags,
  private val walletCoreDataSource: WalletCoreDataSource,
  private val deviceSecurityChecker: DeviceSecurityChecker,
  private val paEPreferencesRepository: PaEPreferencesRepository,
  private val deviceCountryProvider: DeviceCountryProvider,
) {

  companion object {
    private const val TAG = "PlayAndEarnManager"
    private const val PAE_VISIBILITY_FLAG_KEY = "show_play_and_earn"

    // AND-878: gates the usage-access/overlay permission onboarding, the PaEForegroundService
    // that watches the foreground app, and the time-based (play-time) missions. Off by default;
    // the code stays in place and is re-enabled by flipping the flag remotely.
    private const val PAE_USAGE_TRACKING_FLAG_KEY = "pae_usage_tracking_enabled"

    // JSON array of ISO 3166-1 alpha-2 codes, e.g. ["US","CA"]. The visibility flag is already
    // geo-targeted to the same countries, but Firebase's attribution isn't trusted: the locally
    // detected country must also match (unknown country -> hidden).
    private const val PAE_COUNTRIES_FLAG_KEY = "pae_countries"

    // Fallback when the pae_countries flag is missing or unparseable.
    private val PAE_DEFAULT_ALLOWED_COUNTRIES = setOf(
      "US", "CA", "FI", "FR", "DE", "IT", "NL", "NO", "PT", "ES", "SE", "GB",
    )
  }

  private val _playAndEarnVisibilityFlow = MutableStateFlow(false)
  private val _usageTrackingEnabledFlow = MutableStateFlow(false)

  init {
    initialize()
  }

  private fun initialize() {
    CoroutineScope(Dispatchers.IO).launch {
      _playAndEarnVisibilityFlow.value = featureFlags.getFlag(PAE_VISIBILITY_FLAG_KEY, false)
      _usageTrackingEnabledFlow.value = featureFlags.getFlag(PAE_USAGE_TRACKING_FLAG_KEY, false)
    }

    //Listen to play and earn visibility / usage tracking changes
    Firebase.remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
      override fun onUpdate(configUpdate: ConfigUpdate) {
        val visibilityUpdated = PAE_VISIBILITY_FLAG_KEY in configUpdate.updatedKeys
        val usageTrackingUpdated = PAE_USAGE_TRACKING_FLAG_KEY in configUpdate.updatedKeys
        if (visibilityUpdated || usageTrackingUpdated) {
          Firebase.remoteConfig.activate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
              // Update the FeatureFlags cache to maintain single source of truth
              CoroutineScope(Dispatchers.IO).launch {
                if (visibilityUpdated) {
                  val isEnabled = Firebase.remoteConfig.getBoolean(PAE_VISIBILITY_FLAG_KEY)
                  Timber.tag(TAG).d("Remote config visibility updated: $isEnabled")
                  featureFlags.updateFlag(PAE_VISIBILITY_FLAG_KEY, isEnabled.toString())
                  _playAndEarnVisibilityFlow.value = isEnabled
                }
                if (usageTrackingUpdated) {
                  val isEnabled = Firebase.remoteConfig.getBoolean(PAE_USAGE_TRACKING_FLAG_KEY)
                  Timber.tag(TAG).d("Remote config usage tracking updated: $isEnabled")
                  featureFlags.updateFlag(PAE_USAGE_TRACKING_FLAG_KEY, isEnabled.toString())
                  _usageTrackingEnabledFlow.value = isEnabled
                }
              }
            }
          }
        }
      }

      override fun onError(error: FirebaseRemoteConfigException) {
        Timber.tag(TAG).e(error, "Remote config listener error")
      }
    })
  }

  suspend fun shouldShowPlayAndEarn(): Boolean {
    if (!BuildConfig.DEBUG && deviceSecurityChecker.isCompromisedDevice()) {
      return false
    }
    val allowedCountries = featureFlags.getStringListOrNull(PAE_COUNTRIES_FLAG_KEY)
      ?.map { it.trim().uppercase(Locale.US) }
      ?: PAE_DEFAULT_ALLOWED_COUNTRIES
    if (deviceCountryProvider.getCountry() !in allowedCountries) {
      return false
    }
    return featureFlags.getFlag(PAE_VISIBILITY_FLAG_KEY, false)
  }

  /**
   * Whether the usage-tracking half of PaE (permission onboarding, foreground service,
   * time-based missions) is enabled. Independent of [shouldShowPlayAndEarn]: PaE can be visible
   * with tracking off, in which case Play just opens the game.
   */
  suspend fun isUsageTrackingEnabled(): Boolean =
    featureFlags.getFlag(PAE_USAGE_TRACKING_FLAG_KEY, false)

  suspend fun shouldRunUsageTracking(): Boolean = shouldShowPlayAndEarn() && isUsageTrackingEnabled()

  suspend fun shouldStartPaEService(): Boolean {
    val isServiceEnabled = paEPreferencesRepository.isPaEServiceEnabled().first()
    return isServiceEnabled && shouldRunUsageTracking()
  }

  fun observePlayAndEarnVisibility(): StateFlow<Boolean> = _playAndEarnVisibilityFlow.asStateFlow()

  fun observeUsageTrackingEnabled(): StateFlow<Boolean> = _usageTrackingEnabledFlow.asStateFlow()

  suspend fun isSignedIn(): Boolean {
    return walletCoreDataSource.getCurrentWalletAddress() != null
  }

  fun observeIsSignedIn(): Flow<Boolean> =
    walletCoreDataSource.observeCurrentWalletAddress().map { it != null }

  /** Signed in, and (only while usage tracking is enabled) holding the runtime permissions. */
  suspend fun isPlayAndEarnReady(): Boolean {
    return isSignedIn() && (!isUsageTrackingEnabled() || hasRequiredPermissions())
  }

  fun hasRequiredPermissions(): Boolean =
    context.hasUsageStatsPermissionStatus() && context.hasOverlayPermission()
}

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val playAndEarnManager: PlayAndEarnManager
) : ViewModel()

@Composable
fun rememberShouldShowPlayAndEarn(): Boolean = runPreviewable(
  preview = { Random.nextBoolean() },
  real = {
    val vm = hiltViewModel<InjectionsProvider>()
    // Observe the visibility flow so remote-config updates (false -> true) reach the UI...
    val visible by vm.playAndEarnManager.observePlayAndEarnVisibility().collectAsState()
    var shouldShowPlayAndEarn by rememberSaveable { mutableStateOf(false) }

    // ...and re-run the guarded check (compromised device + flag) whenever it flips.
    LaunchedEffect(visible) {
      shouldShowPlayAndEarn = vm.playAndEarnManager.shouldShowPlayAndEarn()
    }

    shouldShowPlayAndEarn
  }
)

@Composable
fun rememberIsPaEUsageTrackingEnabled(): Boolean = runPreviewable(
  preview = { Random.nextBoolean() },
  real = {
    val vm = hiltViewModel<InjectionsProvider>()
    val enabled by vm.playAndEarnManager.observeUsageTrackingEnabled().collectAsState()
    enabled
  }
)

@Composable
fun rememberPlayAndEarnReady(): Boolean = runPreviewable(
  preview = { Random.nextBoolean() },
  real = {
    val vm = hiltViewModel<InjectionsProvider>()
    var isPlayAndEarnReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
      isPlayAndEarnReady = vm.playAndEarnManager.isPlayAndEarnReady()
    }

    isPlayAndEarnReady
  }
)

@Composable
fun rememberIsSignedIn(): Boolean = runPreviewable(
  preview = { Random.nextBoolean() },
  real = {
    val vm = hiltViewModel<InjectionsProvider>()
    var isSignedIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
      isSignedIn = vm.playAndEarnManager.isSignedIn()
    }

    isSignedIn
  }
)
