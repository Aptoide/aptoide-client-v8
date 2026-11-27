package com.aptoide.android.aptoidegames.play_and_earn.presentation.restricted

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEAccountRestriction
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEAccountRestrictionState
import com.aptoide.android.aptoidegames.theme.Palette
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** What a Play & Earn screen shows for what is known about the account (AND-951). */
enum class PaERestrictedGateState {
  /** Not known yet: the screen's own content, unless the screen holds it until known. */
  LOADING,
  RESTRICTED,
  OPEN,
}

/**
 * @param holdWhileLoading the screen must not show its content before the answer (it would act
 * on it, e.g. claim a bonus): it shows a neutral loading state instead.
 */
fun PaERestrictedGateState.showsContent(holdWhileLoading: Boolean): Boolean = when (this) {
  PaERestrictedGateState.LOADING -> !holdWhileLoading
  PaERestrictedGateState.RESTRICTED -> false
  PaERestrictedGateState.OPEN -> true
}

/**
 * @param settled something was heard about the current account ([PaEAccountRestriction.settled]).
 * @param signedIn null until the stored sign-in is read.
 */
fun paERestrictedGateStateOf(
  restriction: PaEAccountRestrictionState,
  settled: Boolean,
  signedIn: Boolean?,
): PaERestrictedGateState = when {
  signedIn == null -> PaERestrictedGateState.LOADING
  // The restricted screen is a signed-in user's only, whatever is still remembered.
  !signedIn -> PaERestrictedGateState.OPEN
  restriction == PaEAccountRestrictionState.RESTRICTED -> PaERestrictedGateState.RESTRICTED
  restriction == PaEAccountRestrictionState.NOT_RESTRICTED -> PaERestrictedGateState.OPEN
  // Asked, and the backend could not tell: the screen is not held for ever.
  settled -> PaERestrictedGateState.OPEN
  else -> PaERestrictedGateState.LOADING
}

/**
 * While the restricted screen is shown, so that a lifted (or expired) ban lets the user back in
 * where they are: calls [refresh] each time the app comes to the front ([resumed] turns true),
 * and every [intervalMillis] while it stays there. [refresh] is throttled on its own.
 */
suspend fun recheckWhileShown(
  resumed: Flow<Boolean>,
  intervalMillis: Long,
  refresh: () -> Unit,
) {
  resumed.distinctUntilChanged().collectLatest { isResumed ->
    while (isResumed) {
      refresh()
      delay(intervalMillis)
    }
  }
}

@HiltViewModel
class PaEAccountRestrictionViewModel @Inject constructor(
  private val accountRestriction: PaEAccountRestriction,
  walletCoreDataSource: WalletCoreDataSource,
) : ViewModel() {

  private val signedIn: Flow<Boolean?> = walletCoreDataSource.observeCurrentWalletAddress()
    .map<String?, Boolean?> { it != null }
    // A wallet that cannot be read is no one signed in.
    .catch { emit(false) }
    .onStart { emit(null) }

  val gateState: StateFlow<PaERestrictedGateState> = combine(
    accountRestriction.state,
    accountRestriction.settled,
    signedIn,
    ::paERestrictedGateStateOf,
  ).stateIn(viewModelScope, SharingStarted.Eagerly, PaERestrictedGateState.LOADING)

  fun refresh() = accountRestriction.refresh()
}

/**
 * What a Play & Earn screen shows for the signed-in account's restriction (AND-951). Opening the
 * surface asks the backend, and so does a change that leaves it loading (e.g. a sign-in), at
 * most once a minute across every surface.
 */
@Composable
fun rememberPaERestrictedGateState(): PaERestrictedGateState = runPreviewable(
  preview = { PaERestrictedGateState.OPEN },
  real = {
    val viewModel = hiltViewModel<PaEAccountRestrictionViewModel>()
    val gateState by viewModel.gateState.collectAsState()
    LaunchedEffect(Unit) { viewModel.refresh() }
    LaunchedEffect(gateState) {
      if (gateState == PaERestrictedGateState.LOADING) viewModel.refresh()
    }
    if (gateState == PaERestrictedGateState.RESTRICTED) {
      val lifecycle = LocalLifecycleOwner.current.lifecycle
      LaunchedEffect(lifecycle) {
        recheckWhileShown(
          resumed = lifecycle.currentStateFlow.map { it.isAtLeast(Lifecycle.State.RESUMED) },
          intervalMillis = PaEAccountRestriction.REFRESH_INTERVAL_MS,
          refresh = viewModel::refresh,
        )
      }
    }
    gateState
  }
)

/**
 * The one entry to every Play & Earn screen for a restricted account (AND-951): shows
 * [PaERestrictedView] instead of [content] while the backend says the signed-in account is
 * restricted for fraud, and [content] otherwise, including while that is not known yet.
 *
 * @param holdWhileLoading show a neutral loading state, not [content], until the answer is known
 * (or the backend could not tell): for a screen whose content acts on the account at once.
 * @param fullBleed the screen draws behind the system bars (no top bar of the app's).
 * @param scrollable false when the screen around it already scrolls (the game's Rewards tab).
 * @param navigateBack draws a back arrow on the restricted screen: for a screen that drew its
 * own, or has none. System back works either way.
 */
@Composable
fun PaERestrictedGate(
  holdWhileLoading: Boolean = false,
  fullBleed: Boolean = false,
  scrollable: Boolean = true,
  navigateBack: (() -> Unit)? = null,
  content: @Composable () -> Unit,
) {
  val gateState = rememberPaERestrictedGateState()
  when {
    gateState.showsContent(holdWhileLoading) -> content()
    gateState == PaERestrictedGateState.RESTRICTED -> PaERestrictedView(
      fullBleed = fullBleed,
      scrollable = scrollable,
      navigateBack = navigateBack,
    )

    else -> Box(modifier = Modifier.fillMaxSize().background(Palette.Black)) { LoadingView() }
  }
}
