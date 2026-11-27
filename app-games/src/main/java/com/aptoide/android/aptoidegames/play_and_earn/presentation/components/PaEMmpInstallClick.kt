package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.play_and_earn.presentation.installs.PaEInstallStartedViewModel

/**
 * Sends the developer's MMP click when a fresh install of a Play & Earn game starts (AND-877),
 * with the backend's install click and the same click id (AND-954).
 *
 * Updates and migrations don't send it: the game is already on the device, so the MMP can't credit
 * a new install to us.
 */
class PaEMmpInstallClick internal constructor(
  private val packageName: String,
  private val send: (packageName: String) -> Unit,
) {

  /** The install view state the user acted on; kept current by [TrackPaEMmpInstallState]. */
  internal var uiState: DownloadUiState? = null

  /** Wraps [onInstallStarted] so a fresh install also sends the clicks. */
  fun onInstallStarted(onInstallStarted: () -> Unit): () -> Unit = {
    if (uiState is DownloadUiState.Install) send(packageName)
    onInstallStarted()
  }
}

@Composable
fun rememberPaEMmpInstallClick(packageName: String): PaEMmpInstallClick = runPreviewable(
  preview = { remember(packageName) { PaEMmpInstallClick(packageName) {} } },
  real = {
    val vm = hiltViewModel<PaEInstallStartedViewModel>()
    remember(packageName) {
      PaEMmpInstallClick(packageName, vm.installTracker::onPaEViewInstallStarted)
    }
  },
)

@Composable
fun TrackPaEMmpInstallState(
  mmpInstallClick: PaEMmpInstallClick,
  uiState: DownloadUiState?,
) = SideEffect {
  mmpInstallClick.uiState = uiState
}
