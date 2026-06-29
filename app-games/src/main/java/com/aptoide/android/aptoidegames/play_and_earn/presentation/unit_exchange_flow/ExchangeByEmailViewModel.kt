package com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeUnitsUseCase
import com.aptoide.android.aptoidegames.play_and_earn.WalletUnitsRefresher
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.toRedeemType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ExchangeEmailUiState {
  data object Idle : ExchangeEmailUiState
  data object Loading : ExchangeEmailUiState
  data object Success : ExchangeEmailUiState
  data object Error : ExchangeEmailUiState
}

@HiltViewModel
class ExchangeByEmailViewModel @Inject constructor(
  private val exchangeUnitsUseCase: ExchangeUnitsUseCase,
  private val exceptionHandler: ExceptionHandler,
  private val walletUnitsRefresher: WalletUnitsRefresher,
) : ViewModel() {

  private val _uiState = MutableStateFlow<ExchangeEmailUiState>(ExchangeEmailUiState.Idle)
  val uiState: StateFlow<ExchangeEmailUiState> = _uiState.asStateFlow()

  private var lastEmail: String? = null
  private var lastRewardType: PaERewardType? = null

  fun exchange(email: String, rewardType: PaERewardType) {
    lastEmail = email
    lastRewardType = rewardType

    viewModelScope.launch {
      _uiState.update { ExchangeEmailUiState.Loading }

      exchangeUnitsUseCase(email, rewardType.toRedeemType()).fold(
        onSuccess = {
          walletUnitsRefresher.invalidate()
          _uiState.update { ExchangeEmailUiState.Success }
        },
        onFailure = { error ->
          exceptionHandler.recordException(error)
          _uiState.update { ExchangeEmailUiState.Error }
        }
      )
    }
  }

  fun retry() {
    val email = lastEmail
    val rewardType = lastRewardType
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
}
