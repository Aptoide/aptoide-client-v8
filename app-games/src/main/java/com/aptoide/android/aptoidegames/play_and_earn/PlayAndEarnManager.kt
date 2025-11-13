package com.aptoide.android.aptoidegames.play_and_earn

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.hasOverlayPermission
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.hasUsageStatsPermissionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PlayAndEarnManager @Inject constructor(
  @ApplicationContext private val context: Context,
  private val featureFlags: FeatureFlags,
  private val walletCoreDataSource: WalletCoreDataSource
) {

  companion object {
    private const val PAE_VISIBILITY_FLAG_KEY = "show_play_and_earn"
  }

  suspend fun shouldShowPlayAndEarn(): Boolean {
    return featureFlags.getFlag(PAE_VISIBILITY_FLAG_KEY, false)
  }

  suspend fun isPlayAndEarnReady(): Boolean {
    val walletAddress = walletCoreDataSource.getCurrentWalletAddress()
    return walletAddress != null && hasRequiredPermissions()
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
    var shouldShowPlayAndEarn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
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
