package com.aptoide.android.aptoidegames.play_and_earn.presentation.app_view

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.PaESessionRefusals
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** The text the game's Rewards tab shows instead of its missions after a refusal (AND-950). */
data class PaERefusalMessage(
  @StringRes val title: Int,
  @StringRes val body: Int,
)

/**
 * Each refusal a session start can get has its own text; the others (e.g. ACCOUNT_RESTRICTED,
 * whose own screen is AND-951) keep the generic error.
 */
fun PaERefusal.toMessage(): PaERefusalMessage = when (code) {
  PaERefusalCode.REGION_NOT_SUPPORTED -> PaERefusalMessage(
    title = R.string.play_and_earn_refused_title,
    body = R.string.play_and_earn_refused_region_body,
  )

  PaERefusalCode.DEVICE_NOT_SUPPORTED -> PaERefusalMessage(
    title = R.string.play_and_earn_refused_title,
    body = R.string.play_and_earn_refused_device_body,
  )

  PaERefusalCode.UPDATE_REQUIRED -> PaERefusalMessage(
    title = R.string.play_and_earn_refused_title,
    body = R.string.play_and_earn_refused_update_body,
  )

  PaERefusalCode.VPN_NOT_ALLOWED -> PaERefusalMessage(
    title = R.string.play_and_earn_refused_title,
    body = R.string.play_and_earn_refused_vpn_body,
  )

  PaERefusalCode.ACCOUNT_RESTRICTED,
  PaERefusalCode.SIGN_IN_BONUS_DEVICE_LIMIT,
  PaERefusalCode.REDEEM_LIMIT,
  PaERefusalCode.INSTALL_LIMIT,
  PaERefusalCode.TOO_MANY_ATTEMPTS,
  PaERefusalCode.REDEEM_IN_PROGRESS,
  PaERefusalCode.REDEEM_UNDER_REVIEW,
  PaERefusalCode.REDEEM_REQUEST_MISMATCH,
  PaERefusalCode.REDEEM_OUTCOME_UNKNOWN -> PaERefusalMessage(
    title = R.string.error_message_generic_title,
    body = R.string.error_message_generic_body,
  )
}

/** The refusal of [packageName]'s last session start, as a message; null when there is none. */
class PaESessionRefusalViewModel(
  packageName: String,
  paESessionRefusals: PaESessionRefusals,
) : ViewModel() {

  val uiState: StateFlow<PaERefusalMessage?> = paESessionRefusals.observe(packageName)
    .map { it?.toMessage() }
    .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}

@HiltViewModel
class PaESessionRefusalsProvider @Inject constructor(
  val paESessionRefusals: PaESessionRefusals,
) : ViewModel()

@Composable
fun rememberPaESessionRefusal(packageName: String): PaERefusalMessage? = runPreviewable(
  preview = { null },
  real = {
    val provider = hiltViewModel<PaESessionRefusalsProvider>()
    val vm: PaESessionRefusalViewModel = viewModel(
      viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner,
      key = "paeSessionRefusal/$packageName",
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return PaESessionRefusalViewModel(
            packageName = packageName,
            paESessionRefusals = provider.paESessionRefusals,
          ) as T
        }
      }
    )
    val uiState by vm.uiState.collectAsState()
    uiState
  }
)
