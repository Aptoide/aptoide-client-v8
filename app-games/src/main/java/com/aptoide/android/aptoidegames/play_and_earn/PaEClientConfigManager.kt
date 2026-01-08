package com.aptoide.android.aptoidegames.play_and_earn

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEClientConfigApi
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPreferencesRepository
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPreferencesRepository.Companion.MIN_HEARTBEAT_INTERVAL_SECONDS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaEClientConfigManager @Inject constructor(
  private val clientConfigApi: PaEClientConfigApi,
  private val paEPreferencesRepository: PaEPreferencesRepository,
  private val playAndEarnManager: PlayAndEarnManager
) {

  suspend fun fetchAndSaveClientConfig() {
    withContext(Dispatchers.IO) {
      try {
        if (!playAndEarnManager.isSignedIn()) {
          Timber.d("PaEClientConfigManager: User not signed in, skipping config fetch")
          return@withContext
        }

        if (!playAndEarnManager.shouldShowPlayAndEarn()) {
          Timber.d("PaEClientConfigManager: Play & Earn not available, skipping config fetch")
          return@withContext
        }

        val config = clientConfigApi.getClientConfig()
        val intervalSeconds = config.heartbeatIntervalSeconds

        if (intervalSeconds >= MIN_HEARTBEAT_INTERVAL_SECONDS) {
          paEPreferencesRepository.setHeartbeatIntervalSeconds(intervalSeconds)
          Timber.d("PaEClientConfigManager: Saved heartbeat interval: ${intervalSeconds}s")
        } else {
          Timber.d("PaEClientConfigManager: Ignoring heartbeat interval ${intervalSeconds}s (below minimum of ${MIN_HEARTBEAT_INTERVAL_SECONDS}s)")
        }
      } catch (e: Exception) {
        Timber.e(e, "PaEClientConfigManager: Failed to fetch client config")
      }
    }
  }
}

@HiltViewModel
class ConfigInjectionsProvider @Inject constructor(
  val paEClientConfigManager: PaEClientConfigManager
) : ViewModel()

@Composable
fun rememberPaEClientConfigManager(): PaEClientConfigManager {
  val vm = hiltViewModel<ConfigInjectionsProvider>()
  return vm.paEClientConfigManager
}
