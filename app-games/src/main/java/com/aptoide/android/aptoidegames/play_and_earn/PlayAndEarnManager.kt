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
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PlayAndEarnManager @Inject constructor(
  @ApplicationContext private val context: Context,
  private val featureFlags: FeatureFlags,
  private val walletCoreDataSource: WalletCoreDataSource,
  private val deviceSecurityChecker: DeviceSecurityChecker,
  private val paEPreferencesRepository: PaEPreferencesRepository
) {

  companion object {
    private const val TAG = "PlayAndEarnManager"
    private const val PAE_VISIBILITY_FLAG_KEY = "show_play_and_earn"
  }

  private val _playAndEarnVisibilityFlow = MutableStateFlow(false)

  init {
    initialize()
  }

  private fun initialize() {
    CoroutineScope(Dispatchers.IO).launch {
      _playAndEarnVisibilityFlow.value = featureFlags.getFlag(PAE_VISIBILITY_FLAG_KEY, false)
    }

    //Listen to play and earn visibility changes
    Firebase.remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
      override fun onUpdate(configUpdate: ConfigUpdate) {
        if (PAE_VISIBILITY_FLAG_KEY in configUpdate.updatedKeys) {
          Firebase.remoteConfig.activate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
              val isEnabled = Firebase.remoteConfig.getBoolean(PAE_VISIBILITY_FLAG_KEY)

              Timber.tag(TAG).d("Remote config visibility updated: $isEnabled")

              // Update the FeatureFlags cache to maintain single source of truth
              CoroutineScope(Dispatchers.IO).launch {
                featureFlags.updateFlag(PAE_VISIBILITY_FLAG_KEY, isEnabled.toString())
                _playAndEarnVisibilityFlow.value = isEnabled
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
    return featureFlags.getFlag(PAE_VISIBILITY_FLAG_KEY, false)
  }

  suspend fun shouldStartPaEService(): Boolean {
    val isServiceEnabled = paEPreferencesRepository.isPaEServiceEnabled().first()
    return isServiceEnabled && shouldShowPlayAndEarn()
  }

  fun observePlayAndEarnVisibility(): StateFlow<Boolean> = _playAndEarnVisibilityFlow.asStateFlow()

  suspend fun isSignedIn(): Boolean {
    return walletCoreDataSource.getCurrentWalletAddress() != null
  }

  fun observeIsSignedIn(): Flow<Boolean> =
    walletCoreDataSource.observeCurrentWalletAddress().map { it != null }

  suspend fun isPlayAndEarnReady(): Boolean {
    return isSignedIn() && hasRequiredPermissions()
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
