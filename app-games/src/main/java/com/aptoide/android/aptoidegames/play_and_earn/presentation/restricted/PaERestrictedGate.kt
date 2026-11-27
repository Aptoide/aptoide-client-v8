package com.aptoide.android.aptoidegames.play_and_earn.presentation.restricted

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEAccountRestriction
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEAccountRestrictionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** What a Play & Earn screen shows for what is known about the account (AND-951). */
enum class PaERestrictedGateState {
  /** Not known yet: the screen's own content, so nothing blank nor restricted flashes. */
  LOADING,
  RESTRICTED,
  OPEN,
}

val PaERestrictedGateState.showsContent: Boolean
  get() = this != PaERestrictedGateState.RESTRICTED

fun PaEAccountRestrictionState.toGateState(): PaERestrictedGateState = when (this) {
  PaEAccountRestrictionState.UNKNOWN -> PaERestrictedGateState.LOADING
  PaEAccountRestrictionState.RESTRICTED -> PaERestrictedGateState.RESTRICTED
  PaEAccountRestrictionState.NOT_RESTRICTED -> PaERestrictedGateState.OPEN
}

@HiltViewModel
class PaEAccountRestrictionViewModel @Inject constructor(
  private val accountRestriction: PaEAccountRestriction,
) : ViewModel() {

  val state: StateFlow<PaEAccountRestrictionState> = accountRestriction.state

  fun refresh() = accountRestriction.refresh()
}

/**
 * What is known about the signed-in account's restriction (AND-951). Opening the surface asks
 * the backend again, at most once a minute across every surface.
 */
@Composable
fun rememberPaEAccountRestriction(): PaEAccountRestrictionState = runPreviewable(
  preview = { PaEAccountRestrictionState.NOT_RESTRICTED },
  real = {
    val viewModel = hiltViewModel<PaEAccountRestrictionViewModel>()
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.refresh() }
    state
  }
)

/**
 * The one entry to every Play & Earn screen for a restricted account (AND-951): shows
 * [PaERestrictedView] instead of [content] while the backend says the account is restricted for
 * fraud, and [content] otherwise, including while that is not known yet.
 *
 * @param fullBleed the screen draws behind the system bars (no top bar): the restricted screen
 * keeps its text clear of them.
 */
@Composable
fun PaERestrictedGate(
  fullBleed: Boolean = false,
  content: @Composable () -> Unit,
) {
  if (rememberPaEAccountRestriction().toGateState().showsContent) {
    content()
  } else {
    PaERestrictedView(fullBleed = fullBleed)
  }
}
