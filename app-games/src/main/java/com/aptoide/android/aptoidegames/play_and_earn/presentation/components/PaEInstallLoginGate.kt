package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.play_and_earn.PlayAndEarnManager
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEPendingInstallHolder
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEPendingInstallHolder.PendingInstall
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in.playAndEarnInstallSignInRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Login gate for Play & Earn installs (AND-876).
 *
 * Wraps the Install/Update/Migrate actions of the Play & Earn install views: a signed-in user gets
 * the action straight away; a logged-out user is sent to the sign-in screen, and the action runs
 * by itself once they are back and signed in (see [ResumePendingPaEInstall]).
 */
@HiltViewModel
class PaEInstallLoginGateViewModel @Inject constructor(
  playAndEarnManager: PlayAndEarnManager,
  val pendingInstallHolder: PaEPendingInstallHolder,
) : ViewModel() {

  /** null until the first value arrives. */
  val isSignedIn: StateFlow<Boolean?> = playAndEarnManager.observeIsSignedIn()
    .stateIn<Boolean?>(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

  val pendingInstall: StateFlow<PendingInstall?> = pendingInstallHolder.pending
}

class PaEInstallLoginGate internal constructor(
  private val isSignedIn: Boolean?,
  private val requestLogin: (() -> Unit)?,
) {

  /** Returns [action] guarded by the login requirement. */
  fun guard(action: () -> Unit): () -> Unit = {
    // No navigation available (previews, embedded usages) or sign-in state not known yet:
    // behave as before the gate existed.
    if (requestLogin == null || isSignedIn != false) action() else requestLogin.invoke()
  }

  companion object {
    /** A gate that never asks for login. */
    val None = PaEInstallLoginGate(isSignedIn = true, requestLogin = null)
  }
}

@Composable
fun rememberPaEInstallLoginGate(
  app: App,
  navigate: ((String) -> Unit)?,
): PaEInstallLoginGate = runPreviewable(
  preview = { PaEInstallLoginGate.None },
  real = {
    val vm = hiltViewModel<PaEInstallLoginGateViewModel>()
    val isSignedIn by vm.isSignedIn.collectAsState()

    remember(isSignedIn, navigate, app.packageName, app.name) {
      PaEInstallLoginGate(
        isSignedIn = isSignedIn,
        requestLogin = navigate?.let { nav ->
          {
            vm.pendingInstallHolder.request(packageName = app.packageName, appName = app.name)
            nav(playAndEarnInstallSignInRoute)
          }
        },
      )
    }
  },
)

/**
 * Starts the install the user asked for before signing in, once: when the user is signed in, the
 * pending install is for [app], and [uiState] offers an install-like action.
 */
@Composable
fun ResumePendingPaEInstall(
  app: App,
  uiState: DownloadUiState?,
) = runPreviewable(
  preview = {},
  real = {
    val vm = hiltViewModel<PaEInstallLoginGateViewModel>()
    val isSignedIn by vm.isSignedIn.collectAsState()
    val pending by vm.pendingInstall.collectAsState()

    LaunchedEffect(isSignedIn, pending, uiState) {
      if (isSignedIn != true || pending?.packageName != app.packageName) return@LaunchedEffect
      val install = when (uiState) {
        is DownloadUiState.Install -> uiState.install
        is DownloadUiState.Outdated -> uiState.update
        is DownloadUiState.Migrate -> uiState.migrate
        is DownloadUiState.MigrateAlias -> uiState.migrateAlias
        else -> null
      } ?: return@LaunchedEffect
      if (vm.pendingInstallHolder.consume(app.packageName)) install()
    }
  },
)
