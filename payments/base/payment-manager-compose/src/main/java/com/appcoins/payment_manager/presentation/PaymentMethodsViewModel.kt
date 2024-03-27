package com.appcoins.payment_manager.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcoins.payment_manager.manager.PaymentManager
import com.appcoins.payment_manager.presentation.PaymentMethodsUiState.Error
import com.appcoins.payment_manager.presentation.PaymentMethodsUiState.Idle
import com.appcoins.payment_manager.presentation.PaymentMethodsUiState.Loading
import com.appcoins.payment_manager.presentation.PaymentMethodsUiState.LoadingSkeleton
import com.appcoins.payment_manager.presentation.PaymentMethodsUiState.NoConnection
import com.appcoins.payment_manager.presentation.PaymentMethodsUiState.PreSelected
import com.appcoins.payment_prefs.domain.PreSelectedPaymentUseCase
import com.appcoins.payments.arch.PurchaseRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class PaymentMethodsViewModel(
  private val purchaseRequest: PurchaseRequest,
  private val paymentManager: PaymentManager,
  private val preSelectedPaymentUseCase: PreSelectedPaymentUseCase,
) : ViewModel() {

  private val viewModelState =
    MutableStateFlow<PaymentMethodsUiState>(Loading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    reload()
  }

  fun reload() {
    viewModelScope.launch {
      try {
        val lastPaymentMethodId = preSelectedPaymentUseCase.getLastSuccessfulPaymentMethod()
        if (lastPaymentMethodId == null) viewModelState.update { LoadingSkeleton }
        val paymentMethods = paymentManager.loadPaymentMethods(purchaseRequest)
        val lastPaymentMethod = paymentMethods.find { it.id == lastPaymentMethodId }

        if (lastPaymentMethod != null) {
          viewModelState.update { PreSelected(lastPaymentMethod, paymentMethods) }
        } else {
          viewModelState.update { Idle(paymentMethods) }
        }
      } catch (e: Throwable) {
        e.printStackTrace()
        if (e is IOException) {
          viewModelState.update { NoConnection }
        } else {
          viewModelState.update { Error }
        }
      }
    }
  }

  fun onPreSelectedShown() {
    viewModelState.update { state ->
      (state as? PreSelected)?.let {
        Idle(it.paymentMethods)
      } ?: state
    }
  }
}
