package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import com.aptoide.android.aptoidegames.R

/**
 * Says why the sign-in bonus the user just claimed was refused (AND-953), as the offer ends at
 * once. Shown only right after the user's own claim; a bonus found refused later says nothing.
 */
@Composable
fun SignInBonusRefusedSnack(showSnack: (String) -> Unit) {
  val viewModel = hiltViewModel<SignInRewardViewModel>()
  val context = LocalContext.current
  LaunchedEffect(viewModel) {
    viewModel.refusedEvent.collect { code ->
      signInBonusRefusedMessage(code)?.let { showSnack(context.getString(it)) }
    }
  }
}

/**
 * The text for a refused sign-in bonus, or null for a code that does not end it. A VPN (AND-964)
 * does not end it either, but the user must know to turn it off before tapping again.
 */
@StringRes
internal fun signInBonusRefusedMessage(code: PaERefusalCode): Int? = when (code) {
  PaERefusalCode.ACCOUNT_RESTRICTED -> R.string.play_and_earn_exchange_error_restricted_body
  PaERefusalCode.REGION_NOT_SUPPORTED -> R.string.play_and_earn_refused_region_body
  PaERefusalCode.DEVICE_NOT_SUPPORTED -> R.string.play_and_earn_refused_device_body
  PaERefusalCode.UPDATE_REQUIRED -> R.string.play_and_earn_refused_update_body
  PaERefusalCode.VPN_NOT_ALLOWED -> R.string.play_and_earn_refused_vpn_body
  PaERefusalCode.SIGN_IN_BONUS_DEVICE_LIMIT ->
    R.string.play_and_earn_refused_bonus_device_limit_body

  PaERefusalCode.REDEEM_LIMIT,
  PaERefusalCode.INSTALL_LIMIT,
  PaERefusalCode.TOO_MANY_ATTEMPTS,
  PaERefusalCode.REDEEM_IN_PROGRESS,
  PaERefusalCode.REDEEM_UNDER_REVIEW,
  PaERefusalCode.REDEEM_REQUEST_MISMATCH,
  PaERefusalCode.REDEEM_OUTCOME_UNKNOWN -> null
}
