package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.play_and_earn.domain.mmp.PaEMmpClickSender
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaEMmpClickViewModel @Inject constructor(
  val sender: PaEMmpClickSender,
) : ViewModel()

/**
 * Sends the developer's MMP click when a fresh install of a Play & Earn game starts (AND-877).
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

  /** Wraps [onInstallStarted] so a fresh install also sends the MMP click. */
  fun onInstallStarted(onInstallStarted: () -> Unit): () -> Unit = {
    if (uiState is DownloadUiState.Install) send(packageName)
    onInstallStarted()
  }
}

@Composable
fun rememberPaEMmpInstallClick(packageName: String): PaEMmpInstallClick = runPreviewable(
  preview = { remember(packageName) { PaEMmpInstallClick(packageName) {} } },
  real = {
    val vm = hiltViewModel<PaEMmpClickViewModel>()
    remember(packageName) { PaEMmpInstallClick(packageName, vm.sender::send) }
  },
)

@Composable
fun TrackPaEMmpInstallState(
  mmpInstallClick: PaEMmpInstallClick,
  uiState: DownloadUiState?,
) = SideEffect {
  mmpInstallClick.uiState = uiState
}
