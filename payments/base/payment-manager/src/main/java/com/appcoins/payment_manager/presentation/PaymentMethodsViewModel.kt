package com.appcoins.payment_manager.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcoins.payment_manager.manager.PaymentManager
import com.appcoins.payment_manager.presentation.PaymentMethodsUiState.Loading
import com.appcoins.payment_manager.presentation.PaymentMethodsUiState.LoadingSkeleton
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
        val lastPaymentId =
          preSelectedPaymentUseCase.getLastSuccessfulPaymentMethod()

        if (lastPaymentId == null) {
          viewModelState.update { LoadingSkeleton }

          val paymentMethods = paymentManager.loadPaymentMethods(purchaseRequest)
          viewModelState.update {
            PaymentMethodsUiState.Idle(paymentMethods = paymentMethods)
          }
        } else {
          val paymentMethods = paymentManager.loadPaymentMethods(purchaseRequest)
          val lastPaymentMethod = paymentMethods.find { it.id == lastPaymentId }
          if (lastPaymentMethod != null) {
            viewModelState.update {
              PaymentMethodsUiState.PreSelected(lastPaymentMethod, paymentMethods)
            }
          } else {
            viewModelState.update {
              PaymentMethodsUiState.Idle(paymentMethods)
            }
          }
        }
      } catch (e: Throwable) {
        e.printStackTrace()
        if (e is IOException) {
          viewModelState.update { PaymentMethodsUiState.NoConnection }
        } else {
          viewModelState.update { PaymentMethodsUiState.Error }
        }
      }
    }
  }

  fun onPreSelectedShown() {
    viewModelState.update { state ->
      (state as? PaymentMethodsUiState.PreSelected)?.let {
        PaymentMethodsUiState.Idle(it.paymentMethods)
      } ?: state
    }
  }
}
