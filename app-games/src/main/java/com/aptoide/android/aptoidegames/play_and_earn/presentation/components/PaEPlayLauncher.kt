package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.campaigns.data.PaEMissionsRepository
import cm.aptoide.pt.campaigns.domain.needsUsageAccess
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.play_and_earn.PlayAndEarnManager
import com.aptoide.android.aptoidegames.play_and_earn.presentation.service.PaEForegroundService
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in.playAndEarnSignInRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/** How the Play & Earn service runs after Play (AND-901). */
enum class PaEPlayMode {
  /** Watches the foreground game to measure play time; needs the usage-access permissions. */
  TRACKING,

  /** Heartbeats for the game's MMP-confirmed missions (AND-879); no permission involved. */
  STATUS,
}

/**
 * Picks the service mode for a game on the Play tap (AND-901).
 *
 * Tracking mode only when the game's campaign needs usage access, the device currently grants
 * the permissions, the user has not opted out in Settings and the usage path is not switched off
 * remotely; status mode otherwise. Play never asks for permissions: the locked time missions do
 * (AND-902/903).
 */
class PaEPlayLauncher internal constructor(
  /** Whether the game's missions need usage access; null when they could not be read. */
  private val needsUsageAccess: Boolean?,
  /** The `pae_usage_tracking_enabled` kill switch for the usage path. */
  private val usageTrackingEnabled: Boolean,
  /** The Settings "Play & Earn tracking" switch (AND-902): off = the user opted out. */
  private val optedIn: Boolean,
  /** Read when the mode is picked, so a permission granted meanwhile counts. */
  private val hasUsagePermissions: () -> Boolean,
  private val startTracking: () -> Unit,
  private val startStatus: (packageName: String) -> Unit,
) {

  fun mode(): PaEPlayMode =
    if (usageTrackingEnabled && optedIn && needsUsageAccess == true && hasUsagePermissions()) {
      PaEPlayMode.TRACKING
    } else {
      PaEPlayMode.STATUS
    }

  /** Starts the Play & Earn service for [packageName] in the mode picked now. */
  fun launch(packageName: String) = when (mode()) {
    PaEPlayMode.TRACKING -> startTracking()
    PaEPlayMode.STATUS -> startStatus(packageName)
  }
}

/**
 * What the Play tap of a Play & Earn game does: a signed-in user starts earning and opens the
 * game; a logged-out one is sent to sign in. Unknown sign-in state, or a surface that cannot
 * navigate, just opens the game.
 */
class PaEPlayTap internal constructor(
  private val isSignedIn: Boolean?,
  private val requestSignIn: (() -> Unit)?,
  private val startEarning: () -> Unit,
) {

  /** Returns [open] guarded by the sign-in requirement and preceded by the service start. */
  fun guard(open: () -> Unit): () -> Unit = {
    if (isSignedIn == false && requestSignIn != null) {
      requestSignIn.invoke()
    } else {
      if (isSignedIn == true) startEarning()
      open()
    }
  }

  companion object {
    /** A tap that only opens the game (previews). */
    val OpenOnly = PaEPlayTap(isSignedIn = null, requestSignIn = null, startEarning = {})
  }
}

@HiltViewModel
class PaEPlayLauncherViewModel @Inject constructor(
  @ApplicationContext private val context: Context,
  private val playAndEarnManager: PlayAndEarnManager,
  private val missionsRepository: PaEMissionsRepository,
) : ViewModel() {

  /** null until the first value arrives. */
  val isSignedIn: StateFlow<Boolean?> = playAndEarnManager.observeIsSignedIn()
    .stateIn<Boolean?>(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

  /** Resolved at tap time, so the missions and permissions of that moment decide the mode. */
  fun launch(packageName: String) {
    viewModelScope.launch {
      // Cache first: the surface showing Play has already fetched the game's missions.
      val needsUsageAccess = missionsRepository.getCampaignMissions(packageName)
        .getOrNull()?.needsUsageAccess()
      if (needsUsageAccess == null) {
        Timber.w("P&E missions of $packageName unknown at Play: status mode")
      }
      PaEPlayLauncher(
        needsUsageAccess = needsUsageAccess,
        usageTrackingEnabled = playAndEarnManager.isUsageTrackingEnabled(),
        optedIn = playAndEarnManager.isUsageTrackingOptedIn().first(),
        hasUsagePermissions = playAndEarnManager::hasRequiredPermissions,
        startTracking = { PaEForegroundService.start(context) },
        startStatus = { PaEForegroundService.startStatusHeartbeat(context, it) },
      ).launch(packageName)
    }
  }
}

@Composable
fun rememberPaEPlayTap(
  packageName: String,
  navigate: ((String) -> Unit)?,
): PaEPlayTap = runPreviewable(
  preview = { PaEPlayTap.OpenOnly },
  real = {
    val vm = hiltViewModel<PaEPlayLauncherViewModel>()
    val isSignedIn by vm.isSignedIn.collectAsState()

    remember(isSignedIn, navigate, packageName) {
      PaEPlayTap(
        isSignedIn = isSignedIn,
        requestSignIn = navigate?.let { nav -> { nav(playAndEarnSignInRoute) } },
        startEarning = { vm.launch(packageName) },
      )
    }
  },
)
