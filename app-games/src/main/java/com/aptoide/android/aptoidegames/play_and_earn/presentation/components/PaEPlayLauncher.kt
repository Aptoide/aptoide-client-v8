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
import cm.aptoide.pt.campaigns.domain.PaECampaignType
import cm.aptoide.pt.campaigns.domain.campaignType
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.play_and_earn.PlayAndEarnManager
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.playAndEarnPermissionsRoute
import com.aptoide.android.aptoidegames.play_and_earn.presentation.service.PaEForegroundService
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in.playAndEarnSignInRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject

/** What the Play tap of a Play & Earn game does (AND-901). */
enum class PaEPlayMode {
  /** Opens the game and watches it to measure play time; needs the usage-access permissions. */
  TRACKING,

  /** Opens the game and heartbeats for the campaign's MMP-confirmed missions (AND-879). */
  STATUS,

  /** Sends the user to the permission screens instead of opening the game. */
  REQUEST_PERMISSIONS,
}

/**
 * Picks what Play does from the campaign type (AND-915), the usage path's kill switch, and the
 * device's permissions, read when the mode is picked.
 *
 * A play-time campaign is measured by this app, so it needs the permissions: granted → tracking
 * mode, missing → the permission screens (the flow before AND-878), or just the game when the
 * surface cannot navigate. An event campaign is confirmed by the developer's MMP: status mode,
 * no permission involved. With the kill switch off every campaign behaves like an event one.
 */
class PaEPlayLauncher internal constructor(
  /** null when the missions could not be read: nothing is asked of the user then. */
  private val campaignType: PaECampaignType?,
  /** The `pae_usage_tracking_enabled` kill switch for the usage path. */
  private val usageTrackingEnabled: Boolean,
  /** Read when the mode is picked, so a permission granted meanwhile counts. */
  private val hasUsagePermissions: () -> Boolean,
  private val canRequestPermissions: Boolean,
) {

  fun mode(): PaEPlayMode = when {
    !usageTrackingEnabled -> PaEPlayMode.STATUS
    campaignType != PaECampaignType.PLAY_TIME -> PaEPlayMode.STATUS
    hasUsagePermissions() -> PaEPlayMode.TRACKING
    canRequestPermissions -> PaEPlayMode.REQUEST_PERMISSIONS
    else -> PaEPlayMode.STATUS
  }

  /** Runs the mode picked now: the service start and the game, or the permission screens. */
  fun launch(
    packageName: String,
    open: () -> Unit,
    startTracking: () -> Unit,
    startStatus: (packageName: String) -> Unit,
    requestPermissions: () -> Unit,
  ) = when (mode()) {
    PaEPlayMode.TRACKING -> {
      startTracking()
      open()
    }

    PaEPlayMode.STATUS -> {
      startStatus(packageName)
      open()
    }

    PaEPlayMode.REQUEST_PERMISSIONS -> requestPermissions()
  }
}

/**
 * The sign-in gate of the Play tap: a signed-in user plays (the launcher decides whether the
 * game opens or the permission screens do); a logged-out one is sent to sign in. Unknown sign-in
 * state, or a surface that cannot navigate, just opens the game.
 */
class PaEPlayTap internal constructor(
  private val isSignedIn: Boolean?,
  private val requestSignIn: (() -> Unit)?,
  private val play: (open: () -> Unit) -> Unit,
) {

  /** Returns [open] guarded by the sign-in requirement and routed through the launcher. */
  fun guard(open: () -> Unit): () -> Unit = {
    when {
      isSignedIn == false && requestSignIn != null -> requestSignIn.invoke()
      isSignedIn == true -> play(open)
      else -> open()
    }
  }

  companion object {
    /** A tap that only opens the game (previews). */
    val OpenOnly = PaEPlayTap(isSignedIn = null, requestSignIn = null, play = { it() })
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

  /**
   * Resolved at tap time, so the campaign and the permissions of that moment decide; [open] (or
   * [requestPermissions]) runs right after the cache read, on the main thread. The read is
   * bounded: a cold cache would otherwise fetch, and a slow fetch must not open the game or
   * navigate after the user has moved on.
   */
  fun play(packageName: String, open: () -> Unit, requestPermissions: (() -> Unit)?) {
    // One tap at a time: a second tap during the read must not open the game or push the
    // permission screens twice.
    if (inFlight?.isActive == true) return
    inFlight = viewModelScope.launch {
      // Cache first: the surface showing Play has already fetched the game's missions.
      val type = withTimeoutOrNull(MISSIONS_READ_TIMEOUT_MILLIS) {
        missionsRepository.getCampaignMissions(packageName).getOrNull()
      }?.campaignType()
      if (type == null) Timber.d("P&E campaign type of $packageName unknown at Play")
      PaEPlayLauncher(
        campaignType = type,
        usageTrackingEnabled = playAndEarnManager.isUsageTrackingEnabled(),
        hasUsagePermissions = playAndEarnManager::hasRequiredPermissions,
        canRequestPermissions = requestPermissions != null,
      ).launch(
        packageName = packageName,
        open = open,
        startTracking = { PaEForegroundService.start(context) },
        startStatus = { PaEForegroundService.startStatusHeartbeat(context, it) },
        requestPermissions = { requestPermissions?.invoke() },
      )
    }
  }

  private var inFlight: Job? = null

  private companion object {
    const val MISSIONS_READ_TIMEOUT_MILLIS = 2_000L
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
        play = { open ->
          vm.play(
            packageName = packageName,
            open = open,
            requestPermissions = navigate?.let { nav -> { nav(playAndEarnPermissionsRoute) } },
          )
        },
      )
    }
  },
)
