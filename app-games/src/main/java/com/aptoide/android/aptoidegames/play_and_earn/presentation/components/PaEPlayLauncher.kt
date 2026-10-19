package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.campaigns.data.PaEMissionsRepository
import cm.aptoide.pt.campaigns.domain.needsUsageAccess
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.play_and_earn.PlayAndEarnManager
import com.aptoide.android.aptoidegames.play_and_earn.presentation.service.PaEForegroundService
import dagger.hilt.android.lifecycle.HiltViewModel
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
 * the permissions and the usage path is not switched off remotely; status mode otherwise. Play
 * never asks for permissions: the locked time missions do (AND-902/903).
 */
class PaEPlayLauncher internal constructor(
  /** Whether the game's missions need usage access; null when they could not be read. */
  private val needsUsageAccess: Boolean?,
  /** The `pae_usage_tracking_enabled` kill switch for the usage path. */
  private val usageTrackingEnabled: Boolean,
  /** Read when the mode is picked, so a permission granted meanwhile counts. */
  private val hasUsagePermissions: () -> Boolean,
) {

  fun mode(): PaEPlayMode =
    if (usageTrackingEnabled && needsUsageAccess == true && hasUsagePermissions()) {
      PaEPlayMode.TRACKING
    } else {
      PaEPlayMode.STATUS
    }

  /** Starts the Play & Earn service for [packageName] in the mode picked now. */
  fun launch(context: Context, packageName: String) = when (mode()) {
    PaEPlayMode.TRACKING -> PaEForegroundService.start(context)
    PaEPlayMode.STATUS -> PaEForegroundService.startStatusHeartbeat(context, packageName)
  }

  companion object {
    /** A launcher that always picks status mode (previews). */
    val Status = PaEPlayLauncher(
      needsUsageAccess = false,
      usageTrackingEnabled = false,
      hasUsagePermissions = { false },
    )
  }
}

@HiltViewModel
class PaEPlayLauncherViewModel @Inject constructor(
  val playAndEarnManager: PlayAndEarnManager,
  val missionsRepository: PaEMissionsRepository,
) : ViewModel()

@Composable
fun rememberPaEPlayLauncher(packageName: String): PaEPlayLauncher = runPreviewable(
  preview = { PaEPlayLauncher.Status },
  real = {
    val vm = hiltViewModel<PaEPlayLauncherViewModel>()
    val usageTrackingEnabled by vm.playAndEarnManager.observeUsageTrackingEnabled().collectAsState()
    // Cache first: the surface showing Play has already fetched the game's missions.
    val needsUsageAccess by produceState<Boolean?>(initialValue = null, packageName) {
      value = vm.missionsRepository.getCampaignMissions(packageName, forceRefresh = false)
        .getOrNull()?.needsUsageAccess()
    }

    remember(needsUsageAccess, usageTrackingEnabled) {
      PaEPlayLauncher(
        needsUsageAccess = needsUsageAccess,
        usageTrackingEnabled = usageTrackingEnabled,
        hasUsagePermissions = vm.playAndEarnManager::hasRequiredPermissions,
      )
    }
  },
)
