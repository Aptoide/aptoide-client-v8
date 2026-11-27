package com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.aptoide_network.play_and_earn.reportUnlessPaERefusal
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeUnitsUseCase
import com.aptoide.android.aptoidegames.play_and_earn.WalletUnitsRefresher
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPendingRedeems
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.toRedeemType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

sealed interface ExchangeEmailUiState {
  data object Idle : ExchangeEmailUiState
  data object Loading : ExchangeEmailUiState
  data object Success : ExchangeEmailUiState
  data class Error(val kind: ExchangeErrorKind) : ExchangeEmailUiState
}

/**
 * What the user is told when a redeem fails (AND-952).
 * @param canRetry retrying can help: the others are final answers, and retrying a redeem whose
 * outcome is being checked would only put it under review.
 */
enum class ExchangeErrorKind(val canRetry: Boolean) {
  Generic(canRetry = true),
  Limit(canRetry = false),
  TooManyAttempts(canRetry = false),
  BeingChecked(canRetry = false),
  Restricted(canRetry = false),
}

/**
 * Redeems units for a code sent by email. Each redeem carries a request id (AND-952), kept by
 * [PaEPendingRedeems] until the answer is final: the backend answers a repeated id once, so
 * sending a redeem again after a lost answer, even from a new screen, never redeems twice. The
 * email and reward of the last redeem stay in [SavedStateHandle] for [retry].
 */
@HiltViewModel
class ExchangeByEmailViewModel @Inject constructor(
  private val exchangeUnitsUseCase: ExchangeUnitsUseCase,
  private val exceptionHandler: ExceptionHandler,
  private val walletUnitsRefresher: WalletUnitsRefresher,
  private val pendingRedeems: PaEPendingRedeems,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

  private val _uiState = MutableStateFlow<ExchangeEmailUiState>(ExchangeEmailUiState.Idle)
  val uiState: StateFlow<ExchangeEmailUiState> = _uiState.asStateFlow()

  fun exchange(email: String, rewardType: PaERewardType) {
    // One request at a time: a second tap must not send the redeem twice.
    if (_uiState.value == ExchangeEmailUiState.Loading) return
    _uiState.update { ExchangeEmailUiState.Loading }
    savedStateHandle[KEY_EMAIL] = email
    savedStateHandle[KEY_REWARD_TYPE] = rewardType.name

    viewModelScope.launch { redeem(email, rewardType) }
  }

  fun retry() {
    val email = savedStateHandle.get<String>(KEY_EMAIL)
    val rewardType = savedStateHandle.get<String>(KEY_REWARD_TYPE)
      ?.let { runCatching { PaERewardType.valueOf(it) }.getOrNull() }
    if (email != null && rewardType != null) {
      exchange(email, rewardType)
    } else {
      _uiState.update { ExchangeEmailUiState.Idle }
    }
  }

  // Clears the success state once the navigation to the success screen has been handled,
  // so returning to this screen doesn't re-trigger the navigation.
  fun resetState() {
    _uiState.update { ExchangeEmailUiState.Idle }
  }

  private suspend fun redeem(email: String, rewardType: PaERewardType) {
    val requestId = try {
      pendingRedeems.idFor(email, rewardType)
    } catch (e: IOException) {
      // The preferences could not be read or written: no id is safe to send.
      exceptionHandler.recordException(e)
      _uiState.update { ExchangeEmailUiState.Error(ExchangeErrorKind.Generic) }
      return
    }

    exchangeUnitsUseCase(email, rewardType.toRedeemType(), requestId).fold(
      onSuccess = {
        pendingRedeems.clear(requestId)
        walletUnitsRefresher.invalidate()
        _uiState.update { ExchangeEmailUiState.Success }
      },
      onFailure = { error ->
        // AND-950: a refusal is expected: logged, not reported as a crash.
        val refusal = reportUnlessPaERefusal(error, exceptionHandler::recordException)
        if (refusal.endsRequest()) pendingRedeems.clear(requestId)
        _uiState.update { ExchangeEmailUiState.Error(refusal.toErrorKind()) }
      }
    )
  }

  private companion object {
    const val KEY_EMAIL = "exchange_email"
    const val KEY_REWARD_TYPE = "exchange_reward_type"
  }
}

/**
 * Whether the redeem's id is done with: the backend refused it for good without taking it, or
 * said the id belongs to other data. Any other failure may have redeemed (or still be
 * redeeming), so the id is kept and a retry replays it.
 */
private fun PaERefusal?.endsRequest(): Boolean = when (this?.code) {
  PaERefusalCode.REDEEM_LIMIT,
  PaERefusalCode.TOO_MANY_ATTEMPTS,
  PaERefusalCode.ACCOUNT_RESTRICTED,
  PaERefusalCode.REDEEM_REQUEST_MISMATCH -> true

  else -> false
}

/**
 * In progress and mismatch read as a generic error: Retry is the fix, with the same id while in
 * progress and a new one after a mismatch.
 */
private fun PaERefusal?.toErrorKind(): ExchangeErrorKind = when (this?.code) {
  PaERefusalCode.REDEEM_LIMIT -> ExchangeErrorKind.Limit
  PaERefusalCode.TOO_MANY_ATTEMPTS -> ExchangeErrorKind.TooManyAttempts
  PaERefusalCode.REDEEM_OUTCOME_UNKNOWN,
  PaERefusalCode.REDEEM_UNDER_REVIEW -> ExchangeErrorKind.BeingChecked

  PaERefusalCode.ACCOUNT_RESTRICTED -> ExchangeErrorKind.Restricted
  else -> ExchangeErrorKind.Generic
}
